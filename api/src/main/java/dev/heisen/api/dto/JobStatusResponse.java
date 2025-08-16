package dev.heisen.api.dto;

import dev.heisen.api.model.JobStatus;

public record JobStatusResponse(
        JobStatus status
) {
}
