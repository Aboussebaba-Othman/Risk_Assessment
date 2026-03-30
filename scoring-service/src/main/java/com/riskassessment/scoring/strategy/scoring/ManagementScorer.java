package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.springframework.stereotype.Component;


@Component
public class ManagementScorer extends BaseRatioScorer {

    private static final double DSO_MIN = 30.0, DSO_MAX = 180.0;
    private static final double DF_MIN  = 15.0, DF_MAX  = 120.0;
    private static final double RS_MIN  = 0.0,  RS_MAX  = 12.0;
    private static final double FRN_MIN = -0.20, FRN_MAX = 0.30;
    private static final double BFR_MIN = 0.0,  BFR_MAX = 0.20;

    public double ratioDSO(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getRevenue())) return partial ? 5.0 : 0.0;
        return lerp((bd(f.getAccountsReceivable()) / bd(f.getRevenue())) * 360.0, DSO_MIN, DSO_MAX, true);
    }
    public double ratioDF(FinancialDataDTO f, boolean partial) {
        double purchases = bd(f.getCostOfGoodsSold()) > 0 ? bd(f.getCostOfGoodsSold()) : bd(f.getRevenue()) * 0.6;
        if (purchases <= 0) return partial ? 5.0 : 0.0;
        return lerp((bd(f.getAccountsPayable()) / purchases) * 360.0, DF_MIN, DF_MAX, false);
    }

    public double ratioRS(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getInventory())) return 5.0;
        return lerp(bd(f.getRevenue()) / bd(f.getInventory()), RS_MIN, RS_MAX, false);
    }

    public double ratioFRN(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getRevenue())) return partial ? 5.0 : 0.0;
        double frn = (bd(f.getEquity()) + bd(f.getLongTermDebt())) - bd(f.getFixedAssets());
        return lerp(frn / bd(f.getRevenue()), FRN_MIN, FRN_MAX, false);
    }

    public double ratioBFR(FinancialDataDTO f, boolean partial) {
        if (!pos(f.getRevenue())) return partial ? 5.0 : 0.0;
        double bfr = bd(f.getCurrentAssets()) - bd(f.getCurrentLiabilities());
        return lerp(bfr / bd(f.getRevenue()), BFR_MIN, BFR_MAX, true);
    }

    public double gestScore(FinancialDataDTO f, boolean partial) {
        return avg(ratioDSO(f, partial), ratioDF(f, partial), ratioRS(f, partial));
    }

    public double structureScore(FinancialDataDTO f, boolean partial) {
        return avg(ratioFRN(f, partial), ratioBFR(f, partial));
    }
}
