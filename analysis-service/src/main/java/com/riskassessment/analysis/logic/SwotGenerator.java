package com.riskassessment.analysis.logic;

import com.riskassessment.analysis.dto.CompanyDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class SwotGenerator {

    public List<String> generateStrengths(CompanyDTO company, BigDecimal revenue, BigDecimal netResult,
            BigDecimal equity) {
        List<String> strengths = new ArrayList<>();

        if (revenue != null && revenue.compareTo(new BigDecimal("1000000")) > 0) {
            strengths.add("Strong Revenue Base (> 1M)");
        }
        if (netResult != null && netResult.compareTo(BigDecimal.ZERO) > 0) {
            strengths.add("Profitable Operations");
        }
        if (equity != null && equity.compareTo(new BigDecimal("500000")) > 0) {
            strengths.add("Solid Equity/Capitalization");
        }
        // Industry specific
        if (company.getIndustrySector() != null && company.getIndustrySector().equalsIgnoreCase("Technology")) {
            strengths.add("High Growth Sector");
        }

        if (strengths.isEmpty()) {
            strengths.add("Stable Core Business"); // Default
        }
        return strengths;
    }

    public List<String> generateWeaknesses(CompanyDTO company, BigDecimal revenue, BigDecimal netResult,
            BigDecimal debt) {
        List<String> weaknesses = new ArrayList<>();

        if (netResult != null && netResult.compareTo(BigDecimal.ZERO) < 0) {
            weaknesses.add("Negative Net Result (Loss making)");
        }
        if (debt != null && revenue != null && debt.compareTo(revenue) > 0) {
            weaknesses.add("High Debt to Revenue Ratio");
        }
        if (company.getIncorporationDate() != null && company.getIncorporationDate().getYear() > 2023) {
            weaknesses.add("Short Operating History (Startup Risk)");
        }

        return weaknesses;
    }

    public List<String> generateOpportunities(CompanyDTO company) {
        List<String> opportunities = new ArrayList<>();
        opportunities.add("Market Expansion");
        opportunities.add("Digital Transformation");

        if (company.getIndustrySector() != null) {
            opportunities.add("Sector Consolidation in " + company.getIndustrySector());
        }
        return opportunities;
    }

    public List<String> generateThreats(CompanyDTO company, BigDecimal netResult) {
        List<String> threats = new ArrayList<>();
        threats.add("Competitive Market Pressure");
        threats.add("Regulatory Changes");

        if (netResult != null && netResult.compareTo(BigDecimal.ZERO) < 0) {
            threats.add("Cash Flow Issues");
        }
        return threats;
    }
}
