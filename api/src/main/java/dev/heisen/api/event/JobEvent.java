package dev.heisen.api.event;

import dev.heisen.api.model.Language;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record JobEvent(
        UUID jobId,
        String userId,
        Language lang,
        String codeRef,
        Instant createdAt
) {
}
