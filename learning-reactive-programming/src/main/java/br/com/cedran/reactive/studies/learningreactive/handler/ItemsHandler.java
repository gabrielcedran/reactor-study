package br.com.cedran.reactive.studies.learningreactive.handler;

import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

  @Autowired
  private ItemReactiveRepository reactiveRepository;


  public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
    return ServerResponse.ok().body(reactiveRepository.findAll(), Item.class);
  }

  public Mono<ServerResponse> getById(ServerRequest serverRequest) {
    return reactiveRepository.findById(serverRequest.pathVariable("id"))
        .flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(item), Item.class))
        .switchIfEmpty(ServerResponse.notFound().build());

  }

  public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
    /*return serverRequest.bodyToMono(Item.class)
        .flatMap(reactiveRepository::save)
        .flatMap(item -> ServerResponse.created(null).contentType(MediaType.APPLICATION_JSON).bodyValue(item));*/
    return serverRequest.bodyToMono(Item.class)
        .flatMap(item -> ServerResponse.created(null).contentType(MediaType.APPLICATION_JSON).body(reactiveRepository.save(item), Item.class));
  }

  public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {
    return reactiveRepository.findById(serverRequest.pathVariable("id"))
        .flatMap(item -> reactiveRepository.deleteById(item.getId()).then(ServerResponse.ok().build()))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> updateById(ServerRequest serverRequest) {
    Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

    return itemMono.flatMap(item ->
      reactiveRepository.findById(serverRequest.pathVariable("id"))
          .flatMap(currentItem -> {
              currentItem.setDescription(item.getDescription());
              currentItem.setPrice(item.getPrice());
              return reactiveRepository.save(currentItem);
          })
        .flatMap(updatedItem -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(updatedItem))
    ).switchIfEmpty(ServerResponse.notFound().build());

  }

  public Mono<ServerResponse> exceptionHandler(ServerRequest serverRequest) {
    throw new RuntimeException("Runtime exception functional handler.");
  }
}
