package no.fintlabs.kafka.producing;

import no.fintlabs.kafka.model.ParameterizedProducerRecord;
import no.fintlabs.kafka.topic.EventTopicService;
import no.fintlabs.kafka.topic.configuration.CleanupFrequency;
import no.fintlabs.kafka.topic.configuration.EntityTopicConfiguration;
import no.fintlabs.kafka.topic.name.EntityTopicNameParameters;
import no.fintlabs.kafka.topic.name.EventTopicNameParameters;
import no.fintlabs.kafka.topic.name.TopicNamePrefixParameters;
import no.fintlabs.message.Message;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MessageProducer {
    private final ParameterizedTemplate<Message> parameterizedTemplate;

    public MessageProducer(ParameterizedTemplateFactory parameterizedTemplateFactory) {
        parameterizedTemplate = parameterizedTemplateFactory.createTemplate(Message.class);
    }

    public void publishMessage(String messageType, String message) {
        TopicNamePrefixParameters topicNamePrefixParameters = TopicNamePrefixParameters.builder()
                .orgIdApplicationDefault()
                .domainContextApplicationDefault()
                .build();

        EventTopicNameParameters eventTopicNameParameters = EventTopicNameParameters
                .builder()
                .eventName("fint-kontroll-messenger")
                .topicNamePrefixParameters(topicNamePrefixParameters)
                .build();

        Message publishMessage = new Message();
        publishMessage.setType(messageType);
        publishMessage.setMessage(message);

        parameterizedTemplate.send(ParameterizedProducerRecord.<Message>builder()
                                           .topicNameParameters(eventTopicNameParameters)
                                           .value(publishMessage)
                                           .build());
    }
}
