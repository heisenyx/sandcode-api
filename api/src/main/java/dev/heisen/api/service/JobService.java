package dev.heisen.api.service;

import dev.heisen.api.dto.JobRequest;
import dev.heisen.api.dto.JobResponse;
import dev.heisen.api.event.JobEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private static final String KAFKA_TOPIC = "job-create";
    private final KafkaService kafkaService;

    public JobResponse create(JobRequest jobRequest) {
        log.info("Creating new job with request {}", jobRequest);

        UUID uuid = UUID.randomUUID();

        JobEvent event = JobEvent.builder()
                .jobId(uuid)
                .userId("placeholder")
                .lang(jobRequest.lang())
                .codeRef("db://jobs/" + uuid)
                .createdAt(Instant.now())
                .build();

        kafkaService.sendMessage(KAFKA_TOPIC, event);

        return new JobResponse(uuid);
    }

//    public JobStatus getStatus(UUID id) {
//
//    }
//
//    public JobStatus getResult(UUID id) {
//
//    }
}
