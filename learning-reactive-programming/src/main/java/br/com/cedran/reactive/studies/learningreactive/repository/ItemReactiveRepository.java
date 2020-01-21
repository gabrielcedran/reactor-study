package br.com.cedran.reactive.studies.learningreactive.repository;


import br.com.cedran.reactive.studies.learningreactive.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;


public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {


  Flux<Item> findByDescription(String description);


}
