package dev.heisen.api.service;

import dev.heisen.api.event.JobResultEvent;
import dev.heisen.api.exception.JobNotFoundException;
import dev.heisen.api.dto.JobRequest;
import dev.heisen.api.dto.JobResponse;
import dev.heisen.api.dto.JobResultResponse;
import dev.heisen.api.dto.JobStatusResponse;
import dev.heisen.api.event.JobCompileEvent;
import dev.heisen.api.model.Job;
import dev.heisen.api.model.JobStatus;
import dev.heisen.api.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobService {

    @Value("${spring.kafka.producer.topic}")
    private String producerTopic;

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

        JobCompileEvent event = JobCompileEvent.builder()
                .jobId(jobId)
                .lang(jobRequest.lang())
                .code(jobRequest.code())
                .stdin(jobRequest.stdin())
                .build();
        kafkaService.sendMessage(producerTopic, event);

        return new JobResponse(jobId);
    }

    public JobStatusResponse getStatus(UUID id) {
        log.info("Getting job status with id {}", id);

        Job job = findJobById(id);

        return new JobStatusResponse(job.getStatus());
    }

    public JobResultResponse getResult(UUID id) {
        log.info("Getting job result with id {}", id);

        Job job = findJobById(id);

        return new JobResultResponse(
                job.getStdout(),
                job.getStderr(),
                job.getExitCode()
        );
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJobResult(JobResultEvent resultEvent) {
        log.info("Received Job Result Event {}", resultEvent);

        try {
            Job job = findJobById(resultEvent.id());

            job.setStatus(resultEvent.status());
            job.setStdout(resultEvent.stdout());
            job.setStderr(resultEvent.stderr());
            job.setExitCode(resultEvent.exitCode());
            job.setFinishedAt(resultEvent.finishedAt());

            jobRepository.save(job);
            log.info("Successfully updated job with id: {}", resultEvent.id());

        } catch (JobNotFoundException e) {
            log.error("Received result for non-existent job id: {}. Ignoring.", resultEvent.id(), e);
        } catch (Exception e) {
            log.error("Error processing job result event: {}", resultEvent, e);
        }
    }

    private Job findJobById(UUID id) {
        return jobRepository.findById(id).orElseThrow(
                () -> new JobNotFoundException("Job with id " + id + " not found")
        );
    }
}
