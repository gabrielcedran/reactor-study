package br.com.cedran.reactive.studies.learningreactive.fluxandmonoplayground.controller.v1;

import br.com.cedran.reactive.studies.learningreactive.constants.ItemConstants;
import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.document.ItemCapped;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemCappedReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class ItemStreamControllerTest {

  @Autowired
  private ItemCappedReactiveRepository repository;

  @Autowired
  private MongoOperations mongoOperations;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeEach
  public void setup() {
    mongoOperations.dropCollection(ItemCapped.class);
    mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

    Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
        .map( i -> new ItemCapped(null, "Random Item: " + i, 100.0+i))
        .take(5);

    repository.insert(itemCappedFlux)
        .doOnNext(item -> log.info("Inserted item: {}", item))
        .blockLast();

  }

  @Test
  public void testStreamAllItems() {
    Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.STREAM_ITEM_ENDPOINT_V1)
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(ItemCapped.class)
        .getResponseBody()
        .take(5);

    StepVerifier.create(itemCappedFlux)
        .expectSubscription()
        .expectNextCount(5)
        .thenCancel()
        .verify();
  }
}
