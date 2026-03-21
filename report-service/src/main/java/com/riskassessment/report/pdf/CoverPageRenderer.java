package com.riskassessment.report.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.riskassessment.report.dto.CompanyDTO;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.time.format.DateTimeFormatter;


@Component
public class CoverPageRenderer extends BasePdfRenderer {

    public void render(Document doc, CompanyDTO company) throws DocumentException {
        for (int i = 0; i < 6; i++) doc.add(Chunk.NEWLINE);

        Paragraph brand = new Paragraph("RISKASSESS PLATFORM", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BRAND_LIGHT));
        doc.add(brand);
        doc.add(new LineSeparator(2f, 100f, BRAND_LIGHT, Element.ALIGN_LEFT, -5));
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);

        Paragraph title = new Paragraph("RAPPORT D'ÉVALUATION DES RISQUES", titleFont());
        doc.add(title);
        Paragraph subTitle = new Paragraph("Rapport Institutionnel - Conforme CDC F-03", FontFactory.getFont(FontFactory.HELVETICA, 14, GRAY_500));
        subTitle.setSpacingBefore(10);
        doc.add(subTitle);

        for (int i = 0; i < 8; i++) doc.add(Chunk.NEWLINE);

        PdfPTable card = new PdfPTable(1);
        card.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setPadding(20);
        cell.setBorderColor(GRAY_200);
        cell.setBorderWidth(1f);
        cell.setBorderWidthLeft(4f);
        cell.setBorderColorLeft(NAVY_900);
        cell.setBackgroundColor(new Color(250, 250, 252));

        String name = (company != null && company.getName() != null) ? company.getName().toUpperCase() : "ENTREPRISE NON DÉFINIE";
        cell.addElement(new Paragraph("ENTITÉ CIBLE :", h3()));
        cell.addElement(new Paragraph(name, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, NAVY_900)));
        if (company != null && company.getTaxId() != null) {
            cell.addElement(new Paragraph("ICE : " + company.getTaxId(), body()));
        }
        card.addCell(cell);
        doc.add(card);

        for (int i = 0; i < 8; i++) doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("Édité le : " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), bodyBold()));
        doc.add(new Paragraph("DOCUMENT STRICTEMENT CONFIDENTIEL — USAGE INTERNE UNIQUEMENT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, RED_600)));

        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingBefore(10);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderColorBottom(GRAY_200);
        lineCell.setBorderWidthBottom(1f);
        line.addCell(lineCell);
        doc.add(line);
    }
}
