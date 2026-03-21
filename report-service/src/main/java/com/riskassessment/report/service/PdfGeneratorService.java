package com.riskassessment.report.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.dto.CompanyDTO;
import com.riskassessment.report.dto.RecommendationDTO;
import com.riskassessment.report.dto.ScoreDTO;
import com.riskassessment.report.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final CoverPageRenderer coverPageRenderer;
    private final ExecutiveSummaryRenderer executiveSummaryRenderer;
    private final CompanyProfileRenderer companyProfileRenderer;
    private final ScoreDetailsRenderer scoreDetailsRenderer;
    private final SwotSectionRenderer swotSectionRenderer;

    public byte[] generateRiskReport(CompanyDTO company, ScoreDTO score, RecommendationDTO rec, AnalysisDTO analysis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            coverPageRenderer.render(doc, company);
            doc.newPage();

            executiveSummaryRenderer.render(doc, score, rec);
            companyProfileRenderer.render(doc, company);
            scoreDetailsRenderer.render(doc, score);
            swotSectionRenderer.render(doc, analysis);
            addFooter(doc);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return out.toByteArray();
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(com.lowagie.text.Chunk.NEWLINE);
        doc.add(new com.lowagie.text.pdf.draw.LineSeparator(0.5f, 100f, new java.awt.Color(229, 231, 235), com.lowagie.text.Element.ALIGN_CENTER, -2));
        doc.add(com.lowagie.text.Chunk.NEWLINE);
        com.lowagie.text.Paragraph gen = new com.lowagie.text.Paragraph(
                "RiskAssess Platform — Propriété exclusive. Toute reproduction non autorisée est interdite.",
                com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 8, new java.awt.Color(107, 114, 128)));
        gen.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        doc.add(gen);
    }
}
