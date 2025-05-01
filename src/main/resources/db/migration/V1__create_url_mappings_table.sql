-- V1__create_url_mappings_table.sql


CREATE TABLE IF NOT EXISTS url_mappings (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            short_key VARCHAR(255) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_short_key ON url_mappings(short_key);
CREATE INDEX IF NOT EXISTS idx_original_url ON url_mappings(original_url);
