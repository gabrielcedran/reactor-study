package br.com.cedran.reactive.studies.learningreactiveprogrammingclient.controller;

import br.com.cedran.reactive.studies.learningreactiveprogrammingclient.domain.Item;
import br.com.cedran.reactive.studies.learningreactiveprogrammingclient.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {


  private WebClient webClient = WebClient.create("http://localhost:8080");

  // Using retrieve, there is only access to the response body directly
  @GetMapping("/client/retrieve")
  public Flux<Item> getAllItemsUsingRetrieve() {
    return webClient.get().uri("/v1/items")
        .retrieve().bodyToFlux(Item.class)
        .log("Items in client project");
  }

  // Using exchange, there is access to the client response, hence it is possible to obtain the headers as well
  @GetMapping("/client/exchange")
  public Flux<Item> getAllItemsUsingExchange() {
    return webClient.get().uri("/v1/items")
        .exchange()
        .flatMapMany(clientResponse ->
            //clientResponse.headers()
            clientResponse.bodyToFlux(Item.class))
        .log("Items in client project");
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> notFoundExceptionHandler(NotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
  }

  @GetMapping("/client/retrieve/{id}")
  public Mono<Item> retrieveSingleItem(@PathVariable String id) {
    return webClient.get().uri("/v1/items/{id}", id)
        .retrieve()
        .onRawStatus(status -> status == HttpStatus.NOT_FOUND.value(),
            clientResponse ->
            // could access response body if there was one being returned by the server
            {
              throw new NotFoundException(String.format("Item %s not found :(.", id));
            })
        .bodyToMono(Item.class)
        .log("Item by ID");
  }

  @GetMapping("/client/exchange/{id}")
  public Mono<Item> exchangeSingleItem(@PathVariable String id) {
    return webClient.get().uri("/v1/items/{id}", id)
        .exchange()
        .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
        .log("Item by ID");
  }

  @PostMapping("/client/retrieve")
  public Mono<Item> createItem(@RequestBody Item item) {
    return webClient.post().uri("/v1/items")
          .contentType(MediaType.APPLICATION_JSON)
          .body(Mono.just(item), Item.class)
          .retrieve()
          .bodyToMono(Item.class)
          .log("Item created");

  }

  @PutMapping("/client/exchange/{id}")
  public Mono<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
    return webClient.put().uri("/v1/items/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(item)
        .exchange()
        .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
        .log("Item updated");
  }

  @DeleteMapping("/client/retrieve/{id}")
  public Mono<Void> deleteItem(@PathVariable String id) {
    return webClient.delete().uri("/v1/items/{id}", id)
        .retrieve()
        .bodyToMono(Void.class)
        .log("Item deleted");
  }

  @GetMapping("/client/retrieve/error")
  public Flux<Item> errorRetrieve() {
    return webClient.get().uri("/v1/items/exception")
        .retrieve()
        .onStatus(HttpStatus::is5xxServerError,
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorMessage -> {
                  log.error("Error message: {}", errorMessage);
                  throw new RuntimeException(errorMessage);
                }))
        .bodyToFlux(Item.class);
  }

  @GetMapping("/client/exchange/error")
  public Flux<Item> errorExchange() {
    return webClient.get().uri("/v1/items/exception")
        .exchange()
        .flatMapMany(clientResponse -> {
          if (clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.bodyToMono(String.class)
                .flatMap(errorMessage -> {
                  log.error("Error message: {}", errorMessage);
                  throw new RuntimeException(errorMessage);
                });
          }
          return clientResponse.bodyToFlux(Item.class);
        });
  }
}
