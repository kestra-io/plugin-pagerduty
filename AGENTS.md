# Kestra PagerDuty Plugin

## What

- Provides plugin components under `io.kestra.plugin.pagerduty`.
- Includes classes such as `PagerDutyTemplate`, `PagerDutyAlert`, `PagerDutyExecution`.

## Why

- This plugin integrates Kestra with PagerDuty.
- It provides tasks that send events to PagerDuty.

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
