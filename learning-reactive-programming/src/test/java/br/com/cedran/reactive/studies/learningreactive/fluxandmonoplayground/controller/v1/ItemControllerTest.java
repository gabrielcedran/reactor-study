package br.com.cedran.reactive.studies.learningreactive.fluxandmonoplayground.controller.v1;

import br.com.cedran.reactive.studies.learningreactive.constants.ItemConstants;
import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ItemReactiveRepository reactiveRepository;

  public List<Item> data() {
    return Arrays.asList(
        new Item(null, "Item 1", 123.32),
        new Item(null, "Item 2", 34.71),
        new Item(null, "Item 3", 63.01),
        new Item("ABC", "4 Item 4", 99.99)
    );
  }

  @BeforeEach
  public void setup() {

    reactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(data()))
        .flatMap(reactiveRepository::save)
        .doOnNext(item -> System.out.println("Inserted item is: " + item))
        .blockLast();

  }

  @Test
  public void test_getAllItems() {
    Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Item.class)
        .getResponseBody();

    StepVerifier.create(itemFlux)
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }

  @Test
  public void test_getAllItems_approach2() {
    webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Item.class)
        .hasSize(4)
        .consumeWith(response -> {
          List<Item> items = response.getResponseBody();
          items.forEach(item -> Assertions.assertTrue(item.getId() != null));
        });

  }

  @Test
  public void test_findById() {
    Flux<Item> mono =  webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "ABC")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Item.class)
        .getResponseBody();

    StepVerifier.create(mono)
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  public void test_findById_approach2() {
    webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "ABC")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.price", CoreMatchers.equalTo(99.99));
  }

  @Test
  public void test_findById_notFound() {
    webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "-ABC")
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void test_createItem() {
    Item item = new Item(null, "Item 5", 123.21);

    webTestClient.post().uri(ItemConstants.ITEM_ENDPOINT_V1)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(item)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id", CoreMatchers.notNullValue());
  }

  @Test
  public void test_createItem_approach2() {
    Item item = new Item(null, "Item 5", 123.21);

    Flux<Item> itemFlux = webTestClient.post().uri(ItemConstants.ITEM_ENDPOINT_V1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Item.class)
        .getResponseBody();

    StepVerifier.create(itemFlux.log())
        .expectSubscription()
        .expectNextMatches(item2 -> item2.getId() != null)
        .verifyComplete();
  }

  @Test
  public void test_deleteItem() {

    webTestClient.delete().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "ABC")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Void.class);

  }

  @Test
  public void test_deleteItem_notFound() {

    webTestClient.delete().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "-ABC")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();

  }

  @Test
  public void test_updateItem() {
    Item item = new Item(null, "Item 4.1", 1.11);

    webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "ABC")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.description", CoreMatchers.equalTo("Item 4.1"))
        .hasJsonPath().jsonPath("$.price", CoreMatchers.equalTo(1.11));
  }

  @Test
  public void test_updateItem_approach2() {
    Item item = new Item(null, "Item 4.1", 1.11);

    Flux<Item> itemFlux = webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "ABC")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Item.class).getResponseBody();

    StepVerifier.create(itemFlux)
        .expectSubscription()
        .expectNextMatches(item2 -> item2.getDescription().equals("Item 4.1") && item2.getPrice().equals(1.11));
  }

  @Test
  public void test_updateItem_nonExistent() {
    Item item = new Item(null, "Item 4.1", 1.11);

    webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/{id}", "-ABC")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void test_exceptionHandling() {

    webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/exception")
        .exchange()
        .expectStatus().is5xxServerError()
        .expectBody(String.class)
        .isEqualTo("Unexpected Error");
  }

  @Test
  public void test_exceptionHandling_approach2() {

    Flux<String> stringFlux = webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1 + "/exception")
        .exchange()
        .expectStatus().is5xxServerError()
        .returnResult(String.class)
        .getResponseBody();

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("Unexpected Error")
        .verifyComplete();
  }
}
