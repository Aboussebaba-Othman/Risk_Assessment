package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.springframework.stereotype.Component;


@Component
public class ProfitabilityScorer extends BaseRatioScorer {

    private static final double ROA_MIN = -0.05, ROA_MAX = 0.20;
    private static final double MN_MIN  = -0.05, MN_MAX  = 0.20;
    private static final double ROE_MIN = -0.10, ROE_MAX = 0.30;
    private static final double EBITDA_MIN = 0.0, EBITDA_MAX = 0.30;

    public double ratioROA(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getTotalAssets())) return partial ? 5.0 : 0.0;
        return lerp(bd(f.getNetResult()) / bd(f.getTotalAssets()), ROA_MIN, ROA_MAX, false);
    }

    public double ratioMN(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getRevenue())) return partial ? 5.0 : 0.0;
        return lerp(bd(f.getNetResult()) / bd(f.getRevenue()), MN_MIN, MN_MAX, false);
    }

    public double ratioROE(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getEquity())) return 0.0;
        return lerp(bd(f.getNetResult()) / bd(f.getEquity()), ROE_MIN, ROE_MAX, false);
    }

    public double ratioEBITDA(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getRevenue())) return partial ? 5.0 : 0.0;
        return lerp(bd(f.getEbitda()) / bd(f.getRevenue()), EBITDA_MIN, EBITDA_MAX, false);
    }

    public double score(FinancialDataDTO f, boolean partial) {
        return avg(ratioROA(f, partial), ratioMN(f, partial), ratioROE(f, partial), ratioEBITDA(f, partial));
    }
}
