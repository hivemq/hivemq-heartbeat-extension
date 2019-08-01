== HiveMQ Heartbeat Extension

This extension allows integration with with load balancers and proxies.

=== How it works

This extension provides a readiness check via HTTP, which means a service is able to
detect if a HiveMQ instance is
offline and the load balancer is thus able to remove the HiveMQ node from the load balancing.
HiveMQ 4.2.0 is required.

=== Installation
Unzip the file: `hivemq-heartbeat-extension-<version>-distribution.zip` to the directory: `<HIVEMQ_HOME>/extensions`
. A configuration file `extension-config.xml` can be found in the `hivemq-heartbeat-extension` folder.
The properties are preconfigured with standard settings and can be adapted to your needs (The meaning of the fields is explained below).
. Start HiveMQ.



=== Configuration

The Heartbeat extension uses its own configuration file 'extension-config.xml' which must be placed in the extensions folder of HiveMQ.

==== General Configuration

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


=== Metrics

This extension exposes a custom metric to HiveMQs holistic metric registry.

|===
| Metric name | Type | Description

| http-heartbeat-meter | Meter | A meter that shows the frequency of heartbeat requests
|===

== Contributing

If you want to contribute to HiveMQ Heartbeat Extension, see the link:CONTRIBUTING.md[contribution guidelines].

== License

HiveMQ Heartbeat Extension is licensed under the `APACHE LICENSE, VERSION 2.0`. A copy of the license can be found link:LICENSE.txt[here].

