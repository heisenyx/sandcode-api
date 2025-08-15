package dev.heisen.api.dto;

import dev.heisen.api.model.JobStatus;
import lombok.Builder;

public record JobStatusResponse(
        JobStatus status
) {
}
