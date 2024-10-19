package ru.gavrilovegor519.tasks_user.dto.input;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDto {
    @NotEmpty(message = "Refresh token can't be empty")
    private String refreshToken;
}
