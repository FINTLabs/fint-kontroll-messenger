package no.fintlabs.message;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.slack.SlackMessenger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Lazy(value = false)
public class MessageConsumer {
    private final SlackMessenger slackMessenger;

    public MessageConsumer(SlackMessenger slackMessenger) {
        this.slackMessenger = slackMessenger;
    }

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @KafkaListener(
            topics = "#{('${fint.kafka.topic.org-id}'.replace('.', '-') + '.' + '${fint.kafka.topic.domain-context}' + '.fint-kontroll-messages')}",
            groupId = "${fint.kafka.topic.org-id}"
    )
    public void listen(Message message, Acknowledgment ack) {
        try {
            log.info("Received message {}", message);

            if (message.getType().equalsIgnoreCase("slackinfo")) {
                slackMessenger.sendMessage(message.getMessage());
            } else if (message.getType().equalsIgnoreCase("slackerror")) {
                slackMessenger.sendErrorMessage(message.getMessage());
            } else {
                log.warn("Unknown message type: {}, message: {}", message.getType(), message.getMessage());
            }

            // Mark the message as processed
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }

    @PostConstruct
    public void printGroupInfo() {
        log.info("✅ Kafka group ID: {}", groupId);
    }
}
