-- Upsert all Kyrgyzstan regions with RU + KY names.
-- Safe to run on both fresh DB and existing DB with data already seeded.

DELETE FROM regions
WHERE id NOT IN (
    SELECT MIN(id) FROM regions GROUP BY name
);

CREATE UNIQUE INDEX IF NOT EXISTS regions_name_unique ON regions (name);

INSERT INTO regions (name, name_ky, created_at, updated_at) VALUES
    ('Бишкек',                  'Бишкек',            NOW(), NOW()),
    ('Чуйская область',         'Чүй облусу',         NOW(), NOW()),
    ('Иссык-Кульская область',  'Ысык-Көл облусу',    NOW(), NOW()),
    ('Нарынская область',       'Нарын облусу',        NOW(), NOW()),
    ('Таласская область',       'Талас облусу',        NOW(), NOW()),
    ('Джалал-Абадская область', 'Жалал-Абад облусу',  NOW(), NOW()),
    ('Ошская область',          'Ош облусу',           NOW(), NOW()),
    ('Баткенская область',      'Баткен облусу',       NOW(), NOW()),
    ('Ош',                      'Ош',                  NOW(), NOW())
ON CONFLICT (name) DO UPDATE
    SET name_ky    = EXCLUDED.name_ky,
        updated_at = NOW();
