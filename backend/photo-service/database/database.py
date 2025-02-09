# Оптимизация для database.py
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession
from sqlalchemy.orm import DeclarativeBase
from config.config import settings

class Database:
    _engine = None
    _session_factory = None

    @classmethod
    def get_engine(cls):
        if cls._engine is None:
            cls._engine = create_async_engine(
                settings.DATABASE_URL_asyncpg_docker,
                pool_size=20,
                max_overflow=10,
                pool_recycle=3600
            )
        return cls._engine

    @classmethod
    def get_session_factory(cls):
        if cls._session_factory is None:
            cls._session_factory = async_sessionmaker(
                bind=cls.get_engine(),
                expire_on_commit=False,
                class_=AsyncSession
            )
        return cls._session_factory

class Base(DeclarativeBase):
    pass