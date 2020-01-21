package br.com.cedran.reactive.studies.learningreactive.fluxandmonoplayground.controller;

import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@ExtendWith(SpringExtension.class)
@WebFluxTest
public class FluxAndMonoControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private ItemReactiveRepository reactiveRepository;

  @Test
  public void flux_approach1() {
    Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.withVirtualTime(() -> integerFlux)
        .expectSubscription()
        .expectNext(1, 2, 3, 4)
        .expectComplete();
  }

  @Test
  public void flux_approach2() {
    webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
        .expectBodyList(Integer.class)
        .hasSize(4);

  }

  @Test
  public void flux_approach3() {
    Flux<Long> longFlux = webTestClient.get().uri("/fluxstream")
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(longFlux)
        .expectNext(0L, 1L, 2L)
        .thenCancel()
        .verify();
  }

  @Test
  public void mono_approach() {
    webTestClient.get().uri("/mono")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Integer.class)
        .consumeWith((result) -> {
          Assertions.assertEquals(1, result.getResponseBody());
        });
  }



}
