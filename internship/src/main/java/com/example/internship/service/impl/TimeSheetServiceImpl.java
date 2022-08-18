package com.example.internship.service.impl;

import com.example.internship.dto.*;
import com.example.internship.exception.BadRequestException;
import com.example.internship.exception.OptimisticLockConflictException;
import com.example.internship.exception.ResourceNotFoundException;
import com.example.internship.helper.PDFGenerator;
import com.example.internship.helper.ExcelFileExport;
import com.example.internship.model.TimeSheet;
import com.example.internship.repository.CategoryRepo;
import com.example.internship.repository.ClientRepo;
import com.example.internship.repository.ProjectRepo;
import com.example.internship.repository.TimeSheetRepo;
import com.example.internship.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSheetServiceImpl implements TimeSheetService {
    @Autowired
    private Logger log;
    private final TimeSheetRepo timeSheetRepo;
    private final ClientRepo clientRepo;
    private final ProjectRepo projectRepo;
    private final CategoryRepo categoryRepo;


    @Override
    public List<TimeSheet> findAll(TimeSheetAllRequestDTO timeSheetDto) {
        var sheets= timeSheetRepo.findAll();
        if(sheets.isEmpty() || sheets == null){
            throw new ResourceNotFoundException("Not found any time sheet in DB");
        }

        var filteredSheets =  sheets.stream()
                .filter(sheet -> sheet.getProject().getTeamMember().getId().equals(timeSheetDto.getId()))
                .filter(sheet -> sheet.getDate().getYear() == timeSheetDto.getDate().getYear())
                .filter(sheet -> sheet.getDate().getMonth().equals(timeSheetDto.getDate().getMonth()))
                .collect(Collectors.toList());
        log.info("All filtered sheets returned");
        return filteredSheets;
    }

    @Override
    public List<TimeSheet> findAll() {
        return timeSheetRepo.findAll();
    }

    @Override
    public List<ReportResponseDTO> findReports(ReportRequestDTO reportDTO) {

        var sheets = timeSheetRepo.findAll();

        if(sheets.isEmpty()){
            throw new ResourceNotFoundException("Not found any report in DB");
        }

        var filteredReports = sheets.stream()
                .filter(tss -> reportDTO.getClient() == null || reportDTO.getClient().equals(tss.getClient().getId()))
                .filter(tss -> reportDTO.getTeamMember() == null || tss.getProject().getTeamMember().getId().equals(reportDTO.getTeamMember()))
                .filter(tss -> reportDTO.getProject() == null || tss.getProject().getId().equals(reportDTO.getProject()))
                .filter(tss -> reportDTO.getCategory() == null || tss.getCategory().getId().equals(reportDTO.getCategory()))
                .filter(tss -> reportDTO.getStartDate() == null || reportDTO.getEndDate() != null && reportDTO.getStartDate().isBefore(tss.getDate()) && reportDTO.getEndDate().isAfter(tss.getDate()))
                .map(tss -> new ReportResponseDTO(tss.getId(),tss.getDate(), tss.getProject().getTeamMember(), tss.getProject(), tss.getCategory(), tss.getDescription(), tss.getTime()))
                .collect(Collectors.toList());
        log.info("All filtered reports returned");
        return filteredReports;
    }

    public List<ReportExportResponseDTO> findReportsForExport(ReportRequestDTO reportDTO) {

        var sheets = timeSheetRepo.findAll();

        var filteredReports = sheets.stream()
                .filter(tss -> reportDTO.getClient() == null || reportDTO.getClient().equals(tss.getClient().getId()))
                .filter(tss -> reportDTO.getTeamMember() == null || tss.getProject().getTeamMember().getId().equals(reportDTO.getTeamMember()))
                .filter(tss -> reportDTO.getProject() == null || tss.getProject().getId().equals(reportDTO.getProject()))
                .filter(tss -> reportDTO.getCategory() == null || tss.getCategory().getId().equals(reportDTO.getCategory()))
                .filter(tss -> reportDTO.getStartDate() == null || reportDTO.getEndDate() != null && reportDTO.getStartDate().isBefore(tss.getDate()) && reportDTO.getEndDate().isAfter(tss.getDate()))
                .map(tss -> new ReportExportResponseDTO(tss.getId(),tss.getDate(), tss.getProject().getTeamMember().getUsername(), tss.getProject().getName(), tss.getCategory().getName(), tss.getDescription(), tss.getTime()))
                .collect(Collectors.toList());
        log.info("All filtered reports returned");

        return filteredReports;
    }

    @Override
    public TimeSheet findById(Long id) {
        var timeSheet = timeSheetRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Time Sheet with id: " + id));
        log.info("Found time sheet with id: " + id);
        return timeSheet;
    }

    @Override
    public TimeSheet save(TimeSheetDTO timeSheetDTO) {
        if(timeSheetDTO.getDate() == null || timeSheetDTO.getProject() == null || timeSheetDTO.getCategory() == null || timeSheetDTO.getClient() == null){
            throw new BadRequestException("Some input fields are empty, check it");
        }

        var timeSheet = new TimeSheet();
        BeanUtils.copyProperties(timeSheetDTO, timeSheet);

        var client = clientRepo.findById(timeSheetDTO.getClient()).orElseThrow(() -> new ResourceNotFoundException("Not found client with id: " + timeSheetDTO.getClient()));
        var project = projectRepo.findById(timeSheetDTO.getProject()).orElseThrow(() -> new ResourceNotFoundException("Not found project with id: " + timeSheetDTO.getProject()));
        var category = categoryRepo.findById(timeSheetDTO.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Not found category with id: " + timeSheetDTO.getCategory()));

        timeSheet.setCategory(category);
        timeSheet.setProject(project);
        timeSheet.setClient(client);
        log.info("Time sheet saved");
        return timeSheetRepo.save(timeSheet);
    }

    @Override
    public TimeSheet update(TimeSheetDTO timeSheetDTO) {
        try {
            if(timeSheetDTO.getId() == null || timeSheetDTO.getDate() == null || timeSheetDTO.getProject() == null || timeSheetDTO.getCategory() == null || timeSheetDTO.getClient() == null){
                throw new BadRequestException("Some input fields are empty, check it");
            }
            timeSheetRepo.findById(timeSheetDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Not found Time Sheet with id: " + timeSheetDTO.getId()));
            log.info("Found time sheet with id: " + timeSheetDTO.getId());

            var timeSheet = new TimeSheet();
            BeanUtils.copyProperties(timeSheetDTO, timeSheet);

            var client = clientRepo.findById(timeSheetDTO.getClient()).orElse(null);
            var project = projectRepo.findById(timeSheetDTO.getProject()).orElse(null);
            var category = categoryRepo.findById(timeSheetDTO.getCategory()).orElse(null);

            timeSheet.setCategory(category);
            timeSheet.setProject(project);
            timeSheet.setClient(client);

            return timeSheetRepo.save(timeSheet);
        }catch (OptimisticLockException e){
            throw new OptimisticLockConflictException("Optimistic lock exception");
        }
    }

    @Override
    public void delete(Long id) {
        timeSheetRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Time Sheet with id: " + id));
        log.info("Found time sheet with id: " + id);
        timeSheetRepo.deleteById(id);
        log.info("Time sheet with id: " + id + " was deleted");

    }

    @Override
    public ByteArrayInputStream returnBISForReports(ReportRequestDTO reportDTORequest) {
        var reports = findReportsForExport(reportDTORequest);
        var pdfGenerator = new PDFGenerator();
        var bis = pdfGenerator.reportsPdf(reports);
        return bis;
    }

    public ByteArrayInputStream excelExport(ReportRequestDTO reportDTORequest) {
        var reports = findReportsForExport(reportDTORequest);
        var excelFileExport = new ExcelFileExport();
        var bis = excelFileExport.reportsExcel(reports);
        return bis;
    }

    @Override
    public void returnCsv(HttpServletResponse response, ReportRequestDTO reportDTORequest) throws IOException {
        var reports = findReportsForExport(reportDTORequest);

        try(ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            String[] csvHeader = {"id", "date", "Team Member", "Project", "Category","Description", "Time"};
            String[] nameMapping = {"id", "date", "teamMember", "project", "category","description", "time"};

            csvWriter.writeHeader(csvHeader);

            for (ReportExportResponseDTO report : reports) {
                csvWriter.write(report, nameMapping);
            }
        }
    }
}
