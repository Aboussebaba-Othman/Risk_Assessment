-- ============================================
-- REPORT SERVICE DATABASE SCHEMA
-- Database: risk_assessment_report
-- ============================================

-- Table: report_templates
CREATE TABLE IF NOT EXISTS report_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    template_type VARCHAR(50) NOT NULL, -- FINANCIAL, RISK, EXECUTIVE, CUSTOM
    content TEXT NOT NULL, -- HTML/Thymeleaf content or reference to file
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: reports
CREATE TABLE IF NOT EXISTS reports (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Reference to company-service
    tenant_id BIGINT NOT NULL, -- Reference to auth-service
    template_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL, -- PDF, EXCEL, HTML
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, GENERATING, COMPLETED, FAILED
    generated_file_path VARCHAR(500),
    generated_by BIGINT, -- Reference to auth-service user
    generated_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES report_templates(id)
);

-- Indexes
CREATE INDEX idx_reports_company_id ON reports(company_id);
CREATE INDEX idx_reports_tenant_id ON reports(tenant_id);
CREATE INDEX idx_reports_status ON reports(status);

-- Initial Templates
INSERT INTO report_templates (name, description, template_type, content) VALUES
    ('Standard Risk Report', 'Comprehensive risk assessment report', 'RISK', '<template>Risk content</template>'),
    ('Financial Summary', 'Key financial metrics summary', 'FINANCIAL', '<template>Financial content</template>')
ON CONFLICT DO NOTHING;
