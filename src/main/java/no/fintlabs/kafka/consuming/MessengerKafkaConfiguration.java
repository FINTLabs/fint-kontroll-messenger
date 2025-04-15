package no.fintlabs.kafka.consuming;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.topic.EventTopicService;
import no.fintlabs.kafka.topic.configuration.CleanupFrequency;
import no.fintlabs.kafka.topic.configuration.EventTopicConfiguration;
import no.fintlabs.kafka.topic.name.EventTopicNameParameters;
import no.fintlabs.kafka.topic.name.TopicNamePrefixParameters;
import no.fintlabs.message.Message;
import no.fintlabs.slack.SlackMessenger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@Lazy(value = false)
public class MessengerKafkaConfiguration {

    private final SlackMessenger slackMessenger;
    private final EventTopicService eventTopicService;

    public MessengerKafkaConfiguration(SlackMessenger slackMessenger, EventTopicService eventTopicService) {
        this.slackMessenger = slackMessenger;
        this.eventTopicService = eventTopicService;
    }

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConcurrentMessageListenerContainer<String, Message> messageConsumer(
            ParameterizedListenerContainerFactoryService parameterizedListenerContainerFactoryService
    ) {
        TopicNamePrefixParameters topicNamePrefixParameters = TopicNamePrefixParameters.builder()
                .orgIdApplicationDefault()
                .domainContextApplicationDefault()
                .build();

        EventTopicNameParameters eventTopicNameParameters = EventTopicNameParameters
                .builder()
                .eventName("fint-kontroll-messenger")
                .topicNamePrefixParameters(topicNamePrefixParameters)
                .build();

        EventTopicConfiguration eventTopicConfiguration = EventTopicConfiguration.builder()
                .retentionTime(Duration.ZERO)
                .cleanupFrequency(CleanupFrequency.NORMAL)
                .build();

        eventTopicService.createOrModifyTopic(eventTopicNameParameters, eventTopicConfiguration);

        ListenerConfiguration listenerConfiguration = ListenerConfiguration.builder()
                .seekingOffsetResetOnAssignment(false)
                .maxPollRecords(100)
                .build();

        ConcurrentMessageListenerContainer<String, Message> container = parameterizedListenerContainerFactoryService.createRecordListenerContainerFactory(
                        Message.class,
                        this::processMessage,
                        listenerConfiguration)
                .createContainer(eventTopicNameParameters);

        container.setAutoStartup(true);

        return container;
    }

    public void processMessage(ConsumerRecord<String, Message> consumerRecord) {
        Message message = consumerRecord.value();

        try {

            log.info("Received message {}", message);

            if (message.getType().equalsIgnoreCase("slackinfo")) {
                slackMessenger.sendMessage(message.getMessage());
            } else if (message.getType().equalsIgnoreCase("slackerror")) {
                slackMessenger.sendErrorMessage(message.getMessage());
            } else {
                log.warn("Unknown message type: {}, message: {}", message.getType(), message.getMessage());
            }

        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }

    @PostConstruct
    public void printGroupInfo() {
        log.info("✅ Kafka group ID: {}", groupId);
    }
}
