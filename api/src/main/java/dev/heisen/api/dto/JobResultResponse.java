package dev.heisen.api.dto;

import lombok.Builder;

public record JobResultResponse(
        String stdout,
        int exitCode
) {
}
