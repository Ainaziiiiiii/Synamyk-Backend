-- Add columns (nullable first to handle existing rows)
ALTER TABLE test_sessions ADD COLUMN IF NOT EXISTS earned_points INTEGER;
ALTER TABLE test_sessions ADD COLUMN IF NOT EXISTS paused_at TIMESTAMP;

-- Fill nulls with 0 before adding NOT NULL constraint
UPDATE test_sessions SET earned_points = 0 WHERE earned_points IS NULL;

-- Now apply NOT NULL constraint
ALTER TABLE test_sessions ALTER COLUMN earned_points SET NOT NULL;
ALTER TABLE test_sessions ALTER COLUMN earned_points SET DEFAULT 0;

-- Backfill earned_points for completed sessions
UPDATE test_sessions ts
SET earned_points = COALESCE((
    SELECT SUM(q.point_value)
    FROM user_answers ua
    JOIN questions q ON q.id = ua.question_id
    WHERE ua.session_id = ts.id
      AND ua.is_correct = true
), 0)
WHERE ts.status = 'COMPLETED'
  AND ts.earned_points = 0;
