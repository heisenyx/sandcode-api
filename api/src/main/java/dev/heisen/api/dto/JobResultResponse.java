package dev.heisen.api.dto;

public record JobResultResponse(
        String stdout,
        int exitCode
) {
}
