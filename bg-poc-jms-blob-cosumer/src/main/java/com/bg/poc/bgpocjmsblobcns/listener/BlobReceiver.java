package com.bg.poc.bgpocjmsblobcns.listener;

import lombok.extern.java.Log;
import org.apache.activemq.blob.BlobDownloadStrategy;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Log
public class BlobReceiver {

    @Value("${tmpdir}")
    private String tmpdir;

    @Autowired
    private BlobDownloadStrategy blobDownloadStrategy;

    @JmsListener(destination = "${queue.blob}", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(ActiveMQBlobMessage msg) throws IOException, JMSException {
        log.info("Received <" + msg.getStringProperty("id") + ">");
        try (FileOutputStream fos = new FileOutputStream(tmpdir + msg.getStringProperty("id")+"_recv")) {
            InputStream is = msg.getInputStream();
            byte[] buffer = new byte[is.available()];
            is.read(buffer);

            fos.write(buffer);
        } catch (Exception e) {
            throw e;
        }

        blobDownloadStrategy.deleteFile(msg);

        msg.acknowledge();
    }
}
