-- Kyrgyz language (KY) translations for all content tables

-- Regions
ALTER TABLE regions ADD COLUMN IF NOT EXISTS name_ky VARCHAR(255);

-- Tests
ALTER TABLE tests ADD COLUMN IF NOT EXISTS title_ky       VARCHAR(500);
ALTER TABLE tests ADD COLUMN IF NOT EXISTS description_ky TEXT;

-- Sub-tests
ALTER TABLE sub_tests ADD COLUMN IF NOT EXISTS title_ky      VARCHAR(500);
ALTER TABLE sub_tests ADD COLUMN IF NOT EXISTS level_name_ky VARCHAR(255);

-- Questions
ALTER TABLE questions ADD COLUMN IF NOT EXISTS text_ky         TEXT;
ALTER TABLE questions ADD COLUMN IF NOT EXISTS section_name_ky VARCHAR(500);
ALTER TABLE questions ADD COLUMN IF NOT EXISTS explanation_ky  TEXT;

-- Answer options
ALTER TABLE answer_options ADD COLUMN IF NOT EXISTS text_ky TEXT;

-- News articles
ALTER TABLE news_articles ADD COLUMN IF NOT EXISTS title_ky   VARCHAR(500);
ALTER TABLE news_articles ADD COLUMN IF NOT EXISTS content_ky TEXT;

-- Video lessons
ALTER TABLE video_lessons ADD COLUMN IF NOT EXISTS title_ky       VARCHAR(500);
ALTER TABLE video_lessons ADD COLUMN IF NOT EXISTS description_ky TEXT;