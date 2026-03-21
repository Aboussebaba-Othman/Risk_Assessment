package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.springframework.stereotype.Component;

@Component
public class LiquidityScorer extends BaseRatioScorer {

    private static final double LG_MIN = 0.5, LG_MAX = 3.0;
    private static final double LR_MIN = 0.3, LR_MAX = 2.0;
    private static final double LI_MIN = 0.0, LI_MAX = 1.0;

    // R1: Actif Circulant / Passif Circulant
    public double ratioLG(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getCurrentLiabilities())) return partial ? 5.0 : 0.0;
        return lerp(bd(f.getCurrentAssets()) / bd(f.getCurrentLiabilities()), LG_MIN, LG_MAX, false);
    }

    // R2: (Actif Circulant - Stocks) / Passif Circulant
    public double ratioLR(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getCurrentLiabilities())) return partial ? 5.0 : 0.0;
        return lerp((bd(f.getCurrentAssets()) - bd(f.getInventory())) / bd(f.getCurrentLiabilities()), LR_MIN, LR_MAX, false);
    }

    // R3: Trésorerie / Passif Circulant
    public double ratioLI(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getCurrentLiabilities())) return partial ? 5.0 : 0.0;
        return lerp(bd(f.getCash()) / bd(f.getCurrentLiabilities()), LI_MIN, LI_MAX, false);
    }

    public double score(FinancialDataDTO f, boolean partial) {
        return avg(ratioLG(f, partial), ratioLR(f, partial), ratioLI(f, partial));
    }
}
