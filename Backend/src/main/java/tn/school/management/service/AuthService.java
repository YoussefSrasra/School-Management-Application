package tn.school.management.service;

import tn.school.management.dto.AuthRequest;
import tn.school.management.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
}