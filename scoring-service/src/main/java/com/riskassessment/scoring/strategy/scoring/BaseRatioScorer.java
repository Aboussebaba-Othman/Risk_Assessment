package com.riskassessment.scoring.strategy.scoring;

import java.math.BigDecimal;

public abstract class BaseRatioScorer {

    protected static final BigDecimal ZERO = BigDecimal.ZERO;

    protected double lerp(double value, double min, double max, boolean inverted) {
        if (max == min) return 5.0;
        double ratio = (Math.min(max, Math.max(min, value)) - min) / (max - min);
        return Math.min(10, Math.max(0, inverted ? (1 - ratio) * 10.0 : ratio * 10.0));
    }

    protected double avg(double... notes) {
        double sum = 0;
        for (double n : notes) sum += n;
        return sum / notes.length;
    }

    protected double bd(BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue();
    }

    protected boolean pos(BigDecimal v) {
        return v != null && v.compareTo(ZERO) > 0;
    }

    protected int safe(Integer v) {
        return v == null ? 0 : v;
    }
}
