package br.com.cedran.reactive.studies.learningreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoTest {

  @Test
  public void fluxTest() {

    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
        //.concatWith(Flux.error(new RuntimeException("Exception occurred.")))
            .concatWith(Flux.just("After concatenation"))
            .buffer(2)
            .flatMap(Flux::fromIterable)
        .log();

    stringFlux.subscribe(
        System.out::println,
        (e) -> System.err.println(e),
        () -> System.out.println("Completed!"));
  }

  @Test
  public void fluxTestElements_withoutError() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

    StepVerifier.create(stringFlux)
        .expectNext("Spring")
        .expectNext("Spring Boot")
        .expectNext("Reactive Spring")
        .verifyComplete();
  }

  @Test
  public void testDbBehaviour() {
    Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("a", "b", "c", "d", "e", "f"))
        .flatMap(s ->  {
          return Flux.fromIterable(dbCall(s));
        }).log();

    stringFlux.subscribe(System.out::println);
  }

  private List<String> dbCall(String s) {
    System.out.println("DbCall!! " + s);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Arrays.asList(s, "2");
  }

  @Test
  public void testDbBehaviour_parallel() {
    Flux<String> stringFlux = Flux.just("a", "b", "c", "d", "e", "f")
        .window(2) // Flux<Flux<String>> (a,b) (c,d) (e,f)
        .flatMap(s -> // Flux<String>
            s.log().map(this::dbCall) // Flux<List<String>>
            .subscribeOn(Schedulers.parallel()) // Execute in parallel the conversion from String into List
                .flatMap(ss -> Flux.fromIterable(ss)) // Convert into Flux<Flux<String>> to return Flux<String>
        );

    StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
  }

  @Test
  public void testDbBehaviour_parallel2() {
    ParallelFlux<String> stringFlux = Flux.just("a", "b", "c", "d", "e", "f")
        .parallel(6)
        .runOn(Schedulers.parallel())
        .flatMap(x -> Flux.fromIterable(dbCall(x)))
        .log();

    StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
  }

  @Test
  public void testDbBehaviour_parallel3() {
    Flux<String> stringFlux =
        Flux.just("a", "b", "c", "d", "e", "f")
        .flatMap(x -> Flux.defer(() ->
            Flux.just(this.dbCall(x))).flatMap(xx -> Flux.fromIterable(xx))
            .subscribeOn(Schedulers.parallel()), 5)

        .log();

    StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
  }

  @Test
  public void testDbBehaviour_parallel4() {
    Flux<String> stringFlux = Flux.just("a", "b", "c", "d", "e", "f")
        .map(Flux::just)
        .flatMap(x -> x.map(this::dbCall).subscribeOn(Schedulers.parallel()).flatMap(y -> Flux.fromIterable(y)), 2).log();

    StepVerifier.create(stringFlux).expectSubscription().expectNextCount(12).verifyComplete();
  }


  @Test
  public void testFluxAndFlatMaps() {
    Flux.just("a", "b", "c", "d", "e", "f")
        .flatMap(x -> Flux.just(x));

    Flux.just("a", "b", "c", "d", "e", "f")
        .map(e -> Flux.just(e))
        .flatMap(Function.identity());

    Flux.just("a", "b", "c", "d", "e", "f")
        .map(e -> Flux.just(e))
        .map( x-> x.map(xx -> Arrays.asList(xx)))
        .flatMap(Function.identity())
        .map( x -> x.size());

  }

  @Test
  public void moreFluxTests() {
    Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
        .take(10)
        .log();

    StepVerifier.create(flux)
        .expectNextCount(10).verifyComplete();
  }

}
