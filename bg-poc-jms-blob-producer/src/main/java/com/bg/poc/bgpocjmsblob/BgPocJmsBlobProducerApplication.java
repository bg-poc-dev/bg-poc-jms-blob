package com.bg.poc.bgpocjmsblob;

import com.bg.poc.bgpocjmsblob.endpoint.BlobSenderController;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@SpringBootApplication
public class BgPocJmsBlobProducerApplication {

    @Value("${activemq.broker-url}")
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory();
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(activeMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        ConfigurableApplicationContext context = SpringApplication.run(BgPocJmsBlobProducerApplication.class, args);

        BlobSenderController senderController = context.getBean(BlobSenderController.class);
        senderController.post();
    }
}
