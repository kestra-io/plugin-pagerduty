# Kestra PagerDuty Plugin

## What

- Provides plugin components under `io.kestra.plugin.pagerduty`.
- Includes classes such as `PagerDutyTemplate`, `PagerDutyAlert`, `PagerDutyExecution`.

## Why

- What user problem does this solve? Teams need to send events to PagerDuty from orchestrated workflows instead of relying on manual console work, ad hoc scripts, or disconnected schedulers.
- Why would a team adopt this plugin in a workflow? It keeps PagerDuty steps in the same Kestra flow as upstream preparation, approvals, retries, notifications, and downstream systems.
- What operational/business outcome does it enable? It reduces manual handoffs and fragmented tooling while improving reliability, traceability, and delivery speed for processes that depend on PagerDuty.

## How

### Architecture

Single-module plugin. Source packages under `io.kestra.plugin`:

- `pagerduty`

Infrastructure dependencies (Docker Compose services):

- `app`

### Key Plugin Classes

- `io.kestra.plugin.pagerduty.PagerDutyAlert`
- `io.kestra.plugin.pagerduty.PagerDutyExecution`

### Project Structure

```
plugin-pagerduty/
├── src/main/java/io/kestra/plugin/pagerduty/
├── src/test/java/io/kestra/plugin/pagerduty/
├── build.gradle
└── README.md
```

## References

- https://kestra.io/docs/plugin-developer-guide
- https://kestra.io/docs/plugin-developer-guide/contribution-guidelines
