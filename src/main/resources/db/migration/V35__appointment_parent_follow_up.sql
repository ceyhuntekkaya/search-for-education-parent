ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS parent_follow_up_outcome VARCHAR(50);

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS follow_up_time TIME(6);

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS no_sale_reason VARCHAR(500);

COMMENT ON COLUMN appointments.parent_follow_up_outcome IS 'Veli takip sonucu (ParentFollowUpOutcome)';
COMMENT ON COLUMN appointments.follow_up_time IS 'Planlanan arama / takip saati';
COMMENT ON COLUMN appointments.no_sale_reason IS 'Satış olmama sebebi';
