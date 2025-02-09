CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    photo_url TEXT NOT NULL,
    object_key TEXT NOT NULL,
    mimetype VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
