package br.com.cedran.reactive.studies.learningreactiveprogrammingclient.handler;

import br.com.cedran.reactive.studies.learningreactiveprogrammingclient.domain.Item;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemFunctionalHandler {

  private WebClient webClient = WebClient.create("http://localhost:8080");

  public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
    return webClient.put().uri("/v1/items/{id}", serverRequest.pathVariable("id"))
        .contentType(MediaType.APPLICATION_JSON)
        .body(serverRequest.bodyToMono(Item.class), Item.class)
        .retrieve().bodyToMono(Item.class)
        .flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(item));
  }

  public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
    return webClient.get().uri("/v1/items")
        .exchange()
        .map(clientResponse -> clientResponse.bodyToFlux(Item.class))
        .flatMap(item -> ServerResponse.ok().body(item, Item.class));
  }

}
