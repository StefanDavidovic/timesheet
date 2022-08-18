package com.example.billing.service;

import com.example.TimeSheetResponsee;
import com.example.billing.config.BillingConfig;
import com.example.billing.model.RoleEnum;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class BillingExport {
    private final TimeSheetService timeSheetService;
    private final BillingConfig billingConfig;

    public void exportBill() throws IOException {
        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            HashMap<RoleEnum, Double> roles = getPayRates();

            Iterator<TimeSheetResponsee> objects = timeSheetService.getTimeSheets();

            XSSFSheet spreadsheet = workbook.createSheet( " Billing ");

            objects.forEachRemaining(ob -> {
                Long id = ob.getId();
                String name = ob.getCategory().getName();
                Double time = Double.parseDouble(String.valueOf(ob.getTime()));
                Double pricePerHour = roles.get(RoleEnum.valueOf(name));

                Row dataRow = spreadsheet.createRow(Math.toIntExact(id++));

                Row row = spreadsheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("Id");

                cell = row.createCell(1);
                cell.setCellValue("Role");

                cell = row.createCell(2);
                cell.setCellValue("Price");

                dataRow.createCell(0).setCellValue(id);
                dataRow.createCell(1).setCellValue(name);
                dataRow.createCell(2).setCellValue(time*pricePerHour);
            });
            File file = new File(billingConfig.pathFile());
            file.setWritable(true);
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        }
    }

    private HashMap<RoleEnum, Double> getPayRates(){
        HashMap<RoleEnum, Double> roles = new HashMap<RoleEnum, Double>();
        roles.put(RoleEnum.QA, 15.5);
        roles.put(RoleEnum.BackEndDev, 19.5);
        roles.put(RoleEnum.FrontEndDev, 18.5);

        return roles;
    }
}
