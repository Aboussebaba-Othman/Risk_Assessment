package com.riskassessment.report.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.riskassessment.report.dto.CompanyDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;


@Component
public class CompanyProfileRenderer extends BasePdfRenderer {

    public void render(Document doc, CompanyDTO company) throws DocumentException {
        doc.add(new Paragraph("1. Profil de l'Entreprise", h2()));
        doc.add(Chunk.NEWLINE);

        if (company == null) {
            doc.add(new Paragraph("Informations indisponibles.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, RED_600)));
            return;
        }

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        try { t.setWidths(new float[]{40, 60}); } catch (Exception ignored) {}
        t.setSpacingAfter(15);

        addRow(t, "Raison Sociale", safe(company.getName()));
        addRow(t, "Identifiant Fiscal (ICE)", safe(company.getTaxId()));
        addRow(t, "Registre du Commerce", safe(company.getRegistrationNumber()));
        addRow(t, "Forme Juridique", safe(company.getLegalForm()));
        addRow(t, "Secteur d'Activité", safe(company.getIndustry()));
        addRow(t, "Date de Création", company.getIncorporationDate() != null
                ? company.getIncorporationDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        addRow(t, "Localisation", safe(company.getCity()) + ", " + safe(company.getCountry()));

        doc.add(t);
    }

    private void addRow(PdfPTable t, String label, String value) {
        PdfPCell lc = new PdfPCell(new Phrase(label, h3()));
        lc.setBackgroundColor(GRAY_100);
        lc.setPadding(8);
        lc.setBorderColor(java.awt.Color.WHITE);
        lc.setBorderWidth(1f);
        t.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(value, body()));
        vc.setBackgroundColor(java.awt.Color.WHITE);
        vc.setPadding(8);
        vc.setBorderColor(GRAY_200);
        vc.setBorderWidth(0);
        vc.setBorderWidthBottom(1f);
        t.addCell(vc);
    }
}
