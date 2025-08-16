package dev.heisen.api.controller;

import dev.heisen.api.dto.JobRequest;
import dev.heisen.api.dto.JobResponse;
import dev.heisen.api.dto.JobResultResponse;
import dev.heisen.api.dto.JobStatusResponse;
import dev.heisen.api.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class JobController {

    private final JobService jobService;

    @PostMapping(value = "/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request
    ) {

        JobResponse response = jobService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/status/{id}")
                .buildAndExpand(response.jobId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<JobStatusResponse> getJobStatus(
            @PathVariable UUID id
    ) {
        JobStatusResponse response = jobService.getStatus(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<JobResultResponse> getJobResult(
            @PathVariable UUID id
    ) {
        JobResultResponse response = jobService.getResult(id);
        return ResponseEntity.ok().body(response);
    }
}