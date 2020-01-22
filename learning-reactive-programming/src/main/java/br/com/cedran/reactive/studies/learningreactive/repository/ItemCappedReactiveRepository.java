package br.com.cedran.reactive.studies.learningreactive.repository;


import br.com.cedran.reactive.studies.learningreactive.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemCappedReactiveRepository extends ReactiveMongoRepository<ItemCapped, String> {

  @Tailable
  Flux<ItemCapped> findItemsBy();

}
