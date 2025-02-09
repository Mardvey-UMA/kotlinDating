from datetime import datetime

from pydantic import BaseModel

class DeletePhotoResponseDTO(BaseModel):
    status: str
    message: str
    timestamp: datetime
