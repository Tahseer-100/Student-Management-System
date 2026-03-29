package com.example.student.service;

import com.example.student.dto.AuthRequestDTO;
import com.example.student.dto.AuthResponseDTO;
import com.example.student.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(AuthRequestDTO request);
}