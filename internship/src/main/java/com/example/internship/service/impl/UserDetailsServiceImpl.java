package com.example.internship.service.impl;

import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.security.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private Logger log;
    private final TeamMemberRepo teamMemberRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var teamMember = teamMemberRepo.findByUsername(username);
        log.info("Found team member with username: " + username);
        return MyUserDetails.build(teamMember);
    }
}
