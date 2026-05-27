# How to use the PagerDuty plugin

Trigger PagerDuty incidents and send execution summaries from Kestra flows.

## Authentication

Set `url` to the PagerDuty Events API endpoint for your service integration. Store it in a [secret](https://kestra.io/docs/concepts/secret).

## Tasks

`PagerDutyAlert` triggers an alert as a step within a flow — set `payload` to a JSON body in the [PagerDuty Events API v2 format](https://developer.pagerduty.com/docs/ZG9jOjExMDI5NTgw-send-an-alert-event).

`PagerDutyExecution` sends a structured execution summary including status, duration, and an execution link, and is designed for use with a [Flow trigger](https://kestra.io/docs/workflow-components/triggers) in a dedicated monitoring namespace that watches other namespaces for failures.
