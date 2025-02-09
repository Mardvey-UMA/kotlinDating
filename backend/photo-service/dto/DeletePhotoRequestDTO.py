from pydantic import BaseModel

class DeletePhotoRequestDTO(BaseModel):
    object_key: str
    class Config:
        from_attributes = True
