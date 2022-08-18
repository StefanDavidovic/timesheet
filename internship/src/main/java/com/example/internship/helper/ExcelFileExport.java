package com.example.internship.helper;

import com.example.internship.dto.ReportExportResponseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ExcelFileExport {

    @Autowired
    private Logger log;

    public ByteArrayInputStream reportsExcel(List<ReportExportResponseDTO> reports){
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Reports");
            Row row = sheet.createRow(0);
            AtomicInteger counter = new AtomicInteger(0);

            CellStyle headerCell = workbook.createCellStyle();
            headerCell.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Stream.of("Id", "Date", "Team Member", "Project", "Category", "Description", "Time").forEach(headerTitle ->
            {
                    Cell cell = row.createCell(counter.getAndIncrement());
                    cell.setCellValue(headerTitle);
                    cell.setCellStyle(headerCell);
            });

            for(int i = 0; i < reports.size(); i++) {
                Row dataRow = sheet.createRow(i+1);

                dataRow.createCell(0).setCellValue(reports.get(i).getId());
                dataRow.createCell(1).setCellValue(reports.get(i).getDate().toString());
                dataRow.createCell(2).setCellValue(reports.get(i).getTeamMember());
                dataRow.createCell(3).setCellValue(reports.get(i).getProject());
                dataRow.createCell(4).setCellValue(reports.get(i).getCategory());
                dataRow.createCell(5).setCellValue(reports.get(i).getDescription());
                dataRow.createCell(6).setCellValue(reports.get(i).getTime());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e){
            log.warning("Error during export excel file");
            return null;
        }
    }

}
