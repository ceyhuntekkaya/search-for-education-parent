CREATE TABLE IF NOT EXISTS parent_search_list (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    parent_id BIGINT,
    name VARCHAR(255),
    data TEXT
    );
