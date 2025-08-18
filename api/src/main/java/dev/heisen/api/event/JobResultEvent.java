package dev.heisen.api.event;

import dev.heisen.api.model.JobStatus;

import java.time.Instant;
import java.util.UUID;

public record JobResultEvent(
       UUID id,
       JobStatus status,
       String stdout,
       String stderr,
       Integer exitCode,
       Instant finishedAt
) {
}