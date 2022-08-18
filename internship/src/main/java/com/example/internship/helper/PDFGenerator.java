package com.example.internship.helper;

import com.example.internship.dto.ReportExportResponseDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class PDFGenerator {
    @Autowired
    private Logger log;
    public ByteArrayInputStream reportsPdf(List<ReportExportResponseDTO> reports) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
            Paragraph para = new Paragraph("Reports", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            Stream.of("ID", "Date", "Team Member", "Project", "Category", "Description", "Time").forEach(headerTitle ->
            {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.
                        getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            for (ReportExportResponseDTO report : reports) {
                Stream.of(report.getId().toString(), report.getDate().toString(), report.getTeamMember(),report.getProject(), report.getCategory(), report.getDescription(),String.valueOf(report.getTime())).forEach(rep -> {
                    PdfPCell cell = createCell(rep);
                    table.addCell(cell);
                });
            }
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            log.warning("Error during export pdf file");
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public PdfPCell createCell(String phrase){
        PdfPCell cell = new PdfPCell(new Phrase(phrase));
        cell.setPaddingLeft(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        return cell;
    }
}