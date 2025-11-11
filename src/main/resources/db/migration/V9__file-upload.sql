CREATE TABLE file_uploads (
                              id BIGSERIAL PRIMARY KEY,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              created_by BIGINT,
                              updated_by BIGINT,
                              is_active BOOLEAN DEFAULT TRUE NOT NULL,

                              file_name VARCHAR(255) NOT NULL,
                              original_file_name VARCHAR(255) NOT NULL,
                              file_url TEXT NOT NULL,
                              thumbnail_url TEXT,
                              file_size_bytes BIGINT NOT NULL,
                              mime_type VARCHAR(255) NOT NULL,
                              media_type VARCHAR(50) NOT NULL,
                              width INTEGER,
                              height INTEGER,
                              duration_seconds INTEGER,
                              upload_id VARCHAR(255),
                              is_processed BOOLEAN DEFAULT FALSE NOT NULL,
                              processing_error TEXT
);


CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON file_uploads
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
