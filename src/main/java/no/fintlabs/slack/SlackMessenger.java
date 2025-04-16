package no.fintlabs.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SlackMessenger {

    @Value("${fint.kontroll.slack.url:http://localhost}")
    private String slackUrl;

    @Value("${fint.kontroll.slack.enabled:false}")
    private boolean slackEnabled;

    @Value("${fint.kontroll.authorization.authorized-org-id:vigo.no}")
    private String authorizedOrgId;

    @Value("${fint.relations.default-base-url:localhost}")
    private String baseUrl;

    @Value("${fint.application-id:app}")
    private String app;

    private final RestClient restClient;

    public SlackMessenger(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(slackUrl).build();
    }

    public boolean sendErrorMessage(String message) {
        String channel = "fint-kontroll-errors";
        String msg = "🔺 " + authorizedOrgId + " - " + app + "🔺\n\n" + message;
        return sendMessage(new SlackMessage(getChannel(channel), "SlackMessageBot", ":disguised_face:" , msg));
    }

    public boolean sendMessage(String message) {
        String channel = "fint-kontroll-errors";
        String msg = "❇️ " + authorizedOrgId + " - " + app + "️ ❇️\n\n" + message;
        return sendMessage(new SlackMessage(getChannel(channel), "SlackMessageBot", ":disguised_face:" , msg));
    }

    private boolean isBeta() {
        log.info("Environment is: {}", baseUrl);
        return baseUrl.contains("localhost") || baseUrl.contains("beta");
    }

    private String getChannel(String channel) {
        return isBeta() ? "fint-kontroll-errors-beta" : channel;
    }

    private boolean sendMessage(SlackMessage slackMessage) {
        log.info("Sending Slack message {}", slackMessage.text());

        log.info("Slack url: {}", slackUrl);
        log.info("Slack enabled: {}", slackEnabled);

        if (slackEnabled) {
            try {
                restClient.post()
                        .uri(slackUrl, slackMessage);
                return true;
            } catch (Exception e) {
                log.warn("Unable to send Slack message " + slackMessage.text(), e);
            }
        } else {
            log.info("Slack sending disabled, slack message {}", slackMessage);
        }

        log.warn("Slack message not sent");
        return false;
    }
}
