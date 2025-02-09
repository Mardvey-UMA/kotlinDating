from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    # app
    APP_NAME: str
    APP_HOST: str
    SERVER_PORT: int

    # база данных
    DB_HOST: str
    DB_PORT: int
    DB_USER: str
    DB_PASS: str
    DB_NAME: str
    DB_HOST_DOCKER: str

    # определение лица фото
    MODEL_PATH: str

    # S3 хранилище
    AWS_ACCESS_KEY_ID: str
    AWS_SECRET_ACCESS_KEY: str
    REGION: str
    ENDPOINT_URL: str
    PHOTO_BUCKET: str

    # еврейка сервер конфигурация
    EUREKA_HOST: str
    EUREKA_PORT: int
    EUREKA_NAME: str

    @property
    def DATABASE_URL_asyncpg_docker(self):
        return f"postgresql+asyncpg://{self.DB_USER}:{self.DB_PASS}@{self.DB_HOST_DOCKER}/{self.DB_NAME}"

    @property
    def EUREKA_SERVER_config(self):
        eureka_url = f"http://{self.EUREKA_HOST}:{self.EUREKA_PORT}/{self.EUREKA_NAME}"
        return {
            "app_name": self.APP_NAME,
            "eureka_server": eureka_url,
            "instance_host": self.APP_HOST,
            "instance_port": self.SERVER_PORT,
            "eureka_protocol": "http",
            "should_register": True,
            "should_discover": True,
        }

    @property
    def S3_connect_config(self):
        return {
            "aws_access_key_id": self.AWS_ACCESS_KEY_ID,
            "aws_secret_access_key": self.AWS_SECRET_ACCESS_KEY,
            "endpoint_url": self.ENDPOINT_URL,
            "region_name": self.REGION
            }

    model_config = SettingsConfigDict(env_file=".env")

settings = Settings()