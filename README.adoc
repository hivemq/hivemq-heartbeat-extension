:hivemq-support: http://www.hivemq.com/support/
:hivemq-extension-download: https://www.hivemq.com/extension/heartbeat-extension/

= HiveMQ Heartbeat Extension

image:https://img.shields.io/badge/Extension_Type-Monitoring-orange?style=for-the-badge[Extension Type]
image:https://img.shields.io/github/v/release/hivemq/hivemq-heartbeat-extension?style=for-the-badge[GitHub release (latest by date),link=https://github.com/hivemq/hivemq-heartbeat-extension/releases/latest]
image:https://img.shields.io/github/license/hivemq/hivemq-heartbeat-extension?style=for-the-badge&color=brightgreen[GitHub,link=LICENSE]
image:https://img.shields.io/github/workflow/status/hivemq/hivemq-heartbeat-extension/CI%20Check/master?style=for-the-badge[GitHub Workflow Status (branch),link=https://github.com/hivemq/hivemq-heartbeat-extension/actions/workflows/check.yml?query=branch%3Amaster]

== Prerequisites

* HiveMQ Enterprise Edition (EE) 4.2.0 or later
* HiveMQ Community Edition (CE) 2020.1 or later

== Purpose

This extension allows integration with load balancers and proxies.
It provides a readiness check via HTTP, which means a service is able to detect if a HiveMQ instance is offline and the load balancer is thus able to remove the HiveMQ node from the load balancing.

== Installation

* Download the extension from the {hivemq-extension-download}[HiveMQ Marketplace^].
* Copy the content of the zip file to the `extensions` folder of your HiveMQ nodes.
* Modify the `extension-config.xml` file for your needs.

== Configuration

The Heartbeat extension uses its own configuration file 'extension-config.xml' which must be placed in the extensions folder of HiveMQ.

|===
| Config name | Required | Description

| <port> | no | The port on which the heartbeat HTTP service should listen. Default is 9090
| <bindAddress> | yes | The bind address of the heartbeat HTTP service. Use 0.0.0.0 if you want to listen on all interfaces.
| <path> | no | The Path where the heartbeat HTTP service is located. Default ist /heartbeat
|===

.Example Configuration
[source]
----
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<heartbeat-extension-configuration>
        <port>9090</port>
        <bind-address>0.0.0.0</bind-address>
        <path>/heartbeat</path>
</heartbeat-extension-configuration>
----

IMPORTANT: By Default the Heartbeat is available at http://MY-IP:9090/heartbeat

== Metrics

This extension exposes a custom metric to HiveMQs holistic metric registry.

|===
| Metric name | Type | Description

| http-heartbeat-meter | Meter | A meter that shows the frequency of heartbeat requests
|===

== Need Help?

If you encounter any problems, we are happy to help.
The best place to get in contact is our {hivemq-support}[support^].

== Contributing

If you want to contribute to HiveMQ Heartbeat Extension, see the link:CONTRIBUTING.md[contribution guidelines].

== License

HiveMQ Heartbeat Extension is licensed under the `APACHE LICENSE, VERSION 2.0`.
A copy of the license can be found link:LICENSE[here].
