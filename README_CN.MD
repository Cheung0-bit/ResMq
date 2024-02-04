# ResMq 

ResMq，即基于Redis Stream实现的消息队列。这是一个轻量级的消息队列中间件。严重依赖Redis。

很长一段时间以来，许多开发人员将Redis用作简单的消息队列。他们通常使用Redis的List或Pub/Sub数据类型。在Redis引入了Stream类型后，非常适合做消息队列。

为了获得更好的开发体验，我把它打包成一个更专业的消息队列中间件，适配Java17+、SpringBoot3+

## 领域模型

![image-20240204144433585](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204144433585.png)

## 功能特性

- **轻量级** :  仅依赖于Redis
- **及时投递** :  即时在后台执行此消息
- **延迟投递** :  通过Redis的ZSet数据类型，实现了延迟队列
- **消息可靠性** :  Lua脚本确保消息从生产者传递到代理。Redis Stream提供了XACK机制，让用户确认收到了消息。消费而没有ACK的消息被添加到Pending队列
- **自动确认** :  ResMq可以在适当的时机自动进行ACK
- **死信队列**:  触发某些阈值的消息被放入死消息队列，供开发人员后续处理
- **消息多播** :  消息可以传递给不同的分组
- **顺序消费** :  消息可以按顺序消费
- **消息堆积** :  Redis Stream可以存储消息。开发人员可以设置上限来触发自动剔除策略
- **消息不丢失** :  依靠Redis的RDB/AOF持久化机制，可以在很大程度上保证消息丢失，但不能100%保证
- **伸缩性**:  可以通过搭建Redis主从集群和哨兵集群来提高可用性
- **开箱即用** :  适应SpringBoot3+，注解式监听，easy code
- **监控看板**:  做一些简单的数据监控

## 快速开始

### 依赖

~~~xml
<dependency>
    <groupId>io.github.cheung0-bit</groupId>
    <artifactId>resmq-spring-boot-starter</artifactId>
    <version>${latestVersion}</version>
</dependency>
~~~

### 配置

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

ResMq只有在enable为true时才能工作，简单业务模式下，可以使用streams配置简单主题和分组。消费者部分中会具体描述什么是简单业务主题和分组

### 生产者

先定义一个实体类

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

发送消息

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

### 消费者

当你的业务非常简单明了时，比如RabbitMQ的[Hello World](https://rabbitmq.com/tutorials/tutorial-one-java.html)模式，你可以选择直接在yml文件中配置业务名称，以及业务名称下的主题和组信息

~~~yml
  streams:
    email-send:
      topic: redis-topic
      group: default-group
~~~

这表明存在电子邮件发送业务，redis提供了主题队列和组来工作。在这种情况下，生产者和消费者可以进行一对一的消费(因为只有一个组，所以不可能进行双重消费)。我们可以这样编码

~~~java
public static final String BUSINESS_NAME = "email-send";

@ResMqListener(name = BUSINESS_NAME)
public void receiveByName(Email email) {
    log.info("[{}]Receive Message--->{}", "through business name", email);
    // ...
}
~~~

通过注释直接配置主题和组也很方便

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

## 监控看板

url : http://{ip}:{port}

![image-20240204151618885](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204151618885.png)

![image-20240204151634562](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204151634562.png)

![image-20240204151758345](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20240204151758345.png)