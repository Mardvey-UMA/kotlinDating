FROM python:3.13-slim

RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    libpq-dev \
    postgresql-client \
    libmagic1 \
    && rm -rf /var/lib/apt/lists/* \

WORKDIR /app

COPY . .

COPY requirements.txt /app/requirements.txt

RUN --mount=type=cache,target=/root/.cache/pip pip install --no-cache-dir -r requirements.txt

CMD ["python", "main.py"]

EXPOSE 8086
