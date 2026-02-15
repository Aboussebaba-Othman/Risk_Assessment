package com.riskassessment.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.dto.CompanyDTO;
import com.riskassessment.report.dto.ScoreDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    public byte[] generateRiskReport(CompanyDTO company, ScoreDTO score, AnalysisDTO analysis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Header
            addHeader(document, company);

            // 2. Executive Summary (Score)
            addExecutiveSummary(document, score);

            // 3. SWOT Analysis
            addSwotAnalysis(document, analysis);

            // 4. Footer / Disclaimer
            addFooter(document);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out.toByteArray();
    }

    private void addHeader(Document document, CompanyDTO company) throws DocumentException {
        // Logo Placeholder (Text for now)
        Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, java.awt.Color.DARK_GRAY);
        Paragraph logo = new Paragraph("RISK ASSESS", logoFont); // Fake logo
        logo.setAlignment(Element.ALIGN_RIGHT);
        document.add(logo);

        document.add(new Paragraph(" ")); // Spacer

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, com.lowagie.text.Font.UNDERLINE);
        Paragraph title = new Paragraph("Comprehensive Risk Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(Chunk.NEWLINE);

        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
        String companyName = (company != null && company.getName() != null) ? company.getName().toUpperCase()
                : "UNKNOWN COMPANY";
        Paragraph companyPara = new Paragraph("Target Entity: " + companyName, subtitleFont);
        companyPara.setAlignment(Element.ALIGN_CENTER);
        document.add(companyPara);

        document.add(Chunk.NEWLINE);
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);
    }

    private void addExecutiveSummary(Document document, ScoreDTO score) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, java.awt.Color.BLUE);
        document.add(new Paragraph("1. Executive Summary", sectionFont));
        document.add(Chunk.NEWLINE);

        if (score != null) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addCell(table, "Financial Health Score", true);
            addCell(table, formatScore(score.getOverallScore()) + " / 100", false);

            addCell(table, "Risk Categorization", true);
            addCell(table, safeString(score.getRiskLevel()), false);

            addCell(table, "Rating Agency Equiv.", true);
            addCell(table, safeString(score.getRiskRating()), false);

            document.add(table);
        } else {
            addWarning(document, "Scoring data is currently unavailable for this company.");
        }
        document.add(Chunk.NEWLINE);
    }

    private void addSwotAnalysis(Document document, AnalysisDTO analysis) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, java.awt.Color.BLUE);
        document.add(new Paragraph("2. SWOT Strategic Analysis", sectionFont));
        document.add(Chunk.NEWLINE);

        if (analysis != null && analysis.getSummary() != null && !analysis.getSummary().isEmpty()) {
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph summary = new Paragraph(analysis.getSummary(), bodyFont);
            summary.setIndentationLeft(20);
            document.add(summary);

            // If we had a recommendation field
            if (analysis.getRecommendation() != null) {
                document.add(Chunk.NEWLINE);
                Paragraph recTitle = new Paragraph("Strategic Recommendation:",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD));
                document.add(recTitle);
                document.add(new Paragraph(analysis.getRecommendation()));
            }

        } else {
            addWarning(document, "Automated SWOT analysis could not be generated due to insufficient data.");
        }
        document.add(Chunk.NEWLINE);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(new LineSeparator());

        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, java.awt.Color.GRAY);
        Paragraph info = new Paragraph("Generated on: " + java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                footerFont);
        info.setAlignment(Element.ALIGN_RIGHT);
        document.add(info);

        Paragraph disclaimer = new Paragraph(
                "CONFIDENTIAL - FOR INTERNAL USE ONLY. This report is generated automatically by the Risk Assessment Platform.",
                footerFont);
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimer);
    }

    private void addCell(PdfPTable table, String text, boolean isHeader) {
        Font font = isHeader ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, java.awt.Color.WHITE)
                : FontFactory.getFont(FontFactory.HELVETICA, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        if (isHeader) {
            cell.setBackgroundColor(new java.awt.Color(60, 100, 160)); // Professional Blue
        } else {
            cell.setBackgroundColor(new java.awt.Color(245, 245, 245)); // Light Gray
        }
        table.addCell(cell);
    }

    private void addWarning(Document document, String message) throws DocumentException {
        Font warnFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, java.awt.Color.RED);
        document.add(new Paragraph("Note: " + message, warnFont));
    }

    private String safeString(String input) {
        return input != null ? input : "N/A";
    }

    private String formatScore(java.math.BigDecimal score) {
        return score != null ? score.toPlainString() : "0";
    }
}
