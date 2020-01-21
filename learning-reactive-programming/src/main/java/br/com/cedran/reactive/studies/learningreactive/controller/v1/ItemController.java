package br.com.cedran.reactive.studies.learningreactive.controller.v1;

import br.com.cedran.reactive.studies.learningreactive.constants.ItemConstants;
import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

  @Autowired
  private ItemReactiveRepository reactiveRepository;

  @GetMapping(ItemConstants.ITEM_ENDPOINT_V1)
  public Flux<Item> getAllItems() {
    return reactiveRepository.findAll();
  }

  @GetMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
  public Mono<ResponseEntity<Item>> getById(@PathVariable String id) {
    return reactiveRepository.findById(id)
        .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping(ItemConstants.ITEM_ENDPOINT_V1)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Item> createItem(@RequestBody Item item) {
    return reactiveRepository.save(item);

  }

  @DeleteMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
  public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
    return reactiveRepository.findById(id)
            .flatMap(item -> reactiveRepository.deleteById(id).then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
  public Mono<ResponseEntity<Item>> updateItemById(@PathVariable String id, @RequestBody Item item) {
    return reactiveRepository.findById(id)
        .flatMap(currentItem -> {
          currentItem.setPrice(item.getPrice());
          currentItem.setDescription(item.getDescription());
          return reactiveRepository.save(currentItem);
        })
        .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /* Exception handler local to the controller. Generic for all controllers: ControllerExceptionHandler.
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException runtimeException) {
    log.error("Error caught in handleRuntimeException. Message {}", runtimeException.getMessage(), runtimeException);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(runtimeException.getMessage());
  }*/

  @GetMapping(ItemConstants.ITEM_ENDPOINT_V1 + "/exception")
  public Flux<Item> getAllItemsWithException() {
    return reactiveRepository.findAll().concatWith(Mono.error(new RuntimeException("Unexpected Error")));
  }
}
