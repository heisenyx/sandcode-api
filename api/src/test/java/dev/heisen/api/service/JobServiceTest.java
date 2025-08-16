package dev.heisen.api.service;

import dev.heisen.api.exception.JobNotFoundException;
import dev.heisen.api.dto.JobRequest;
import dev.heisen.api.dto.JobResponse;
import dev.heisen.api.dto.JobResultResponse;
import dev.heisen.api.dto.JobStatusResponse;
import dev.heisen.api.event.JobEvent;
import dev.heisen.api.model.Job;
import dev.heisen.api.model.JobStatus;
import dev.heisen.api.model.Language;
import dev.heisen.api.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private JobService jobService;

    @Test
    void testCreate_shouldSaveJob() {
        JobRequest jobRequest = new JobRequest(Language.JAVA, "System.out.println(\"Hello!\");", "");
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);

        JobResponse jobResponse = jobService.create(jobRequest);

        assertThat(jobResponse).isNotNull();
        verify(jobRepository).save(jobCaptor.capture());
        Job savedJob = jobCaptor.getValue();

        assertThat(savedJob).isNotNull();
        assertThat(savedJob.getId()).isEqualTo(jobResponse.jobId());
        assertThat(savedJob.getStatus()).isEqualTo(JobStatus.PENDING);
        assertThat(savedJob.getLang()).isEqualTo(Language.JAVA);
        assertThat(savedJob.getCode()).isEqualTo("System.out.println(\"Hello!\");");
    }

    @Test
    void testCreate_shouldSendKafkaEvent() {
        JobRequest jobRequest = new JobRequest(Language.JAVA, "System.out.println(\"Hello!\");", "");
        ArgumentCaptor<JobEvent> eventCaptor = ArgumentCaptor.forClass(JobEvent.class);

        JobResponse jobResponse = jobService.create(jobRequest);

        assertThat(jobResponse).isNotNull();
        verify(kafkaService).sendMessage(eq("job-create"), eventCaptor.capture());
        JobEvent savedEvent = eventCaptor.getValue();

        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.jobId()).isEqualTo(jobResponse.jobId());
        assertThat(savedEvent.lang()).isEqualTo(Language.JAVA);
    }

    @Test
    void testGetStatus_shouldReturnJobStatus_whenJobExists() {

        UUID jobId = UUID.randomUUID();
        Job job = Job.builder()
                .id(jobId)
                .status(JobStatus.PENDING)
                .lang(Language.JAVA)
                .createdAt(Instant.now())
                .code("System.out.println(\"Hello!\");")
                .stdin("")
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobStatusResponse response = jobService.getStatus(jobId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(JobStatus.PENDING);
    }

    @Test
    void testGetStatus_shouldThrowJobNotFoundException_whenJobNotFound() {

        UUID jobId = UUID.randomUUID();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> jobService.getStatus(jobId));
    }

    @Test
    void testGetResult_shouldReturnJobResult_whenJobExists() {

        UUID jobId = UUID.randomUUID();
        Job job = Job.builder()
                .id(jobId)
                .status(JobStatus.COMPLETED)
                .lang(Language.JAVA)
                .createdAt(Instant.now())
                .code("System.out.println(\"Hello!\");")
                .stdin("")
                .exitCode(0)
                .stdout("Success")
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobResultResponse response = jobService.getResult(jobId);

        assertThat(response).isNotNull();
        assertThat(response.exitCode()).isEqualTo(0);
        assertThat(response.stdout()).isEqualTo("Success");
    }

    @Test
    void testGetResult_shouldThrowJobNotFoundException_whenJobNotFound() {

        UUID jobId = UUID.randomUUID();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> jobService.getResult(jobId));
    }
}