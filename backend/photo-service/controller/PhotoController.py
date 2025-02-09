from fastapi import Depends, UploadFile, File, APIRouter
from dto.DeletePhotoRequestDTO import DeletePhotoRequestDTO
from dto.DeletePhotoResponseDTO import DeletePhotoResponseDTO
from dto.GetPhotosResponseDTO import GetPhotosResponseDTO
from dto.UploadPhotoResponseDTO import UploadPhotoResponseDTO
from dep.Dependicies import get_user_name, get_photo_service
from services.PhotoService import PhotoService
router = APIRouter(prefix="/api/photos", tags=["Photos"])

@router.post("/upload", response_model=UploadPhotoResponseDTO)
async def upload_photo(
        user_name: str = Depends(get_user_name),
        file: UploadFile = File(...),
        photoService: PhotoService = Depends(get_photo_service)
        )-> UploadPhotoResponseDTO:
    return await photoService.handle_photo(file, user_name)

@router.get("/get", response_model=GetPhotosResponseDTO)
async def upload_photo(
        user_name: str = Depends(get_user_name),
        photoService: PhotoService = Depends(get_photo_service)) \
        -> GetPhotosResponseDTO:
    return await photoService.get_photos(user_name)

@router.delete("/delete", response_model=DeletePhotoResponseDTO)
async def upload_photo(
        photo_request: DeletePhotoRequestDTO,
        user_name: str = Depends(get_user_name),
        photoService: PhotoService = Depends(get_photo_service)) \
        -> DeletePhotoResponseDTO:
    return await photoService.delete_photo(photo_request, user_name)
