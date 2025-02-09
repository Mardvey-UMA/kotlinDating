from pydantic import BaseModel

class UploadPhotoResponseDTO(BaseModel):
    result: bool
    message: str
    photo_url: str
    object_key: str