package com.bg.poc.bgpocjmsblobcns;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.blob.BlobDownloadStrategy;
import org.apache.activemq.blob.DefaultBlobDownloadStrategy;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Session;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class BgPocJmsBlobConsumerApplication {

    @Value("${activemq.broker-url}")
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory();
    }

    @Bean
    public BlobDownloadStrategy blobDownloadStrategy(ActiveMQConnectionFactory activeMQConnectionFactory) {
        return new DefaultBlobDownloadStrategy(activeMQConnectionFactory.getBlobTransferPolicy()) {

            @Override
            protected URL createMessageURL(ActiveMQBlobMessage message) throws JMSException, MalformedURLException {
                return message.getURL();
            }
        };
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(activeMQConnectionFactory());
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(BgPocJmsBlobConsumerApplication.class, args);
    }
}
