import logging
import uuid
from config.config import settings
from services.S3ClientService import S3Client

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Прослойка для сохранения в хранилище именно с username и прочим

class UserPhotoService:
    _s3_clients = {}

    def __init__(self, bucket_name: str = settings.PHOTO_BUCKET):
        self.bucket_name = bucket_name
        if bucket_name not in self._s3_clients:
            self._s3_clients[bucket_name] = S3Client(
                **settings.S3_connect_config,
                bucket_name=bucket_name
            )
        self.s3_client = self._s3_clients[bucket_name]

    def upload_user_photo(self, content: bytes, user_name: str, file_name: str) -> str:
        unique_name = f"{uuid.uuid4().hex}_{file_name}"
        object_path = f"{user_name}/{unique_name}"

        if self.s3_client.upload_file(content, object_path):
            return f"{settings.ENDPOINT_URL}/{settings.PHOTO_BUCKET}/{object_path}"
        return ""

    def delete_user_photo(self, object_key: str):
        try:
            self.s3_client.delete_file(object_key)
            return True
        except Exception as e:
            logger.error(f"Error deleting file {object_key}: {e}")
            return None
    def get_user_photo(self, user_name):
        pass
