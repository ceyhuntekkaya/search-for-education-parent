-- PARENT_PERSONAL (veli kişisel notu) ve eksik REASON_FOR_NEGATIVITY değerlerini note_type check'e ekle
ALTER TABLE appointment_notes
    DROP CONSTRAINT IF EXISTS appointment_notes_note_type_check;

ALTER TABLE appointment_notes
    ADD CONSTRAINT appointment_notes_note_type_check
        CHECK (note_type IS NULL OR note_type IN (
            'GENERAL',
            'PREPARATION',
            'FOLLOW_UP',
            'OUTCOME',
            'COMPLAINT',
            'COMPLIMENT',
            'TECHNICAL_ISSUE',
            'RESCHEDULING',
            'CANCELLATION',
            'REASON_FOR_NEGATIVITY',
            'REMINDER',
            'INTERNAL',
            'PARENT_PERSONAL'
        ));
