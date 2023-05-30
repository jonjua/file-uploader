CREATE TABLE IF NOT EXISTS files(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    url VARCHAR(255),
    size BIGINT,
    content_type VARCHAR(255),
    content BYTEA);

CREATE TABLE IF NOT EXISTS summary(
    files_count BIGINT,
    files_size BIGINT);