package com.riskassessment.report.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.dto.AnalysisResultDTO;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class SwotSectionRenderer extends BasePdfRenderer {

    private static final String[]   TYPES  = {"STRENGTH", "WEAKNESS", "OPPORTUNITY", "THREAT"};
    private static final String[]   LABELS = {"FORCES (Strengths)", "FAIBLESSES (Weaknesses)", "OPPORTUNITÉS", "MENACES"};
    private static final Color[]    COLORS = {new Color(22, 163, 74), new Color(220, 38, 38), new Color(56, 189, 248), new Color(249, 115, 22)};

    public void render(Document doc, AnalysisDTO analysis) throws DocumentException {
        doc.add(new Paragraph("3. Synthèse Stratégique (SWOT)", h2()));
        doc.add(Chunk.NEWLINE);

        if (analysis == null) {
            doc.add(new Paragraph("Analyse SWOT indisponible ou non générée.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, RED_600)));
            return;
        }

        if (analysis.getOverallHealth() != null) {
            doc.add(new Paragraph("Santé Financière Globale : " + analysis.getOverallHealth(), h3()));
            doc.add(Chunk.NEWLINE);
        }

        if (analysis.getResults() != null && !analysis.getResults().isEmpty()) {
            for (int i = 0; i < TYPES.length; i++) {
                renderSwotGroup(doc, analysis.getResults(), TYPES[i], LABELS[i], COLORS[i]);
            }
        } else if (analysis.getSummary() != null && !analysis.getSummary().isBlank()) {
            doc.add(new Paragraph(analysis.getSummary(), body()));
        } else {
            doc.add(new Paragraph("Aucune donnée SWOT détectée.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, RED_600)));
        }
    }

    private void renderSwotGroup(Document doc, List<AnalysisResultDTO> allResults, String type, String label, Color color) throws DocumentException {
        List<AnalysisResultDTO> items = allResults.stream()
                .filter(r -> type.equals(r.getResultType()))
                .collect(Collectors.toList());
        if (items.isEmpty()) return;

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingBefore(10);
        PdfPCell hc = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        hc.setBackgroundColor(color); hc.setBorder(Rectangle.NO_BORDER); hc.setPadding(5);
        headerTable.addCell(hc);
        doc.add(headerTable);

        PdfPTable bodyTable = new PdfPTable(1);
        bodyTable.setWidthPercentage(100);
        bodyTable.setSpacingAfter(10);
        for (AnalysisResultDTO item : items) {
            PdfPCell bc = new PdfPCell();
            bc.setBackgroundColor(GRAY_100); bc.setBorderColor(GRAY_200);
            bc.setBorderWidth(1f); bc.setPadding(8);
            bc.addElement(new Paragraph(safe(item.getTitle()), bodyBold()));
            bc.addElement(new Paragraph(safe(item.getDescription()), body()));
            if (item.getRecommendation() != null && !item.getRecommendation().isBlank()) {
                Paragraph rec = new Paragraph("Recommandation : " + item.getRecommendation(), small());
                rec.setSpacingBefore(4);
                bc.addElement(rec);
            }
            bodyTable.addCell(bc);
        }
        doc.add(bodyTable);
    }
}
