package com.example.internship.service;

import com.example.TeamMemberResponse;
import com.example.internship.dto.PasswordResetDTO;
import com.example.internship.dto.TeamMemberDTO;
import com.example.internship.model.TeamMember;
import com.google.protobuf.Descriptors;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;
public interface TeamMemberService {

    List<TeamMemberDTO> findAll();
    Map<String, Object> findPageable(int size, int page);
    TeamMember findById(Long id);
    TeamMemberDTO findByIdFromEmployees(Long id);
    TeamMember findByUsername(String username);
    TeamMember save(TeamMemberDTO teamMemberDTO);
    TeamMember update(TeamMemberDTO teamMemberDTO);
    TeamMember changePassword(PasswordResetDTO passwordResetDTO) throws MessagingException;
    void delete(Long id);
    boolean checkUserExistInEmployees(String email);

}
