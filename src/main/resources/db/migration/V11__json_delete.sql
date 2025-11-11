ALTER TABLE school_pricing
ALTER COLUMN competitor_analysis TYPE text
    USING competitor_analysis::text;