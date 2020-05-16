package com.task;

import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.amqp.AmqpConnection;
import org.vertx.java.platform.Verticle;


/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class PingVerticle extends Verticle {

  public void start() {


    AmqpClientOptions options = new AmqpClientOptions()
            .setHost("localhost")
            .setPort(5672)
            .setUsername("user")
            .setPassword("secret");
    // Create a client using its own internal Vert.x instance.
    AmqpClient client1 = AmqpClient.create(options);

    // USe an explicit Vert.x instance.
    AmqpClient client2 = AmqpClient.create(vertx, options);

    client1.connect(ar -> {
      if (ar.failed()) {
        System.out.println("Unable to connect to the broker");
      } else {
        System.out.println("Connection succeeded");
        AmqpConnection connection = ar.result();
      }
    });

//    vertx.eventBus().registerHandler("ping-address", new Handler<Message<String>>() {
//      @Override
//      public void handle(Message<String> message) {
//        message.reply("pong!");
//        container.logger().info("Sent back pong");
//      }
//    });
//
//    container.logger().info("PingVerticle started");
  }

  

}
