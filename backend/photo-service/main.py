import asyncio
from contextlib import asynccontextmanager
from fastapi import FastAPI
from py_eureka_client.eureka_client import EurekaClient
from config.config import settings
from controller.PhotoController import router
import uvicorn

eureka_client = EurekaClient(**settings.EUREKA_SERVER_config)

@asynccontextmanager
async def lifespan(app: FastAPI):
    asyncio.create_task(eureka_client.start())
    yield
app = FastAPI(lifespan=lifespan)
app.include_router(router)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8086)