package br.com.cedran.reactive.studies.learningreactive.fluxandmonoplayground.repository;

import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ItemReactiveRepositoryTest {

  @Autowired
  private ItemReactiveRepository reactiveRepository;

  private List<Item> items = Arrays.asList(
      new Item(null, "Item 1", 20.2),
      new Item(null, "Item 2", 30.29),
      new Item(null, "Item 3", 10.44),
      new Item(null, "Item 4", 99.9),
      new Item("123", "Item 5", 1.91));

  @BeforeEach
  public void setup() {
    reactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(items))
        .flatMap(reactiveRepository::save)
        .doOnNext(item -> System.out.println(item))
    .blockLast();
  }

  @Test
  public void getAllItems() {
    StepVerifier.create(reactiveRepository.findAll())
      .expectSubscription()
      .expectNextCount(5)
      .verifyComplete();
  }

  @Test
  public void getItemById() {
    StepVerifier.create(reactiveRepository.findById("123"))
        .expectSubscription()
        .expectNextMatches(item -> item.getDescription().equals("Item 5"))
        .verifyComplete();
  }

  @Test
  public void getItemByDescription() {
    StepVerifier.create(reactiveRepository.findByDescription("Item 4"))
        .expectSubscription()
        .expectNextMatches(item -> item.getPrice().equals(Double.valueOf(99.9)));
  }


  @Test
  public void saveItem() {
    Item item = new Item(null, "Item 6", 43.63);

    Mono<Item> savedItem = reactiveRepository.save(item);

    StepVerifier.create(savedItem.log("saveItem: "))
        .expectSubscription()
        .expectNextMatches(item1 -> item.getId() != null && item.getDescription().equals("Item 6"))
        .verifyComplete();
  }

  @Test
  public void updateItem() {
    Flux<Item> updatedItem = reactiveRepository.findByDescription("Item 2")
        .map(item -> {item.setPrice(11.111); return item;})
        .flatMap(reactiveRepository::save);

    StepVerifier.create(updatedItem)
        .expectSubscription()
        .expectNextMatches(item -> item.getPrice().equals(Double.valueOf(11.111)));

  }

  @Test
  public void deleteItem() {
    Mono<Void> deletedItem = reactiveRepository.findById("123")
      .map(Item::getId)
      .flatMap(reactiveRepository::deleteById);

    StepVerifier.create(deletedItem.log("delete"))
        .expectSubscription()
        .verifyComplete();

    StepVerifier.create(reactiveRepository.findAll().log("findAll"))
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }

}
