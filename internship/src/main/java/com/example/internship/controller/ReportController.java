package com.example.internship.controller;

import com.example.internship.dto.ReportRequestDTO;
import com.example.internship.dto.ReportResponseDTO;
import com.example.internship.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TimeSheetService timeSheetService;

    @GetMapping
    public ResponseEntity<List<ReportResponseDTO>> filterReports(ReportRequestDTO reportDTO){
            return new ResponseEntity(timeSheetService.findReports(reportDTO), HttpStatus.OK);
    }

    @GetMapping("/csv")
    public void getAllEmployeesInCsv(HttpServletResponse response, ReportRequestDTO reportDTORequest) throws IOException {
        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=reports.csv";
        response.setHeader(headerKey, headerValue);

        timeSheetService.returnCsv(response,reportDTORequest);
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> reportsPdf(ReportRequestDTO reportDTORequest) throws IOException{

        var bis = timeSheetService.returnBISForReports(reportDTORequest);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reports.pdf");

        return ResponseEntity.ok().headers(headers).contentType
                        (MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping(value = "/excel")
    public void reportsExcel(HttpServletResponse response, ReportRequestDTO reportDTORequest) throws IOException{
        var bis = timeSheetService.excelExport(reportDTORequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=reports.xlsx");
        IOUtils.copy(bis, response.getOutputStream());
    }
}
