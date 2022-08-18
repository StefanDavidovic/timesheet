package com.example.internship.controller;

import com.example.internship.dto.PasswordResetDTO;
import com.example.internship.dto.TeamMemberDTO;
import com.example.internship.model.TeamMember;
import com.example.internship.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.mail.MessagingException;
import java.util.*;

@RestController
@RequestMapping("/api/teamMembers")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;


    @GetMapping()
    public ResponseEntity<List<TeamMemberDTO>> getTeamMembers(){
        return new ResponseEntity<>(teamMemberService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/pageable")
    public ResponseEntity<Map<String, Object>> getAllTeamMembers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(teamMemberService.findPageable(size, page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamMember(@PathVariable(value="id") Long id) {
        return new ResponseEntity<>(teamMemberService.findByIdFromEmployees(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity saveTeamMember(@RequestBody TeamMemberDTO teamMemberDTO){
        return new ResponseEntity(teamMemberService.save(teamMemberDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamMember> updateTeamMember(@RequestBody TeamMemberDTO teamMemberDTO){
        return new ResponseEntity<>(teamMemberService.update(teamMemberDTO),  HttpStatus.OK);
    }

    @PutMapping("/password/{email}")
    public ResponseEntity<TeamMember> changePassowrd(@RequestBody PasswordResetDTO passwordResetDTO) throws MessagingException {
        return new ResponseEntity<>(teamMemberService.changePassword(passwordResetDTO),  HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeamMember(@PathVariable(value="id") Long id) {
        teamMemberService.delete(id);
        return new ResponseEntity<>("Member with id " + id + "was successfully deleted", HttpStatus.OK);
    }

}
