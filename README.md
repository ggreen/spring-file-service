# Spring File Service


This project was developed to demonstrate file movement and integration
 using [VMware Data Solutions](https://www.vmware.com/solutions/data-solutions.html) and [Spring](https://spring.io/)

- High throughput file streaming
- Multi-site replication (hub-spoke) 
- Distribution of millions of small files
- Maintain file source directory structure 
- Low latency file data transfers


# Architecture

![architecture.png](docs/images/architecture.png)

## RabbitMQ

### High throughput file streaming

[RabbitMQ](https://www.rabbitmq.com/) support moving large number of small files.
The application consumer supports the [RabbitMQ streaming](https://www.rabbitmq.com/stream.html) that has [benchmarks to support throughput of millions messages](https://tanzu.vmware.com/content/blog/rabbitmq-event-streaming-broker) per second.

### Multi-site replication (hub-spoke)

RabbitMQ features such as [exchanges](https://www.rabbitmq.com/tutorials/amqp-concepts.html#exchanges), [routing with binding rules](https://www.rabbitmq.com/tutorials/tutorial-one-spring-amqp.html), site replication ([shovel](https://www.rabbitmq.com/shovel.html) and [federation](https://www.rabbitmq.com/federation.html)) make it a great solution for implementing hub and spoke integration patterns. RabbitMQ decouples producer and consumer applications. The application can be running on the same network or distributed over a Wide Area Network (WAN). This allows for better maintainability and extensibility compared to disk-based replication.

### Distribution of millions of small files

RabbitMQ supports high availability and fault tolerance for messaging.
Rabbit can be set up as a cluster within a single network. Outages to one or more RabbitMQ servers can be transparent to producer and consumer applications. 

In general, message utilize memory, disk and network resources. In RabbitMQ (ex: version RabbitMQ 11) the default max size if 134 MB. The messages should be less the maximum allowed size of 512 MB. See [RabbitMQ configuration](https://www.rabbitmq.com/configure.html). 

### Maintain file source directory structure

The [file-send-source](https://github.com/ggreen/spring-file-service/tree/main/applications/file-send-source) application  implementation maintains adds the file attributes to each message. This includes the absolute path, the relative path, in addition to the file counter.

### Low latency file data transfers

RabbitMQ supports [low latency event streaming](https://www.linkedin.com/learning/achieving-low-latency-data-with-edge-computing/rabbitmq?autoplay=true&resume=false).
It uses a push based model from producers to consumers.  

The file-consumer-sink saves meta-data to remember which files where send to RabbitMQ.
It uses [GemFire](https://www.vmware.com/products/gemfire.html) (based on [Apache Geode](https://www.linkedin.com/learning/achieving-low-latency-data-with-edge-computing/apache-geode?autoplay=true&resume=false)).
GemFire is an In-memory SQL-database. It provides high-performance real-time apps with an ultra-high speed, in-memory data and compute grid data processing.
