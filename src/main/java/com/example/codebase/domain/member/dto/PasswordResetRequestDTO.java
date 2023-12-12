package com.example.codebase.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public record PasswordResetRequestDTO (@NotBlank String oldPassword,
                                       @NotBlank String newPassword) {}