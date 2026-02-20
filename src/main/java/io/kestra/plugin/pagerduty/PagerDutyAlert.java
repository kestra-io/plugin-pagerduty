package io.kestra.plugin.pagerduty;

import io.kestra.core.http.HttpRequest;
import io.kestra.core.http.HttpResponse;
import io.kestra.core.http.client.HttpClient;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.VoidOutput;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.net.URI;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Send PagerDuty alert from errors task",
    description = "Posts a raw Events API v2 payload from an `errors` handler to PagerDuty. Provide the Events endpoint URL and JSON payload (including `routing_key` and `event_action`). Keep credentials in secrets; see [PagerDuty documentation](https://developer.pagerduty.com/docs/ZG9jOjExMDI5NTgx-send-an-alert-event)."
)
@Plugin(
    examples = {
        @Example(
            title = "Send a PagerDuty alert on a failed flow execution.",
            full = true,
            code = """
                id: unreliable_flow
                namespace: company.team

                tasks:
                  - id: fail
                    type: io.kestra.plugin.scripts.shell.Commands
                    runner: PROCESS
                    commands:
                      - exit 1

                errors:
                  - id: alert_on_failure
                    type: io.kestra.plugin.pagerduty.PagerDutyAlert
                    url: "{{ secret('PAGERDUTY_EVENT') }}" # https://events.pagerduty.com/v2/enqueue
                    payload: |
                      {
                        "dedup_key": "samplekey",
                        "routing_key": "samplekey",
                        "event_action": "trigger",
                        "payload" : {
                            "summary": "PagerDuty alert",
                            "source": "kestra",
                            "severity": "error"
                        }
                      }
                """
        ),
        @Example(
            title = "Send a PagerDuty acknowledge from an errors handler.",
            full = true,
            code = """
                id: pagerduty_acknowledge
                namespace: company.team

                tasks:
                  - id: do_work
                    type: io.kestra.plugin.scripts.shell.Commands
                    commands:
                      - exit 1

                errors:
                  - id: acknowledge_existing_incident
                    type: io.kestra.plugin.pagerduty.PagerDutyAlert
                    url: "{{ secret('PAGERDUTY_EVENT') }}"
                    payload: |
                      {
                        "dedup_key": "existing-incident-key",
                        "routing_key": "samplekey",
                        "event_action": "acknowledge",
                        "payload": {
                          "summary": "Acknowledge incident from Kestra",
                          "source": "kestra",
                          "severity": "error"
                        }
                      }
                """
        ),
    },
    aliases = "io.kestra.plugin.notifications.pagerduty.PagerDutyAlert"
)
public class PagerDutyAlert extends AbstractPagerDutyConnection {

    @Schema(
        title = "PagerDuty Events API URL",
        description = "Endpoint such as `https://events.pagerduty.com/v2/enqueue`; store secrets with `secret()`."
    )
    @PluginProperty(dynamic = true)
    @NotBlank
    protected String url;

    @Schema(
        title = "PagerDuty message payload",
        description = "Raw JSON string sent to PagerDuty; must include `routing_key` and `event_action`. Supports templating."
    )
    protected Property<String> payload;

    @Override
    public VoidOutput run(RunContext runContext) throws Exception {
        String url = runContext.render(this.url);

        try (HttpClient client = new HttpClient(runContext, super.httpClientConfigurationWithOptions())) {
            String payload = runContext.render(this.payload).as(String.class).orElse(null);

            runContext.logger().debug("Send Discord webhook: {}", payload);
            HttpRequest.HttpRequestBuilder requestBuilder = createRequestBuilder(runContext)
                .addHeader("Content-Type", "application/json")
                .uri(URI.create(url))
                .method("POST")
                .body(HttpRequest.StringRequestBody.builder()
                    .content(payload)
                    .build());

            HttpRequest request = requestBuilder.build();

            HttpResponse<String> response = client.request(request, String.class);

            runContext.logger().debug("Response: {}", response.getBody());

            if (response.getStatus().getCode() == 200) {
                runContext.logger().info("Request succeeded");
            }
        }
        return null;
    }
}
