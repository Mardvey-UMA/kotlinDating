import logging

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.orm import sessionmaker
from sqlalchemy import delete
from sqlalchemy import select
from sqlalchemy.orm import Mapped
from contextlib import asynccontextmanager
from typing import Optional, List
# Для записи в БДшку

from models.Photo import PhotoOrm

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class PhotoOrmService:
    def __init__(self, session_factory: sessionmaker):
        self.session_factory = session_factory

    @asynccontextmanager
    async def _get_session(self):
        async with self.session_factory() as session:
            try:
                yield session
                await session.commit()
            except Exception:
                await session.rollback()
                raise

    async def add_photo(self, email: str, photo_url: str, object_key: str, mimetype: str) -> bool:
        async with self._get_session() as session:
            try:
                new_photo = PhotoOrm()

                new_photo.email = email
                new_photo.object_key = object_key
                new_photo.mimetype = mimetype
                new_photo.photo_url = photo_url

                session.add(new_photo)
                return True
            except SQLAlchemyError as e:
                logger.error(f"Ошибка при добавлении записи: {e}")
                await session.rollback()
                return False

    async def delete_photo(self, object_key: str, email: str) -> bool:
        async with self._get_session() as session:
            try:
                photo_to_delete = await session.execute(
                    select(PhotoOrm).filter(
                            PhotoOrm.object_key == object_key,
                            PhotoOrm.email == email
                    )
                )

                photo_to_delete = photo_to_delete.scalars().first()

                if photo_to_delete:
                    await session.delete(photo_to_delete)
                    await session.commit()
                    return True
                else:
                    logger.error(f"Запись с object_key={object_key} и email={email} не найдена.")
                    return False
            except SQLAlchemyError as e:
                print(f"Ошибка при удалении записи: {e}")
                await session.rollback()
                return False

    async def get_photos_by_email(self, email: str) -> List[PhotoOrm]:
        async with self._get_session() as session:
            try:
                stmt = select(PhotoOrm).filter(PhotoOrm.email == email)
                result = await session.execute(stmt)
                photos = result.scalars().all()
                return photos
            except SQLAlchemyError as e:
                logger.error(f"Ошибка при получении записей: {e}")
                return []
