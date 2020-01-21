package br.com.cedran.reactive.studies.learningreactive.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

@Component
public class SampleHandlerFunction {

  Logger logger = LoggerFactory.getLogger(SampleHandlerFunction.class);

  public Mono<ServerResponse> flux(ServerRequest serverRequest) {

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(longProcessing().subscribeOn(Schedulers.elastic()), Integer.class);


  }

  //FIXME
  private Mono<List<Integer>> longProcessing() {
    return Mono.fromCallable(() -> {
      try {
        logger.info("Request arrived");
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return Arrays.asList(1,2,3,4);
    });

  }

  public Mono<ServerResponse> mono(ServerRequest serverRequest) {

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(1).log(), Integer.class);


  }

}
