package no.fintlabs.message;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.kafka.producing.MessageProducer;
import no.fintlabs.slack.SlackMessenger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/messaging")
public class MessageController {
    private final SlackMessenger slackMessenger;
    private final MessageProducer messageProducer;

    public MessageController(SlackMessenger slackMessenger, MessageProducer messageProducer) {
        this.slackMessenger = slackMessenger;
        this.messageProducer = messageProducer;
    }

    @PostMapping("/message")
    public void sendMessage(@RequestParam String type, @RequestBody String message) {
        log.info("Sending message {} to {}", message, type);

        if (type.equalsIgnoreCase("slackinfo")) {
            slackMessenger.sendMessage(message);
        }
    }

    @PostMapping("/error")
    public void sendErrorMessage(@RequestParam String type, @RequestBody String message) {
        log.info("Sending error message {} to {}", message, type);

        if (type.equalsIgnoreCase("slackerror")) {
            slackMessenger.sendErrorMessage(message);
        }
    }

    @PostMapping("/testmessageevent")
    public void testMessageEvent(@RequestParam String type, @RequestBody String message) {
        log.info("Sending test message {} to {}", message, type);

        if(type == null || type.isEmpty() || message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Type and message cannot be null or empty");
        }

        messageProducer.publishMessage(type, message);
    }
}
