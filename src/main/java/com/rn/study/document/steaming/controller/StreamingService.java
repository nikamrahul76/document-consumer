package com.rn.study.document.steaming.controller;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class StreamingService {

    private final WebClient webClient;

    public StreamingService(WebClient.Builder webClientBuilder) {
        // Configure WebClient with the upstream server base URL
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build(); // Replace with your upstream base URL
    }

    @GetMapping(value = "/proxy-stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<Flux<DataBuffer>>> proxyStream() {
        return webClient.get()
                .uri("/stream")
                .retrieve()
                .toEntityFlux(DataBuffer.class)
                .map(entity -> {
                    HttpHeaders upstreamHeaders = entity.getHeaders();
                    return ResponseEntity.ok()
                            .headers(upstreamHeaders)
                            .body(entity.getBody());
                });
    }
}
