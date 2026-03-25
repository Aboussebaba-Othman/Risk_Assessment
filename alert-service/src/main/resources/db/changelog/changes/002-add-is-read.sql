-- ============================================
-- ADD IS_READ TO ALERTS
-- ============================================

ALTER TABLE alerts
ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN read_at TIMESTAMP;

CREATE INDEX idx_alerts_is_read ON alerts(is_read);
