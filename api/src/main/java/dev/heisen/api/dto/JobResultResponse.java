package dev.heisen.api.dto;

public record JobResultResponse(
        String stdout,
        String stderr,
        Integer exitCode
) {
}
