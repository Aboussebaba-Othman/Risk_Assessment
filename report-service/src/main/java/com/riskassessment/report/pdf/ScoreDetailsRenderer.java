package com.riskassessment.report.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.riskassessment.report.dto.ScoreDTO;
import org.springframework.stereotype.Component;

import java.awt.Color;


@Component
public class ScoreDetailsRenderer extends BasePdfRenderer {

    private static final String[][] RISK_BANDS = {
        {"90+", "EXCELLENT", "22c55e", "Défaut <1%"},
        {"75-89", "FAIBLE (LOW_RISK)", "4ade80", "Défaut 1-3%"},
        {"60-74", "MODÉRÉ (MODERATE_RISK)", "facc15", "Défaut 3-8%"},
        {"40-59", "MOYEN (MEDIUM_RISK)", "fb923c", "Défaut 8-15%"},
        {"25-39", "ÉLEVÉ (HIGH_RISK)", "ef4444", "Défaut 15-30%"},
        {"0-24", "CRITIQUE", "991b1b", "Défaut >30%"}
    };

    public void render(Document doc, ScoreDTO score) throws DocumentException {
        doc.add(new Paragraph("2. Détails de l'Évaluation Financière", h2()));
        doc.add(Chunk.NEWLINE);

        if (score == null) {
            doc.add(new Paragraph("Aucun score généré pour cette entreprise.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, RED_600)));
            doc.add(Chunk.NEWLINE);
            return;
        }

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        try { t.setWidths(new float[]{40, 60}); } catch (Exception ignored) {}
        t.setSpacingAfter(15);

        addRow(t, "Date de Calcul", safe(score.getScoredAt()));
        addRow(t, "Méthodologie", safe(score.getScoringMethod()));
        addRow(t, "Note (Rating)", safe(score.getRiskRating()));
        if (score.getNotes() != null && !score.getNotes().isBlank()) {
            addRow(t, "Observation Système", score.getNotes());
        }
        doc.add(t);

        doc.add(new Paragraph("Échelle de Risque Associée (Modèle CDC) :", h3()));
        doc.add(Chunk.NEWLINE);
        doc.add(buildRiskBandLegend(score));
    }

    private PdfPTable buildRiskBandLegend(ScoreDTO score) throws DocumentException {
        PdfPTable legend = new PdfPTable(4);
        legend.setWidthPercentage(100);
        try { legend.setWidths(new float[]{15, 30, 20, 35}); } catch (Exception ignored) {}
        legend.setSpacingAfter(15);

        for (String[] b : RISK_BANDS) {
            Color bandColor = hexColor(b[2]);
            PdfPCell c1 = new PdfPCell(new Phrase(b[0], FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
            c1.setBackgroundColor(bandColor); c1.setBorder(Rectangle.NO_BORDER); c1.setPadding(5);
            legend.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase(b[1], bodyBold()));
            c2.setBackgroundColor(GRAY_100); c2.setBorder(Rectangle.NO_BORDER); c2.setPadding(5);
            legend.addCell(c2);

            PdfPCell c3 = new PdfPCell(new Phrase(b[3], small()));
            c3.setBackgroundColor(Color.WHITE); c3.setBorderColor(GRAY_200);
            c3.setBorderWidth(0); c3.setBorderWidthBottom(1f); c3.setPadding(5);
            legend.addCell(c3);

            boolean isActive = score.getRiskLevel() != null && score.getRiskLevel().contains(b[1].split(" ")[0]);
            if (isActive || (b[1].contains("CRITIQUE") && "CRITICAL".equals(score.getRiskLevel()))) {
                PdfPCell c4 = new PdfPCell(new Phrase("<-- Actuel", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, NAVY_900)));
                c4.setBorder(Rectangle.NO_BORDER); c4.setPadding(5);
                legend.addCell(c4);
            } else {
                PdfPCell c4 = new PdfPCell(); c4.setBorder(Rectangle.NO_BORDER);
                legend.addCell(c4);
            }
        }
        return legend;
    }

    private void addRow(PdfPTable t, String label, String value) {
        PdfPCell lc = new PdfPCell(new Phrase(label, h3()));
        lc.setBackgroundColor(GRAY_100); lc.setPadding(8);
        lc.setBorderColor(Color.WHITE); lc.setBorderWidth(1f);
        t.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(value, body()));
        vc.setBackgroundColor(Color.WHITE); vc.setPadding(8);
        vc.setBorderColor(GRAY_200); vc.setBorderWidth(0); vc.setBorderWidthBottom(1f);
        t.addCell(vc);
    }

    private Color hexColor(String hex) {
        try {
            return new Color(Integer.parseInt(hex.substring(0, 2), 16),
                    Integer.parseInt(hex.substring(2, 4), 16),
                    Integer.parseInt(hex.substring(4, 6), 16));
        } catch (Exception e) { return NAVY_900; }
    }
}
