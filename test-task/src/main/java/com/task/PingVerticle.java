package com.task;

import io.vertx.amqp.*;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
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
    AmqpClient client = AmqpClient.create(options);

    client.connect(ar -> {
      if (ar.failed()) {
        System.out.println("Unable to connect to the broker");
      } else {
        System.out.println("Connection succeeded");
        AmqpConnection connection = ar.result();
        connection.createAnonymousSender(responseSender -> {
          // You got an anonymous sender, used to send the reply
          // Now register the main receiver:
          connection.createReceiver("my-queue", done -> {
            if (done.failed()) {
              System.out.println("Unable to create receiver");
            } else {
              AmqpReceiver receiver = done.result();
              receiver.handler(msg -> {
                // You got the message, let's reply.
                responseSender.result().send(AmqpMessage.create()
                        .address(msg.replyTo())
                        .correlationId(msg.id()) // send the message id as correlation id
                        .withBody("my response to your request")
                        .build()
                );
              });
            }
          });
        });

        // On the sender side (sending the initial request and expecting a reply)
        connection.createDynamicReceiver(replyReceiver -> {
          // We got a receiver, the address is provided by the broker
          String replyToAddress = replyReceiver.result().address();

          // Attach the handler receiving the reply
          replyReceiver.result().handler(msg -> {
            System.out.println("Got the reply! " + msg.bodyAsString());
          });

          // Create a sender and send the message:
          connection.createSender("my-queue", sender -> {
            sender.result().send(AmqpMessage.create()
                    .replyTo(replyToAddress)
                    .id("my-message-id")
                    .withBody("This is my request").build());
          });
        });
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
