CREATE TABLE users (
                       id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       hashed_password TEXT NOT NULL
);

CREATE TABLE media (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       media_type VARCHAR(20) NOT NULL,
                       release_year INTEGER,
                       genres TEXT,
                       age_restriction INTEGER,
                       creator_id INTEGER NOT NULL,

                       FOREIGN KEY (creator_id) REFERENCES users(id)
);