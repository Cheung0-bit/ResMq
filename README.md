# ResMq 

[中文版](https://github.com/Cheung0-bit/ResMq/blob/master/README_CN.md)

ResMq, aka Message Queue based on Redis Stream.  This is a lightweight message queuing middleware. Heavily relies on Redis. 

For a long time, many developers used Redis as a simple message queue. They usually use Redis's List or Pub/Sub data type. But since Redis introduced the Stream type, it's perfectly suited for message queues. 

In order to get a better development experience, I packaged it into a more professional message queue middleware, adapted to Java17+,  SpringBoot3+

## Domain Model

![image-20240204144433585](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204144433585.png)

## Features

- **Light Weight** :  Rely only on Redis
- **Instant delivery** : Instant execute this message in the background
- **Delay delivery** :  Through Redis's ZSet data type, the message is sent periodically
- **Message Reliability** : Lua scripts ensure that messages are delivered from the producer to the Broker. Redis Stream provides the XACK mechanism to let consumers acknowledge the reception of messages. Consumed messages without ACK are added to the Pending Queue
- **Automatic ACK** : ResMq can automatically ACK when appropriate
- **DLQ** : Messages that trigger some threshold are put into a dead message queue for later processing by the developer
- **Message Multicast** : Messages can be delivered to different packets
- **Sequential consumption** : Messages can be consumed sequentially
- **Message Accumulation** : Redis Streams can always store messages. Developers can set an upper limit to trigger an automatic culling policy
- **Message durability** : Relying on the RDB/AOF persistence mechanism of Redis, the message loss can be guaranteed to a large extent, but it is not 100% guaranteed
- **Scalable** : Redis master-slave clusters and sentinel clusters can be set up to improve availability
- **Out of the box** : Adapt to SpringBoot3+, Annotating listening, Easy to use
- **Dashboard** : Do some simple monitoring

## Getting Started

### Dependceny

~~~xml
<dependency>
    <groupId>io.github.cheung0-bit</groupId>
    <artifactId>resmq-spring-boot-starter</artifactId>
    <version>${latestVersion}</version>
</dependency>
~~~

### Configuration

~~~yml
res-mq:
  # ------- Required -------
  enable: true
  # ------- Optional -------
  streams:
    email-send:
      topic: redis-topic
      group: default-group
    log-service:
      topic: sys-log
      group: default-group
    order-service:
      topic: order-service
      group: nanjing-group
    video-transcode:
      topic: video-transcode
      group: my-group
  max-queue-size: 1000
  dead-message-delivery-count: 1
  dead-message-delivery-second: 60
  dead-message-scheduled-thread-pool-core-size: 2
  dead-message-timer-initial-delay: 30
  dead-message-timer-delay: 30
  pending-messages-pull-count: 10
~~~

ResMq only works when enabled, and streams is configured with simple business topics and groups. What are the simple business topics and groupings described in the Consumer section

### Producer

Define a message entity class first

~~~java
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Email {
    String title;
    String text;
    String author;
}
~~~

Send the message

~~~java
@Resource
private ResMqTemplate resMqTemplate; // io.github.resmq.core.template.ResMqTemplate

// Sync Send
public void sendRedisMessage() {
    Email email = new Email("test email", "nothing", "bruce");
    try {
        resMqTemplate.syncSend("redis-topic", email);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Sync Delay Send
public void sendRedisDelayMessage() throws InterruptedException {
    Email email = new Email("test email " + i + j, "something", "bruce");
    try {
        resMqTemplate.syncDelaySend("redis-topic", email, 10, TimeUnit.SECONDS);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
~~~

### Consumer

When your business is very simple and clear, like RabbitMQ's [Hello World](https://rabbitmq.com/tutorials/tutorial-one-java.html) pattern, you can choose to configure the business name directly in the yml file, as well as the topic and group information under the business name

Like this :

~~~yml
  streams:
    email-send:
      topic: redis-topic
      group: default-group
~~~

This indicates that there is an email-send bussiness, and redis provide the topic queue and  group to work. In this case, the producer and consumer can do one-to-one consumption (since there is only one group, double consumption is not possible). So we can code like this

~~~java
public static final String BUSINESS_NAME = "email-send";

@ResMqListener(name = BUSINESS_NAME)
public void receiveByName(Email email) {
    log.info("[{}]Receive Message--->{}", "through business name", email);
    // ...
}
~~~

It's also convenient to configure topics and groups directly via annotations

~~~java
@ResMqListener(topic = "redis-topic", group = "group1")
public void receiveByTopicGroup1(Email email) {
    log.info("[{}]Receive Message--->{}", "through topic1 group1", email);
    // ...
}

@ResMqListener(topic = "redis-topic", group = "group2")
public void receiveByTopicGroup2(Email email) {
    log.info("[{}]Receive Message--->{}", "through topic1 group2", email);
    // ...
}
~~~

## DashBoard

url : http://{ip}:{port}

![image-20240204151618885](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204151618885.png)

![image-20240204151634562](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204151634562.png)

![image-20240204151758345](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204151758345.png)