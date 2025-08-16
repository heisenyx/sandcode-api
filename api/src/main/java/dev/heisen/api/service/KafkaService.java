package dev.heisen.api.service;

import dev.heisen.api.event.JobEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String, JobEvent> kafkaTemplate;

    public void sendMessage(String topic, JobEvent event) {
        log.info("Sending job request to topic {}", topic);
        kafkaTemplate.send(topic, event);
    }
}
