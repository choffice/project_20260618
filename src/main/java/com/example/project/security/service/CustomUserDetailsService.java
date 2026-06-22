package com.example.project.security.service;

import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import com.example.project.security.dto.AuthUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> result = userRepository.findByEmail(username);
    if (!result.isPresent()) {
      throw new UsernameNotFoundException("이메일을 확인해주세요");
    }
    User user = result.get();
    AuthUserDTO dto = new AuthUserDTO(
        user.getEmail(), user.getPassword(), user.getRoleSet().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet()),
        user.getUid(), user.getEmail(), user.getName(), user.getProfileImg(),
        user.getLocation(), user.getCategory(), user.getStatus()
    );

    return dto;
  }
}
