package com.bg.poc.bgpocjmsblobfs.endpoint;

import com.bg.poc.bgpocjmsblobfs.service.StorageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log
public class FileController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/files")
    public List<String> listUploadedFiles() throws IOException {
        return storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        log.info("GET "+file);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PutMapping("/files/{file}")
    public ResponseEntity put(@PathVariable String file, InputStream stream) throws URISyntaxException {
        log.info("PUT " + file);
        storageService.store(stream, file);
        return ResponseEntity.created(new URI("")).build();
    }

    @DeleteMapping("/files/{file:.+}")
    public ResponseEntity delete(@PathVariable String file) {
        log.info("DELETE " + file);
        storageService.delete(file);
        return ResponseEntity.ok().build();
    }
}
