package br.com.cedran.reactive.studies.learningreactive.fluxandmonoplayground.handler;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class SampleHandlerFunctionTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void test_approach1_withFunctionalEndpoint() {

    FluxExchangeResult<Integer> result = webTestClient.get().uri("/functional/flux")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .returnResult(Integer.class);

    StepVerifier.create(result.getResponseBody())
        .expectSubscription()
        .expectNext(1, 2, 3, 4)
        .expectComplete();

  }

  @Test
  public void test_approach2_withFunctionalEndpoint() {
    webTestClient.get().uri("/functional/mono")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Integer.class)
        .consumeWith(result -> Assertions.assertEquals(1, result.getResponseBody()));
  }

}
