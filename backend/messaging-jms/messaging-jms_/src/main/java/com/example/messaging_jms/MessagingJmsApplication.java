package com.example.messaging_jms;

import com.example.messaging_jms.model.Email;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
@EnableJms
public class MessagingJmsApplication {

    public static void main(String[] args) {
        // Launch the application
        System.out.println(" *** Demarrage : MessagingJmsApplication 1");
        ConfigurableApplicationContext context = SpringApplication.run(MessagingJmsApplication.class, args);

        System.out.println(" *** MessagingJmsApplication 2 : apres appel ConfigurableApplicationContext ");

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println(" *** MessagingJmsApplication 3 : apres appel context.getBean(JmsTemplate.class) ");
                // Send a message with a POJO - the template reuse the message converter
                System.out.println(" *** MessagingJmsApplication 3 : Sending an email message.");
                jmsTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello from Spring JMS"));
                System.out.println(" *** MessagingJmsApplication 4 : jmsTemplate.convertAndSend.");
    }


}
