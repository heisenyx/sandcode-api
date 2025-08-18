package dev.heisen.api.event;

import dev.heisen.api.model.Language;
import lombok.Builder;

import java.util.UUID;

@Builder
public record JobCompileEvent(
        UUID jobId,
        Language lang,
        String code,
        String stdin
) {
}
