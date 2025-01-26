CREATE TABLE IF NOT EXISTS media_file
(
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    file_name            TEXT NOT NULL,
    sha256_hash          TEXT NOT NULL UNIQUE,
    origin_creation_date TEXT NOT NULL,
    upload_date          TEXT NOT NULL,
    file_path            TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tag
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS media_file_tag
(
    media_file_id INTEGER NOT NULL,
    tag_id        INTEGER NOT NULL,
    PRIMARY KEY (media_file_id, tag_id),
    FOREIGN KEY (media_file_id) REFERENCES media_file (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);