package com.riskassessment.report.pdf;

import java.awt.Color;


public abstract class BasePdfRenderer {

    protected static final Color NAVY_900   = new Color(15, 23, 42);
    protected static final Color BRAND_LIGHT = new Color(56, 189, 248);
    protected static final Color GRAY_100   = new Color(243, 244, 246);
    protected static final Color GRAY_200   = new Color(229, 231, 235);
    protected static final Color GRAY_500   = new Color(107, 114, 128);
    protected static final Color TEXT_MAIN  = new Color(31, 41, 55);
    protected static final Color GREEN_600  = new Color(22, 163, 74);
    protected static final Color RED_600    = new Color(220, 38, 38);
    protected static final Color ORANGE_500 = new Color(249, 115, 22);

    protected com.lowagie.text.Font titleFont()  { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 26, NAVY_900); }
    protected com.lowagie.text.Font h1()          { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 18, NAVY_900); }
    protected com.lowagie.text.Font h2()          { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 13, NAVY_900); }
    protected com.lowagie.text.Font h3()          { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 10, GRAY_500); }
    protected com.lowagie.text.Font body()        { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 10, TEXT_MAIN); }
    protected com.lowagie.text.Font bodyBold()    { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 10, TEXT_MAIN); }
    protected com.lowagie.text.Font small()       { return com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 8, GRAY_500); }

    protected String safe(String s) { return s != null ? s : "N/A"; }
}
