package com.example.internship.controller;

import com.example.internship.dto.TimeSheetAllRequestDTO;
import com.example.internship.dto.TimeSheetDTO;
import com.example.internship.model.TimeSheet;
import com.example.internship.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeSheets")
@RequiredArgsConstructor
public class TimeSheetController {

    private final TimeSheetService timeSheetService;

    @GetMapping()
    public ResponseEntity<List<TimeSheet>> getTimeSheets(TimeSheetAllRequestDTO timeSheetDto) {
        return new ResponseEntity<>(timeSheetService.findAll(timeSheetDto), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TimeSheet>> getAllTimeSheets() {
        return new ResponseEntity<>(timeSheetService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSheet> getTimeSheet(@PathVariable(value="id") Long id){
        return new ResponseEntity<>(timeSheetService.findById(id),HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<TimeSheet> saveTimeSheet(@RequestBody TimeSheetDTO timeSheetDTO) {
        return new ResponseEntity(timeSheetService.save(timeSheetDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSheet> updateTimeSheet(@RequestBody TimeSheetDTO timeSheetDTO) {
        return new ResponseEntity<>(timeSheetService.update(timeSheetDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTimeSheet(@PathVariable(value="id") Long id) {
        timeSheetService.delete(id);
        return new ResponseEntity<>("TimeSheet with id " + id + "was successfully deleted", HttpStatus.OK);
    }
}
