package com.riskassessment.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.dto.AnalysisResultDTO;
import com.riskassessment.report.dto.CompanyDTO;
import com.riskassessment.report.dto.RecommendationDTO;
import com.riskassessment.report.dto.ScoreDTO;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfGeneratorService {

    private static final Color BRAND_BLUE = new Color(30, 80, 160);
    private static final Color BRAND_DARK = new Color(40, 40, 40);
    private static final Color HEADER_BG = new Color(30, 80, 160);
    private static final Color ROW_ALT = new Color(245, 247, 252);
    private static final Color GREEN = new Color(39, 174, 96);
    private static final Color ORANGE = new Color(230, 126, 34);
    private static final Color RED_ALERT = new Color(192, 57, 43);
    private static final Color GREY_TEXT = new Color(120, 120, 120);

    private Font h1() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BRAND_BLUE);
    }

    private Font h2() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_BLUE);
    }

    private Font h3() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BRAND_DARK);
    }

    private Font body() {
        return FontFactory.getFont(FontFactory.HELVETICA, 10, BRAND_DARK);
    }

    private Font small() {
        return FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, GREY_TEXT);
    }

    // =========================================================================
    public byte[] generateRiskReport(CompanyDTO company, ScoreDTO score,
            RecommendationDTO rec, AnalysisDTO analysis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, company);
            addCompanyInfo(doc, company);
            addScoringSection(doc, score);
            addRecommendation(doc, rec);
            addSwotSection(doc, analysis);
            addFooter(doc);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return out.toByteArray();
    }

    // -- 1. Header ------------------------------------------------------------
    private void addHeader(Document doc, CompanyDTO company) throws DocumentException {
        Paragraph brand = new Paragraph("RISK ASSESS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, BRAND_BLUE));
        brand.setAlignment(Element.ALIGN_RIGHT);
        doc.add(brand);
        doc.add(Chunk.NEWLINE);

        Paragraph title = new Paragraph("Comprehensive Risk Assessment Report", h1());
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        String name = (company != null && company.getName() != null)
                ? company.getName().toUpperCase()
                : "UNKNOWN COMPANY";
        Paragraph sub = new Paragraph("Target Entity: " + name,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BRAND_DARK));
        sub.setAlignment(Element.ALIGN_CENTER);
        doc.add(sub);
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator(1.5f, 100f, BRAND_BLUE, Element.ALIGN_CENTER, -2));
        doc.add(Chunk.NEWLINE);
    }

    // -- 2. Company Info ------------------------------------------------------
    private void addCompanyInfo(Document doc, CompanyDTO company) throws DocumentException {
        doc.add(new Paragraph("1. Company Overview", h2()));
        doc.add(Chunk.NEWLINE);

        if (company == null) {
            addWarning(doc, "Company information could not be retrieved.");
            return;
        }

        PdfPTable t = twoColTable();
        row(t, "Legal Name", safe(company.getName()), true);
        row(t, "Tax ID (ICE)", safe(company.getTaxId()), false);
        row(t, "Registration No.", safe(company.getRegistrationNumber()), true);
        row(t, "Legal Form", safe(company.getLegalForm()), false);
        row(t, "Industry / Sector", safe(company.getIndustry()), true);
        row(t, "Incorporation Date", company.getIncorporationDate() != null
                ? company.getIncorporationDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : "N/A", false);
        row(t, "Country / City", safe(company.getCountry()) + " / " + safe(company.getCity()), true);
        row(t, "Employees", company.getEmployeeCount() != null
                ? company.getEmployeeCount() + " employees"
                : "N/A", false);
        row(t, "Status", safe(company.getStatus()), true);
        doc.add(t);
        doc.add(Chunk.NEWLINE);
    }

    // -- 3. Score & Risk Level ------------------------------------------------
    private void addScoringSection(Document doc, ScoreDTO score) throws DocumentException {
        doc.add(new Paragraph("2. Financial Risk Score (CDC — 15 Ratios)", h2()));
        doc.add(Chunk.NEWLINE);

        if (score == null) {
            addWarning(doc, "No score calculated yet. Run POST /api/v1/scores/calculate/{id} first.");
            doc.add(Chunk.NEWLINE);
            return;
        }

        double s = score.getOverallScore() != null ? score.getOverallScore().doubleValue() : 0;
        Color scoreColor = s >= 75 ? GREEN : s >= 40 ? ORANGE : RED_ALERT;

        PdfPTable gauge = new PdfPTable(2);
        gauge.setWidthPercentage(100);
        gauge.setWidths(new float[] { 60, 40 });
        gauge.setSpacingBefore(5);
        gauge.setSpacingAfter(10);

        PdfPCell lc = new PdfPCell(new Phrase("Overall Risk Score", h3()));
        lc.setBackgroundColor(HEADER_BG);
        lc.setPadding(10);
        lc.setBorder(Rectangle.NO_BORDER);
        gauge.addCell(lc);

        PdfPCell sc = new PdfPCell(new Phrase(String.format("%.0f / 100", s),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, scoreColor)));
        sc.setBackgroundColor(new Color(248, 248, 248));
        sc.setPadding(10);
        sc.setHorizontalAlignment(Element.ALIGN_CENTER);
        sc.setBorder(Rectangle.NO_BORDER);
        gauge.addCell(sc);
        doc.add(gauge);

        PdfPTable t = twoColTable();
        row(t, "Risk Level", safe(score.getRiskLevel()), true);
        row(t, "Rating", safe(score.getRiskRating()), false);
        row(t, "Methodology", safe(score.getScoringMethod()), true);
        row(t, "Scored At", safe(score.getScoredAt()), false);
        if (score.getNotes() != null && !score.getNotes().isBlank())
            row(t, "Notes", score.getNotes(), true);
        doc.add(t);

        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Score Bands (CDC Section 3.3):", h3()));
        String[][] bands = {
                { "90-100", "EXCELLENT   — Defaut <1%", "00A36C" },
                { "75-89", "LOW_RISK    — Defaut 1-3%", "2ECC71" },
                { "60-74", "MODERATE_RISK — Defaut 3-8%", "F39C12" },
                { "40-59", "MEDIUM_RISK — Defaut 8-15%", "E67E22" },
                { "25-39", "HIGH_RISK   — Defaut 15-30%", "E74C3C" },
                { "0-24", "CRITICAL    — Defaut >30%", "8E44AD" }
        };
        PdfPTable legend = new PdfPTable(2);
        legend.setWidthPercentage(70);
        legend.setSpacingBefore(4);
        for (String[] b : bands) {
            PdfPCell r1 = new PdfPCell(
                    new Phrase(b[0], FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
            r1.setBackgroundColor(hexColor(b[2]));
            r1.setPadding(4);
            r1.setBorder(Rectangle.NO_BORDER);
            legend.addCell(r1);
            PdfPCell r2 = new PdfPCell(new Phrase(b[1], body()));
            r2.setPadding(4);
            r2.setBorder(Rectangle.NO_BORDER);
            r2.setBackgroundColor(ROW_ALT);
            legend.addCell(r2);
        }
        doc.add(legend);
        doc.add(Chunk.NEWLINE);
    }

    // -- 4. CDC Recommendation (F-03) -----------------------------------------
    private void addRecommendation(Document doc, RecommendationDTO rec) throws DocumentException {
        doc.add(new Paragraph("3. Credit Decision (CDC F-03)", h2()));
        doc.add(Chunk.NEWLINE);

        if (rec == null) {
            addWarning(doc, "Recommendation not available. Calculate score first.");
            doc.add(Chunk.NEWLINE);
            return;
        }

        boolean accord = rec.getDecision() != null && rec.getDecision().startsWith("ACCORD");
        boolean refus = "REFUS".equals(rec.getDecision());
        Color decColor = accord ? GREEN : refus ? RED_ALERT : ORANGE;

        PdfPTable badge = new PdfPTable(2);
        badge.setWidthPercentage(100);
        badge.setWidths(new float[] { 45, 55 });
        badge.setSpacingBefore(5);
        badge.setSpacingAfter(10);

        PdfPCell decCell = new PdfPCell(new Phrase(safe(rec.getDecision()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE)));
        decCell.setBackgroundColor(decColor);
        decCell.setPadding(12);
        decCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        decCell.setBorder(Rectangle.NO_BORDER);
        badge.addCell(decCell);

        PdfPCell labelCell = new PdfPCell(new Phrase(safe(rec.getDecisionLabel()), h3()));
        labelCell.setBackgroundColor(ROW_ALT);
        labelCell.setPadding(12);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setBorder(Rectangle.NO_BORDER);
        badge.addCell(labelCell);
        doc.add(badge);

        PdfPTable t = twoColTable();
        row(t, "Credit Limit Policy", safe(rec.getCreditLimitPolicy()), true);
        row(t, "Max Payment Days", rec.getMaxPaymentDays() != null
                ? rec.getMaxPaymentDays() + " days"
                : "N/A", false);
        row(t, "Guarantees Required", safe(rec.getGuaranteesRequired()), true);
        row(t, "Default Rate Range", safe(rec.getDefaultRateRange()), false);
        doc.add(t);
        doc.add(Chunk.NEWLINE);
    }

    // -- 5. SWOT Analysis -----------------------------------------------------
    private void addSwotSection(Document doc, AnalysisDTO analysis) throws DocumentException {
        doc.add(new Paragraph("4. SWOT Strategic Analysis", h2()));
        doc.add(Chunk.NEWLINE);

        if (analysis == null) {
            addWarning(doc, "SWOT analysis could not be retrieved.");
            doc.add(Chunk.NEWLINE);
            return;
        }

        if (analysis.getOverallHealth() != null) {
            Color hc = "GOOD".equals(analysis.getOverallHealth()) ? GREEN
                    : "POOR".equals(analysis.getOverallHealth()) ? RED_ALERT : ORANGE;
            doc.add(new Paragraph("Overall Financial Health: " + analysis.getOverallHealth(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, hc)));
            doc.add(Chunk.NEWLINE);
        }

        if (analysis.getResults() != null && !analysis.getResults().isEmpty()) {
            String[] types = { "STRENGTH", "WEAKNESS", "OPPORTUNITY", "THREAT" };
            String[] labels = { "Strengths", "Weaknesses", "Opportunities", "Threats" };
            Color[] colors = { GREEN, RED_ALERT, BRAND_BLUE, ORANGE };

            for (int i = 0; i < types.length; i++) {
                final String type = types[i];
                List<AnalysisResultDTO> items = analysis.getResults().stream()
                        .filter(r -> type.equals(r.getResultType()))
                        .collect(Collectors.toList());
                if (items.isEmpty())
                    continue;

                doc.add(new Paragraph(labels[i], FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, colors[i])));

                for (AnalysisResultDTO item : items) {
                    PdfPTable card = new PdfPTable(1);
                    card.setWidthPercentage(100);
                    card.setSpacingBefore(4);
                    card.setSpacingAfter(4);

                    PdfPCell tc = new PdfPCell(
                            new Phrase("[" + safe(item.getCategory()) + "] " + safe(item.getTitle()), h3()));
                    tc.setBackgroundColor(ROW_ALT);
                    tc.setPadding(6);
                    tc.setBorderColor(colors[i]);
                    tc.setBorderWidth(1.5f);
                    card.addCell(tc);

                    PdfPCell dc = new PdfPCell(new Phrase(safe(item.getDescription()), body()));
                    dc.setPadding(6);
                    dc.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                    dc.setBorderColor(new Color(200, 200, 200));
                    card.addCell(dc);

                    if (item.getRecommendation() != null && !item.getRecommendation().isBlank()) {
                        PdfPCell rc = new PdfPCell(new Phrase("=> " + item.getRecommendation(), small()));
                        rc.setPadding(5);
                        rc.setBackgroundColor(new Color(250, 250, 250));
                        rc.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                        rc.setBorderColor(new Color(200, 200, 200));
                        card.addCell(rc);
                    }
                    doc.add(card);
                }
                doc.add(Chunk.NEWLINE);
            }
        } else if (analysis.getSummary() != null && !analysis.getSummary().isBlank()) {
            doc.add(new Paragraph(analysis.getSummary(), body()));
        } else {
            addWarning(doc, "No SWOT data. Trigger via POST /analysis/companies/{id}/trigger");
        }
        doc.add(Chunk.NEWLINE);
    }

    // -- Footer ---------------------------------------------------------------
    private void addFooter(Document doc) throws DocumentException {
        doc.add(new LineSeparator(0.5f, 100f, GREY_TEXT, Element.ALIGN_CENTER, -2));
        doc.add(Chunk.NEWLINE);
        Paragraph gen = new Paragraph(
                "Generated: " + java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                        + "   |   Risk Assessment Platform — CDC Compliant Scoring Engine",
                small());
        gen.setAlignment(Element.ALIGN_CENTER);
        doc.add(gen);
        Paragraph disc = new Paragraph("CONFIDENTIAL — FOR INTERNAL USE ONLY", small());
        disc.setAlignment(Element.ALIGN_CENTER);
        doc.add(disc);
    }

    // -- Helpers --------------------------------------------------------------
    private PdfPTable twoColTable() {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setSpacingBefore(5);
        t.setSpacingAfter(5);
        try {
            t.setWidths(new float[] { 40, 60 });
        } catch (DocumentException ignored) {
        }
        return t;
    }

    private void row(PdfPTable t, String label, String value, boolean shade) {
        PdfPCell lc = new PdfPCell(new Phrase(label, h3()));
        lc.setBackgroundColor(HEADER_BG);
        lc.setPadding(6);
        lc.setBorder(Rectangle.NO_BORDER);
        t.addCell(lc);
        PdfPCell vc = new PdfPCell(new Phrase(value, body()));
        vc.setBackgroundColor(shade ? ROW_ALT : Color.WHITE);
        vc.setPadding(6);
        vc.setBorder(Rectangle.NO_BORDER);
        t.addCell(vc);
    }

    private void addWarning(Document doc, String msg) throws DocumentException {
        doc.add(new Paragraph("! " + msg, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, RED_ALERT)));
    }

    private String safe(String s) {
        return s != null ? s : "N/A";
    }   

    private Color hexColor(String hex) {
        try {
            return new Color(
                    Integer.parseInt(hex.substring(0, 2), 16),
                    Integer.parseInt(hex.substring(2, 4), 16),
                    Integer.parseInt(hex.substring(4, 6), 16));
        } catch (Exception e) {
            return BRAND_BLUE;
        }
    }
}
