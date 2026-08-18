-- Adopted-schema migration for databases that were created by the original
-- URL shortener implementation before visitor_hash was introduced.
--
-- Fresh databases run V1 first, so this statement is also safe there because
-- V2 only executes after V1 has created the table.
ALTER TABLE click_events
    ADD COLUMN IF NOT EXISTS visitor_hash VARCHAR(64);
