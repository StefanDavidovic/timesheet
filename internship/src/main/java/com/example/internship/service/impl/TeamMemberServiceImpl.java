package com.example.internship.service.impl;

import com.example.TeamMemberRequest;
import com.example.TeamMemberServiceGrpc;
import com.example.internship.config.GlobalConfig;
import com.example.internship.dto.PasswordResetDTO;
import com.example.internship.dto.TeamMemberDTO;
import com.example.internship.exception.BadRequestException;
import com.example.internship.exception.OptimisticLockConflictException;
import com.example.internship.exception.ResourceNotFoundException;
import com.example.internship.model.Role;
import com.example.internship.model.TeamMember;
import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.service.TeamMemberService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.persistence.OptimisticLockException;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    @Autowired
    private Logger log;

    private final TeamMemberRepo teamMemberRepo;

    private final RestTemplate restTemplate;
    private final PasswordEncoder encoder;
    private final SendMailService sendMail;
    @Autowired
    private GlobalConfig globalConfig;
    @GrpcClient("grpcClient")
    TeamMemberServiceGrpc.TeamMemberServiceBlockingStub synchronousClient;


    @Override
    public List<TeamMemberDTO> findAll() {
            var members = teamMemberRepo.findAll();
            if(members.isEmpty() || members == null){
                throw new ResourceNotFoundException("Not found any Team Member in DB");
            }

            var memberDTOS = new ArrayList<TeamMemberDTO>();

            members.stream().forEach(member -> {
                TeamMemberRequest teamMemberRequest = TeamMemberRequest.newBuilder().setEmail(member.getEmail()).build();
                var teamMemberFromEmployees = synchronousClient.getTeamMemberById(teamMemberRequest);
                TeamMemberDTO teamMemberDTO = new TeamMemberDTO(member.getId(),teamMemberFromEmployees.getName(),member.getUsername(), member.getHoursPerWeek(), member.getEmail(), member.getPassword(),member.getRole().toString(), member.isStatus(), member.isArchive());
                memberDTOS.add(teamMemberDTO);
            });
            log.info("All members returned");
            return memberDTOS;
    }

    @Override
    public Map<String, Object> findPageable(int size, int page) {

        var paging = PageRequest.of(page, size);

        var pageableMembers = teamMemberRepo.findAll(paging);
        var teamMembers = pageableMembers.getContent();

        if (teamMembers.isEmpty() || teamMembers == null) {
            throw new ResourceNotFoundException("Not found any Team Member in DB");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("teamMembers", teamMembers);
        response.put("currentPage", pageableMembers.getNumber());
        response.put("totalItems", pageableMembers.getTotalElements());
        log.info("All pageable members returned");
        return response;
    }

    @Override
    public TeamMember findById(Long id) {

        var teamMember = teamMemberRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with id: " + id));
        log.info("Found member with id: " + id);
        return teamMember;
    }

    @Override
    public TeamMemberDTO findByIdFromEmployees(Long id) {
        var member = teamMemberRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with id: " + id));

        TeamMemberRequest teamMemberRequest = TeamMemberRequest.newBuilder().setEmail(member.getEmail()).build();
        var teamMemberFromEmployees = synchronousClient.getTeamMemberById(teamMemberRequest);
        var name = teamMemberFromEmployees.getName();

        TeamMemberDTO teamMemberDTO = new TeamMemberDTO(member.getId(),name,member.getUsername(), member.getHoursPerWeek(), member.getEmail(), member.getPassword(), member.getRole().toString(), member.isStatus(), member.isArchive());
        return teamMemberDTO;
    }


    @Override
    public TeamMember findByUsername(String username) {
        return teamMemberRepo.findByUsername(username);
    }

    @Override
    public TeamMember save(TeamMemberDTO teamMemberDTO) {
        var checkUserInEmployee = checkUserExistInEmployees(teamMemberDTO.getEmail());
        if(checkUserInEmployee){
            if(teamMemberDTO.getEmail().length() == 0 || teamMemberDTO.getRole().length() == 0 || teamMemberDTO.getPassword().length() ==0 || teamMemberDTO.getUsername().length() ==0){
                throw new BadRequestException("Some input fields are empty, check it");
            }
            var teamMember = new TeamMember();
            BeanUtils.copyProperties(teamMemberDTO, teamMember);
            teamMember.setRole(Role.valueOf(teamMemberDTO.getRole()));
            teamMember.setPassword(encoder.encode(teamMemberDTO.getPassword()));
            return teamMemberRepo.save(teamMember);

        }else {
            log.warning("Member with id: " + teamMemberDTO.getId() + " does not found");
            throw new ResourceNotFoundException("User does not exist in Employees");
        }
    }

    @Override
    public TeamMember update(TeamMemberDTO teamMemberDTO) {
        try{
            if (teamMemberDTO.getId() == null || teamMemberDTO.getEmail().length() == 0 || teamMemberDTO.getRole().length() == 0 || teamMemberDTO.getName().length() == 0 || teamMemberDTO.getUsername().length() == 0) {
                throw new BadRequestException("Some input fields are empty, check it");
            }

            teamMemberRepo.findById(teamMemberDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with id: " + teamMemberDTO.getId()));
            log.info("Found member with id: " + teamMemberDTO.getId());

            var teamMember = new TeamMember();
            BeanUtils.copyProperties(teamMemberDTO, teamMember);
            teamMember.setRole(Role.valueOf(teamMemberDTO.getRole()));
            return teamMemberRepo.save(teamMember);
        }catch (OptimisticLockException e){
            throw new OptimisticLockConflictException("Optimistic lock exception");
        }


    }

    @Override
    public TeamMember changePassword(PasswordResetDTO passwordResetDTO) throws MessagingException {

        if (passwordResetDTO.getEmail() == null || passwordResetDTO.getNewPassword().length() == 0 || passwordResetDTO.getOldPassword().length() == 0 || passwordResetDTO.getOldPassword() ==null || passwordResetDTO.getNewPassword() == null) {
            throw new BadRequestException("Some input fields are empty, check it");
        }

        var teamMember= teamMemberRepo.findByEmail(passwordResetDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with email: " + passwordResetDTO.getEmail()));
        log.info("Found member with email: " + passwordResetDTO.getEmail());

        if(!BCrypt.checkpw(passwordResetDTO.getOldPassword(),teamMember.getPassword())){
            log.warning("Old password is not correct for email: " + passwordResetDTO.getEmail());
            throw new BadRequestException("Some input fields are incorrect, check it");
        }

        teamMember.setPassword(encoder.encode(passwordResetDTO.getNewPassword()));
        teamMemberRepo.save(teamMember);
        sendMail.send(passwordResetDTO.getEmail(), "internship@gmail.com");

        return teamMember;
    }

    @Override
    public void delete(Long id) {
        teamMemberRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with id: " + id));
        log.info("Found member with id: " + id);
        teamMemberRepo.deleteById(id);
        log.info("Member with id: " + id + " was deleted");
    }

    public boolean checkUserExistInEmployees(String email){
        try {
            var response = restTemplate.getForEntity(globalConfig.employeeUrl() + email, JsonNode.class);
            if (response != null){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}
