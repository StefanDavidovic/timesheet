package com.example.internship.service;

import com.example.internship.dto.*;
import com.example.internship.model.TimeSheet;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface TimeSheetService {

    List<TimeSheet> findAll(TimeSheetAllRequestDTO timeSheetDto);

    List<TimeSheet> findAll();
    List<ReportResponseDTO> findReports(ReportRequestDTO reportDTO);
    List<ReportExportResponseDTO> findReportsForExport(ReportRequestDTO reportDTO);
    TimeSheet findById(Long id);


    TimeSheet save(TimeSheetDTO timeSheetDTO);

    TimeSheet update(TimeSheetDTO timeSheetDTO);

    void delete(Long id);

    ByteArrayInputStream returnBISForReports(ReportRequestDTO reportDTORequest);
    void returnCsv(HttpServletResponse response, ReportRequestDTO reportDTORequest) throws IOException;
    ByteArrayInputStream excelExport(ReportRequestDTO reportDTORequest);

}
