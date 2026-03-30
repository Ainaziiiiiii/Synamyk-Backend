-- Support multiple selected options per user answer

CREATE TABLE IF NOT EXISTS user_answer_selected_options
(
    user_answer_id   BIGINT NOT NULL REFERENCES user_answers (id) ON DELETE CASCADE,
    answer_option_id BIGINT NOT NULL REFERENCES answer_options (id) ON DELETE CASCADE,
    PRIMARY KEY (user_answer_id, answer_option_id)
);

-- Migrate existing single selections to the new join table
INSERT INTO user_answer_selected_options (user_answer_id, answer_option_id)
SELECT id, selected_option_id
FROM user_answers
WHERE selected_option_id IS NOT NULL;

-- Drop the old column
ALTER TABLE user_answers
    DROP COLUMN IF EXISTS selected_option_id;
