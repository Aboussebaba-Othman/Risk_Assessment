-- ============================================
-- SCORING SERVICE DATABASE SCHEMA
-- Database: risk_assessment_scoring
-- ============================================

-- Table: scoring_criteria
CREATE TABLE IF NOT EXISTS scoring_criteria (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL, -- FINANCIAL, OPERATIONAL, MARKET, LEGAL
    name VARCHAR(100) NOT NULL,
    description TEXT,
    weight DECIMAL(5, 2) NOT NULL DEFAULT 1.0, -- Weight in scoring calculation
    min_value DECIMAL(10, 2),
    max_value DECIMAL(10, 2),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: scores
CREATE TABLE IF NOT EXISTS scores (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Reference to company-service (no FK)
    tenant_id BIGINT NOT NULL, -- Reference to auth-service (no FK)
    overall_score DECIMAL(5, 2) NOT NULL, -- 0.00 to 100.00
    financial_score DECIMAL(5, 2),
    operational_score DECIMAL(5, 2),
    market_score DECIMAL(5, 2),
    legal_score DECIMAL(5, 2),
    risk_rating VARCHAR(20) NOT NULL, -- AAA, AA, A, BBB, BB, B, CCC, CC, C, D
    risk_level VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    confidence_level DECIMAL(5, 2), -- Confidence in the score (0-100)
    scoring_method VARCHAR(50) NOT NULL DEFAULT 'AUTOMATED', -- AUTOMATED, MANUAL, HYBRID
    scored_by BIGINT, -- Reference to auth-service users
    scored_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_until TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: score_details
CREATE TABLE IF NOT EXISTS score_details (
    id BIGSERIAL PRIMARY KEY,
    score_id BIGINT NOT NULL,
    criteria_id BIGINT NOT NULL,
    value DECIMAL(10, 2) NOT NULL,
    weighted_value DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    FOREIGN KEY (score_id) REFERENCES scores(id) ON DELETE CASCADE,
    FOREIGN KEY (criteria_id) REFERENCES scoring_criteria(id)
);

-- Table: score_history
CREATE TABLE IF NOT EXISTS score_history (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Reference to company-service (no FK)
    score_id BIGINT NOT NULL,
    overall_score DECIMAL(5, 2) NOT NULL,
    risk_rating VARCHAR(20) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    change_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (score_id) REFERENCES scores(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_scores_company_id ON scores(company_id);
CREATE INDEX idx_scores_tenant_id ON scores(tenant_id);
CREATE INDEX idx_scores_risk_level ON scores(risk_level);
CREATE INDEX idx_scores_risk_rating ON scores(risk_rating);
CREATE INDEX idx_scores_scored_at ON scores(scored_at);
CREATE INDEX idx_score_details_score_id ON score_details(score_id);
CREATE INDEX idx_score_details_criteria_id ON score_details(criteria_id);
CREATE INDEX idx_score_history_company_id ON score_history(company_id);
CREATE INDEX idx_score_history_score_id ON score_history(score_id);

-- Insert default scoring criteria
INSERT INTO scoring_criteria (category, name, description, weight) VALUES
    ('FINANCIAL', 'Liquidity Ratio', 'Current assets / Current liabilities', 1.5),
    ('FINANCIAL', 'Debt-to-Equity Ratio', 'Total debt / Total equity', 1.5),
    ('FINANCIAL', 'Profitability Margin', 'Net profit / Revenue', 1.0),
    ('OPERATIONAL', 'Operational Efficiency', 'Operating costs / Revenue', 1.0),
    ('OPERATIONAL', 'Employee Turnover', 'Annual employee turnover rate', 0.8),
    ('MARKET', 'Market Share', 'Company market share in industry', 1.2),
    ('MARKET', 'Customer Concentration', 'Revenue from top 5 customers', 0.9),
    ('LEGAL', 'Compliance Score', 'Regulatory compliance rating', 1.3),
    ('LEGAL', 'Litigation Risk', 'Pending legal cases', 1.1)
ON CONFLICT DO NOTHING;

-- Comments for documentation
COMMENT ON TABLE scoring_criteria IS 'Criteria used for risk scoring';
COMMENT ON TABLE scores IS 'Risk scores for companies';
COMMENT ON TABLE score_details IS 'Detailed breakdown of scores';
COMMENT ON TABLE score_history IS 'Historical record of score changes';
