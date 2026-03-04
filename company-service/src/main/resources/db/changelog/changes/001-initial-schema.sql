-- ============================================
-- COMPANY SERVICE DATABASE SCHEMA
-- Database: risk_assessment_company
-- ============================================

-- Table: companies
CREATE TABLE IF NOT EXISTS companies (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL, -- Reference to auth-service tenants (no FK)
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) UNIQUE,
    tax_id VARCHAR(100),
    industry VARCHAR(100),
    country VARCHAR(2), -- ISO 3166-1 alpha-2
    city VARCHAR(100),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(255),
    employee_count INTEGER,
    annual_revenue DECIMAL(15, 2),
    currency VARCHAR(3) DEFAULT 'USD', -- ISO 4217
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED
    risk_level VARCHAR(20), -- LOW, MEDIUM, HIGH, CRITICAL
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT, -- Reference to auth-service users
    updated_by BIGINT
);

-- Table: company_documents
CREATE TABLE IF NOT EXISTS company_documents (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- FINANCIAL_STATEMENT, CONTRACT, CERTIFICATE, etc.
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    description TEXT,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by BIGINT, -- Reference to auth-service users
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- Table: company_contacts
CREATE TABLE IF NOT EXISTS company_contacts (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    contact_type VARCHAR(50) NOT NULL, -- PRIMARY, FINANCIAL, LEGAL, etc.
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    position VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    is_primary BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_companies_tenant_id ON companies(tenant_id);
CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_companies_risk_level ON companies(risk_level);
CREATE INDEX idx_companies_registration_number ON companies(registration_number);
CREATE INDEX idx_companies_name ON companies(name);
CREATE INDEX idx_company_documents_company_id ON company_documents(company_id);
CREATE INDEX idx_company_documents_type ON company_documents(document_type);
CREATE INDEX idx_company_contacts_company_id ON company_contacts(company_id);
CREATE INDEX idx_company_contacts_primary ON company_contacts(is_primary);

-- Comments for documentation
COMMENT ON TABLE companies IS 'Companies being assessed for risk';
COMMENT ON TABLE company_documents IS 'Documents uploaded for companies';
COMMENT ON TABLE company_contacts IS 'Contact persons for companies';
