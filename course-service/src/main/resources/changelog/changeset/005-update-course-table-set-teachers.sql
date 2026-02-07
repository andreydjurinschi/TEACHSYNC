-- migration_add_teachers_to_courses.sql

-- Обновляем таблицу courses, связываем с учителями
-- Предполагаем, что таблица courses имеет поле teacher_id

-- Курс 1: Java Basics -> Teacher Bob Johnson (id=2)
UPDATE courses
SET teacher_id = 2
WHERE id = 1;

-- Курс 2: Spring Boot -> Teacher David Brown (id=4)
UPDATE courses
SET teacher_id = 4
WHERE id = 2;

-- Курс 3: Databases -> Teacher Frank Garcia (id=6)
UPDATE courses
SET teacher_id = 6
WHERE id = 3;

-- Курс 4: ORM Technologies -> Teacher Grace Miller (id=7)
UPDATE courses
SET teacher_id = 7
WHERE id = 4;

-- Курс 5: Python Web Development -> Teacher Hank Davis (id=8)
UPDATE courses
SET teacher_id = 8
WHERE id = 5;

-- Курс 6: C# programming language -> Teacher Ivy Martinez (id=9)
UPDATE courses
SET teacher_id = 9
WHERE id = 6;

-- Курс 7: Microservices -> Teacher Jack Lopez (id=10)
UPDATE courses
SET teacher_id = 10
WHERE id = 7;

-- Курс 8: Kafka -> Teacher Bob Johnson (id=2)
UPDATE courses
SET teacher_id = 2
WHERE id = 8;

-- Курс 9: Frontend React -> Teacher David Brown (id=4)
UPDATE courses
SET teacher_id = 4
WHERE id = 9;

-- Курс 10: Cloud AWS -> Teacher Frank Garcia (id=6)
UPDATE courses
SET teacher_id = 6
WHERE id = 10;
