import logging
from contextlib import contextmanager
from botocore.exceptions import NoCredentialsError, EndpointConnectionError
from botocore.session import get_session
import magic

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Сохранение в S3 хранилище

class S3Client:
    _instances = {}

    def __new__(cls, **kwargs):
        key = (
            kwargs.get('bucket_name'),
            kwargs.get('region_name'),
            kwargs.get('endpoint_url')
        )
        if key not in cls._instances:
            instance = super().__new__(cls)
            instance.__init__(**kwargs)
            cls._instances[key] = instance
        return cls._instances[key]

    def __init__(self, **config):
        if not hasattr(self, '_initialized'):
            required_keys = {'aws_access_key_id', 'aws_secret_access_key', 'bucket_name'}
            if not required_keys.issubset(config):
                missing = required_keys - config.keys()
                raise ValueError(f"Missing required configuration keys: {missing}")

            self.config = {
                'aws_access_key_id': config['aws_access_key_id'],
                'aws_secret_access_key': config['aws_secret_access_key'],
                'endpoint_url': config.get('endpoint_url'),
                'region_name': config.get('region_name')
            }
            self.bucket_name = config['bucket_name']
            self.session = get_session()
            self.magic_instance = magic.Magic(mime=True)
            self._initialized = True

    @contextmanager
    def get_client(self):
        try:
            client = self.session.create_client("s3", **self.config)
            yield client
        except (NoCredentialsError, EndpointConnectionError) as e:
            logger.error(f"Connection error: {e}")
            raise
        finally:
            pass

    def upload_file(self, content: bytes, object_name: str) -> bool:
        try:
            mimetype = self.magic_instance.from_buffer(content)
            with self.get_client() as client:
                client.put_object(
                    Bucket=self.bucket_name,
                    Key=object_name,
                    Body=content,
                    ContentType=mimetype
                )
            logger.info(f"Successfully uploaded {object_name}")
            return True
        except Exception as e:
            logger.error(f"Upload failed for {object_name}: {e}")
            raise

    def delete_file(self, object_name: str):
        try:
            with self.get_client() as client:
                client.delete_object(
                    Bucket=self.bucket_name,
                    Key=object_name
                )
            logger.info(f"File {object_name} deleted from {self.bucket_name}")
        except Exception as e:
            logger.error(f"Error deleting file {object_name}: {e}")
            raise
