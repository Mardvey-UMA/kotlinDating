import cv2
import numpy as np
from ultralytics import YOLO
import logging
import threading
from config.config import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class FaceDetectorService:
    _model = None
    _model_lock = threading.Lock()

    def __init__(self):
        self._initialize_model()

    def _initialize_model(self):
        with self._model_lock:
            if self._model is None:
                try:
                    model_path = settings.MODEL_PATH
                    if not model_path:
                        raise ValueError("MODEL_PATH not configured")

                    self._model = YOLO(model_path).to("cpu")
                    logger.info("YOLO model loaded successfully")

                except Exception as e:
                    logger.error(f"Model loading failed: {str(e)}")
                    raise

    def detect_face(self, image_data):
        try:
            image = self._load_image(image_data)
            results = self._model(image)
            return bool(results and len(results[0].boxes) > 0)
        except Exception as e:
            logger.error(f"Error during face detection: {e}")
            return False

    @staticmethod
    def _load_image(image_data):
        nparr = np.frombuffer(image_data, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        if image is None:
            raise ValueError("Error while image decode")
        return image