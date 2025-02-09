from typing import List

from pydantic import BaseModel

class GetPhotosResponseDTO(BaseModel):
    photos_urls: List[str]