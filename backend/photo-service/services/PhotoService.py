from datetime import datetime
from typing import Any

from fastapi import UploadFile

from database.database import Database
from dto.DeletePhotoRequestDTO import DeletePhotoRequestDTO
from dto.DeletePhotoResponseDTO import DeletePhotoResponseDTO
from dto.GetPhotosResponseDTO import GetPhotosResponseDTO
from dto.UploadPhotoResponseDTO import UploadPhotoResponseDTO
from services.FaceDetectorService import FaceDetectorService
from services.PhotoORM import PhotoOrmService
from services.S3ClientService import S3Client
from services.S3HadnlingPhotoService import UserPhotoService
import magic

class PhotoService:
    def __init__(self):
        self.mime_detector = magic.Magic(mime=True)
        self.face_detector = FaceDetectorService()
        self.user_photo_service = UserPhotoService()
        self.photo_orm_service = PhotoOrmService(Database.get_session_factory())

    async def get_file_type(self, file: UploadFile) -> str:
        file_bytes = await file.read(1024)
        await file.seek(0)
        return self.mime_detector.from_buffer(file_bytes)

    async def handle_photo(self, file: UploadFile, username: str) -> UploadPhotoResponseDTO:
        try:
            file_content = await file.read()
            await file.seek(0)

            mimetype = await self.get_file_type(file)

            if not self.face_detector.detect_face(file_content):
                return UploadPhotoResponseDTO(
                    result=False,
                    message="No face detected",
                    photo_url="none",
                    object_key="none"
                )

            photo_url = self.user_photo_service.upload_user_photo(
                content=file_content,
                user_name=username,
                file_name=file.filename
            )

            object_key = "/".join(photo_url.rstrip("/").rsplit("/", 2)[-2:])

            await self.photo_orm_service.add_photo(
                photo_url=photo_url,
                email=username,
                object_key=object_key,
                mimetype=mimetype
            )

            return UploadPhotoResponseDTO(
                result=True,
                message="Uploaded photo successfully",
                photo_url=photo_url,
                object_key=object_key
            )

        except Exception as e:
            return UploadPhotoResponseDTO(
                result=False,
                message=str(e),
                photo_url="none",
                object_key="none"
            )

    async def delete_photo(self, request: DeletePhotoRequestDTO, username: str) -> DeletePhotoResponseDTO:
        try:
            self.user_photo_service.delete_user_photo(request.object_key)
            await self.photo_orm_service.delete_photo(request.object_key, username)

            return DeletePhotoResponseDTO(
                status="success",
                message="Deleted photo successfully",
                timestamp=datetime.utcnow(),
            )
        except Exception as e:
            return DeletePhotoResponseDTO(
                status="error",
                message=str(e),
                timestamp=datetime.utcnow(),
            )

    async def get_photos(self, username: str) -> GetPhotosResponseDTO:
        photos = await self.photo_orm_service.get_photos_by_email(username)
        return GetPhotosResponseDTO(
            photos_urls=[photo.photo_url for photo in photos]
        )