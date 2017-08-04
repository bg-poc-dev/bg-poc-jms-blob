package com.bg.poc.bgpocjmsblob.endpoint;

import lombok.extern.java.Log;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
@Log
public class BlobSenderController {

    @Value("${queue.blob}")
    private String queue;

    @Value("${fs.host}")
    private String fsHost;

    @Value("${tmpdir}")
    private String tmpdir;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping(path = "/")
    public ResponseEntity post() throws MalformedURLException, URISyntaxException {
        String name = createFile();
        URL url = new URL(fsHost + name);

        log.info("sending file " + url);

        jmsTemplate.send(queue, session -> {
            BlobMessage msg = ((ActiveMQSession) session).createBlobMessage(url, true);
            msg.setStringProperty("id", name);
            return msg;
        });

        return ResponseEntity.created(url.toURI()).build();
    }

    private String createFile() {
        String st = String.valueOf(System.nanoTime());
        try (OutputStream fos = new FileOutputStream(tmpdir + st)) {
            fos.write(st.getBytes());
            return st;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
