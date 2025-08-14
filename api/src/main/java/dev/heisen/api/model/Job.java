package dev.heisen.api.model;

import jakarta.persistence.*;
import lombok.*; // Используем более точечные аннотации
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language lang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Lob
    @Column(nullable = false)
    private String code;

    @Lob
    private String stdin;

    @Embedded
    private JobResult result;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}