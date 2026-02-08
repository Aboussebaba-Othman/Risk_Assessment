-- ============================================
-- ANALYSIS SERVICE DATABASE SCHEMA
-- Database: risk_assessment_analysis
-- ============================================

-- Table: financial_analyses
CREATE TABLE IF NOT EXISTS financial_analyses (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Reference to company-service (no FK)
    tenant_id BIGINT NOT NULL, -- Reference to auth-service (no FK)
    analysis_type VARCHAR(50) NOT NULL, -- QUARTERLY, ANNUAL, CUSTOM
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, FAILED
    overall_health VARCHAR(20), -- EXCELLENT, GOOD, FAIR, POOR, CRITICAL
    revenue DECIMAL(15, 2),
    expenses DECIMAL(15, 2),
    net_profit DECIMAL(15, 2),
    assets DECIMAL(15, 2),
    liabilities DECIMAL(15, 2),
    equity DECIMAL(15, 2),
    cash_flow DECIMAL(15, 2),
    currency VARCHAR(3) DEFAULT 'USD',
    analyzed_by BIGINT, -- Reference to auth-service users
    analyzed_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: analysis_metrics
CREATE TABLE IF NOT EXISTS analysis_metrics (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_category VARCHAR(50) NOT NULL, -- LIQUIDITY, PROFITABILITY, LEVERAGE, EFFICIENCY
    metric_value DECIMAL(15, 4) NOT NULL,
    benchmark_value DECIMAL(15, 4),
    variance_percentage DECIMAL(5, 2),
    interpretation VARCHAR(20), -- POSITIVE, NEUTRAL, NEGATIVE
    notes TEXT,
    FOREIGN KEY (analysis_id) REFERENCES financial_analyses(id) ON DELETE CASCADE
);

-- Table: analysis_results
CREATE TABLE IF NOT EXISTS analysis_results (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL,
    result_type VARCHAR(50) NOT NULL, -- STRENGTH, WEAKNESS, OPPORTUNITY, THREAT
    category VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(20), -- LOW, MEDIUM, HIGH, CRITICAL
    recommendation TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (analysis_id) REFERENCES financial_analyses(id) ON DELETE CASCADE
);

-- Table: analysis_attachments
CREATE TABLE IF NOT EXISTS analysis_attachments (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50), -- SPREADSHEET, PDF, IMAGE
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by BIGINT,
    FOREIGN KEY (analysis_id) REFERENCES financial_analyses(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_financial_analyses_company_id ON financial_analyses(company_id);
CREATE INDEX idx_financial_analyses_tenant_id ON financial_analyses(tenant_id);
CREATE INDEX idx_financial_analyses_status ON financial_analyses(status);
CREATE INDEX idx_financial_analyses_period ON financial_analyses(period_start, period_end);
CREATE INDEX idx_analysis_metrics_analysis_id ON analysis_metrics(analysis_id);
CREATE INDEX idx_analysis_metrics_category ON analysis_metrics(metric_category);
CREATE INDEX idx_analysis_results_analysis_id ON analysis_results(analysis_id);
CREATE INDEX idx_analysis_results_type ON analysis_results(result_type);
CREATE INDEX idx_analysis_results_severity ON analysis_results(severity);
CREATE INDEX idx_analysis_attachments_analysis_id ON analysis_attachments(analysis_id);

-- Comments for documentation
COMMENT ON TABLE financial_analyses IS 'Financial analyses performed on companies';
COMMENT ON TABLE analysis_metrics IS 'Detailed financial metrics from analyses';
COMMENT ON TABLE analysis_results IS 'SWOT analysis results';
COMMENT ON TABLE analysis_attachments IS 'Supporting documents for analyses';
