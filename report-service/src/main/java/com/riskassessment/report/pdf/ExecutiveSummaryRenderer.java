package com.riskassessment.report.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.riskassessment.report.dto.RecommendationDTO;
import com.riskassessment.report.dto.ScoreDTO;
import org.springframework.stereotype.Component;

import java.awt.Color;


@Component
public class ExecutiveSummaryRenderer extends BasePdfRenderer {

    public void render(Document doc, ScoreDTO score, RecommendationDTO rec) throws DocumentException {
        doc.add(new Paragraph("Résumé Exécutif", h1()));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        try { table.setWidths(new float[]{33, 33, 33}); } catch (Exception ignored) {}
        table.setSpacingBefore(5);
        table.setSpacingAfter(15);

        String scoreVal = score != null && score.getOverallScore() != null
                ? String.format("%.0f/100", score.getOverallScore().doubleValue()) : "N/A";
        String riskLvl  = score != null && score.getRiskLevel() != null ? safe(score.getRiskLevel()) : "N/A";
        String decision = rec != null && rec.getDecision() != null ? safe(rec.getDecision()) : "N/A";

        table.addCell(createKpiCell("SCORE GLOBAL CDC", scoreVal, getScoreColor(score)));
        table.addCell(createKpiCell("NIVEAU DE RISQUE", riskLvl, getScoreColor(score)));
        table.addCell(createKpiCell("DÉCISION DE CRÉDIT", decision, getDecisionColor(rec)));

        doc.add(table);
    }

    private PdfPCell createKpiCell(String label, String value, Color color) {
        PdfPTable inner = new PdfPTable(1);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, GRAY_500)));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setPaddingBottom(5);
        inner.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color)));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        inner.addCell(valueCell);

        PdfPCell outerCell = new PdfPCell(inner);
        outerCell.setPadding(10);
        outerCell.setBorderColor(GRAY_200);
        outerCell.setBorderWidth(1f);
        outerCell.setBorderWidthTop(3f);
        outerCell.setBorderColorTop(color);
        return outerCell;
    }

    private Color getScoreColor(ScoreDTO score) {
        if (score == null || score.getOverallScore() == null) return GRAY_500;
        double s = score.getOverallScore().doubleValue();
        if (s >= 75) return GREEN_600;
        if (s >= 40) return ORANGE_500;
        return RED_600;
    }

    private Color getDecisionColor(RecommendationDTO rec) {
        if (rec == null || rec.getDecision() == null) return GRAY_500;
        if (rec.getDecision().startsWith("ACCORD")) return GREEN_600;
        if ("REFUS".equals(rec.getDecision())) return RED_600;
        return ORANGE_500;
    }
}
