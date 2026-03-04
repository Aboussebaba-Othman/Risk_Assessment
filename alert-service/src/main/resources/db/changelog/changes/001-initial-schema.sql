-- ============================================
-- ALERT SERVICE DATABASE SCHEMA
-- Database: risk_assessment_alert
-- ============================================

-- Table: alerts
CREATE TABLE IF NOT EXISTS alerts (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL, -- Reference to auth-service
    company_id BIGINT, -- Optional reference to company-service
    alert_type VARCHAR(50) NOT NULL, -- RISK_THRESHOLD, DEADLINE, SYSTEM, COMPLIANCE
    severity VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW', -- NEW, ACKNOWLEDGED, RESOLVED, DISMISSED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    resolved_by BIGINT -- Reference to auth-service user
);

-- Table: notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    alert_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL, -- Reference to auth-service user
    channel VARCHAR(20) NOT NULL, -- EMAIL, SMS, IN_APP, WEBHOOK
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED, READ
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_alerts_tenant_id ON alerts(tenant_id);
CREATE INDEX idx_alerts_company_id ON alerts(company_id);
CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_status ON notifications(status);
