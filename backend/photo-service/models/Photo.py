from datetime import datetime
from typing import Annotated
from sqlalchemy.orm import mapped_column, Mapped
from sqlalchemy import text
from database.database import Base

intpk = Annotated[int, mapped_column(primary_key=True)]
created_at = Annotated[datetime, mapped_column(server_default=text("TIMEZONE('utc', now())"))]
from sqlalchemy import String, Text


class PhotoOrm(Base):
    __tablename__ = 'photos'
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    email: Mapped[str] = mapped_column(String(255))
    photo_url: Mapped[str] = mapped_column(Text)
    mimetype: Mapped[str] = mapped_column(String(255))
    created_at: Mapped[datetime] = mapped_column(server_default=text("TIMEZONE('utc', now())"))
    object_key: Mapped[str] = mapped_column(Text)