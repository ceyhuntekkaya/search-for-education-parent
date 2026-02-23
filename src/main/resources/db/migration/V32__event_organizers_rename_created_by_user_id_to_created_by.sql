-- BaseEntity ile uyum için event_organizers.created_by_user_id -> created_by
ALTER TABLE event_organizers RENAME COLUMN created_by_user_id TO created_by;
