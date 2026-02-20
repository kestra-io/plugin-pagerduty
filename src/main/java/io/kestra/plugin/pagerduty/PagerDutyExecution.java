package io.kestra.plugin.pagerduty;

import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.VoidOutput;
import io.kestra.core.plugins.notifications.ExecutionInterface;
import io.kestra.core.plugins.notifications.ExecutionService;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Send PagerDuty alert for a flow run",
    description = "Posts execution details (UI link, IDs, namespace, flow name, start time, duration, and final status with failing task when present) to PagerDuty via the Events API v2. Use only in flows triggered by a [Flow trigger](https://kestra.io/docs/administrator-guide/monitoring#alerting); for `errors` handlers use [PagerDutyAlert](https://kestra.io/plugins/plugin-pagerduty/io.kestra.plugin.pagerduty.pagerdutyalert) instead. Defaults `executionId` to the current run."
)
@Plugin(
    examples = {
        @Example(
            title = "Send a PagerDuty notification on a failed flow execution.",
            full = true,
            code = """
                id: failure_alert
                namespace: company.team

                tasks:
                  - id: send_alert
                    type: io.kestra.plugin.pagerduty.PagerDutyExecution
                    url: "{{ secret('PAGERDUTY_EVENT') }}" # format: https://events.pagerduty.com/v2/enqueue
                    payloadSummary: "PagerDuty Alert"
                    routingKey: "routingkey"
                    eventAction: "trigger"
                    executionId: "{{trigger.executionId}}"

                triggers:
                  - id: failed_prod_workflows
                    type: io.kestra.plugin.core.trigger.Flow
                    conditions:
                      - type: io.kestra.plugin.core.condition.ExecutionStatus
                        in:
                          - FAILED
                          - WARNING
                      - type: io.kestra.plugin.core.condition.ExecutionNamespace
                        namespace: prod
                        prefix: true
                """
        )
    },
    aliases = "io.kestra.plugin.notifications.pagerduty.PagerDutyExecution"
)
public class PagerDutyExecution extends PagerDutyTemplate implements ExecutionInterface {
    @Builder.Default
    private final Property<String> executionId = Property.ofExpression("{{ execution.id }}");
    private Property<Map<String, Object>> customFields;
    private Property<String> customMessage;

    @Override
    public VoidOutput run(RunContext runContext) throws Exception {
        this.templateUri = Property.ofValue("pagerduty-template.peb");
        this.templateRenderMap = Property.ofValue(ExecutionService.executionMap(runContext, this));

        return super.run(runContext);
    }
}
