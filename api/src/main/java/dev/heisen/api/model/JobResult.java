package dev.heisen.api.model;

import jakarta.persistence.Lob;
import lombok.Builder;

import java.time.Instant;

@Builder
public class JobResult {
    @Lob
    private String stdout;
    private int exitCode;
    private Instant createdAt;
    private Instant finishedAt;
}
