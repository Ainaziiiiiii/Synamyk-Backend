-- Add earned_points column if not already added by Hibernate ddl-auto
ALTER TABLE test_sessions ADD COLUMN IF NOT EXISTS earned_points INTEGER NOT NULL DEFAULT 0;
ALTER TABLE test_sessions ADD COLUMN IF NOT EXISTS paused_at TIMESTAMP;

-- Backfill earned_points for completed sessions:
-- sum pointValue of all correctly answered questions in each session
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
