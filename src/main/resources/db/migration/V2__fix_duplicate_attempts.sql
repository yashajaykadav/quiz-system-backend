-- ============================================================
-- V2__fix_duplicate_attempts.sql
-- Cleans up orphan IN_PROGRESS rows for quiz+student combos
-- that already have a COMPLETED attempt, then adds a unique
-- constraint so this can never happen again.
-- ============================================================

-- Step 1: Delete student_answers belonging to orphan attempts
DELETE FROM student_answers
WHERE quiz_attempt_id IN (
    SELECT id FROM quiz_attempts orphan
    WHERE orphan.status = 'IN_PROGRESS'
      AND EXISTS (
          SELECT 1 FROM quiz_attempts completed
          WHERE completed.quiz_id    = orphan.quiz_id
            AND completed.student_id = orphan.student_id
            AND completed.status     = 'COMPLETED'
      )
);

-- Step 2: Delete the orphan IN_PROGRESS attempts themselves
DELETE qa FROM quiz_attempts qa
WHERE qa.status = 'IN_PROGRESS'
  AND EXISTS (
      SELECT 1 FROM (SELECT * FROM quiz_attempts) completed
      WHERE completed.quiz_id    = qa.quiz_id
        AND completed.student_id = qa.student_id
        AND completed.status     = 'COMPLETED'
  );

-- Step 3: Add unique constraint — one status per (quiz, student)
--         Prevents the same student from having both IN_PROGRESS
--         and COMPLETED rows for the same quiz simultaneously.
ALTER TABLE quiz_attempts
    ADD CONSTRAINT UQ_attempt_quiz_student_status
        UNIQUE (quiz_id, student_id, status);
