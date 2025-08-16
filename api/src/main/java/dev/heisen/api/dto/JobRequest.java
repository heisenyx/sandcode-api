package dev.heisen.api.dto;

import dev.heisen.api.model.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record JobRequest(

        @NotNull(message = "Language is not set")
        Language lang,

        @NotBlank
        @Size(max = 16000, message = "The length of the code should not exceed 16000 characters!")
        String code,

        @Nullable
        @Size(max = 1000, message = "The length of the input should not exceed 1000 characters")
        String stdin
) {
}
