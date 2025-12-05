package tn.school.management.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.school.management.dto.AuthRequest;
import tn.school.management.dto.AuthResponse;
import tn.school.management.entity.Admin;
import tn.school.management.repository.AdminRepository;
import tn.school.management.security.CustomUserDetailsService;
import tn.school.management.security.JwtService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AdminRepository adminRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterAdminSuccessfully() {
        AuthRequest request = new AuthRequest("admin", "password");
        Admin admin = Admin.builder().username("admin").password("encodedPass").build();
        UserDetails userDetails = new User("admin", "encodedPass", Collections.emptyList());

        when(adminRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        AuthRequest request = new AuthRequest("admin", "password");
        UserDetails userDetails = new User("admin", "encodedPass", Collections.emptyList());

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("valid-token");

        AuthResponse response = authService.login(request);

        assertEquals("valid-token", response.getToken());
        verify(authenticationManager).authenticate(any());
    }
}