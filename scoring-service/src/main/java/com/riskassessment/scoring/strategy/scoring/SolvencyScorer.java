package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.springframework.stereotype.Component;

@Component
public class SolvencyScorer extends BaseRatioScorer {

    private static final double AF_MIN = 0.0, AF_MAX = 0.7;
    private static final double CE_MIN = 0.0, CE_MAX = 3.0;
    private static final double CI_MIN = 1.0, CI_MAX = 10.0;

    public double ratioAF(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getTotalLiabilities())) return partial ? 5.0 : 0.0;
        return lerp(bd(f.getEquity()) / bd(f.getTotalLiabilities()), AF_MIN, AF_MAX, false);
    }

    public double ratioCE(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getEquity())) return 0.0;
        double debt = bd(f.getLongTermDebt()) + bd(f.getCurrentLiabilities());
        return lerp(debt / bd(f.getEquity()), CE_MIN, CE_MAX, true);
    }

    public double ratioCI(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getFinancialExpenses())) return 5.0; 
        return lerp(bd(f.getOperatingIncome()) / bd(f.getFinancialExpenses()), CI_MIN, CI_MAX, false);
    }

    public double score(FinancialDataDTO f, boolean partial) {
        return avg(ratioAF(f, partial), ratioCE(f, partial), ratioCI(f, partial));
    }
}
