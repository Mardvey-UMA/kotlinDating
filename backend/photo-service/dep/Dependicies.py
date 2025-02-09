from fastapi import HTTPException, Request

from services.PhotoService import PhotoService


def get_user_name(request: Request):
    user_name = request.headers.get("X-User-Name")
    if not user_name:
        raise HTTPException(status_code=400, detail="X-User-Name header is missing")
    return user_name

async def get_photo_service():
    yield PhotoService()