package dev.heisen.api.service;

import dev.heisen.api.exception.JobNotFoundException;
import dev.heisen.api.dto.JobRequest;
import dev.heisen.api.dto.JobResponse;
import dev.heisen.api.dto.JobResultResponse;
import dev.heisen.api.dto.JobStatusResponse;
import dev.heisen.api.event.JobEvent;
import dev.heisen.api.model.Job;
import dev.heisen.api.model.JobStatus;
import dev.heisen.api.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobService {

    private static final String KAFKA_TOPIC = "job-create";
    private final KafkaService kafkaService;
    private final JobRepository jobRepository;

    public JobResponse create(JobRequest jobRequest) {
        log.info("Creating new job with request {}", jobRequest);

        UUID jobId = UUID.randomUUID();
        Instant now = Instant.now();

        Job job = Job.builder()
                .id(jobId)
                .lang(jobRequest.lang())
                .status(JobStatus.PENDING)
                .code(jobRequest.code())
                .stdin(jobRequest.stdin())
                .createdAt(now)
                .build();
        jobRepository.save(job);

        JobEvent event = JobEvent.builder()
                .jobId(jobId)
                .lang(jobRequest.lang())
                .codeRef("db://jobs/" + jobId)
                .createdAt(now)
                .build();
        kafkaService.sendMessage(KAFKA_TOPIC, event);

        return new JobResponse(jobId);
    }

    public JobStatusResponse getStatus(UUID id) {
        log.info("Getting job status with id {}", id);

        Job job = jobRepository.findById(id).orElseThrow(
                () -> new JobNotFoundException("Job with id " + id + " not found")
        );

        return new JobStatusResponse(job.getStatus());
    }

    public JobResultResponse getResult(UUID id) {
        log.info("Getting job result with id {}", id);

        Job job = jobRepository.findById(id).orElseThrow(
                () -> new JobNotFoundException("Job with id " + id + " not found")
        );

        return new JobResultResponse(
                job.getStdout(),
                job.getExitCode()
        );
    }
}
