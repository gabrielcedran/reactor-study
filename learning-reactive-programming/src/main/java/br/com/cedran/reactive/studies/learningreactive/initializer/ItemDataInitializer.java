package br.com.cedran.reactive.studies.learningreactive.initializer;

import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.document.ItemCapped;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemCappedReactiveRepository;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

  @Autowired
  private ItemReactiveRepository repository;

  @Autowired
  private ItemCappedReactiveRepository cappedRepository;

  @Autowired
  private MongoOperations mongoOperations;

  @Override
  public void run(String... args) throws Exception {
    initialDataSetup();
    createCappedCollection();
  }

  public List<Item> data() {
    return Arrays.asList(
        new Item(null, "Item 1", 123.32),
        new Item(null, "Item 2", 34.71),
        new Item(null, "Item 3", 63.01),
        new Item("ABC", "4 Item 4", 99.99)
    );
  }

  private void initialDataSetup() {

    repository.deleteAll()
        .thenMany(Flux.fromIterable(data()))
        .flatMap(repository::save)
        .thenMany(repository.findAll())
        .subscribe(item -> System.out.println("Inserted: " + item));

    Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
        .map( i -> new ItemCapped(null, "Random Item: " + i, 100.0+i));

    cappedRepository.insert(itemCappedFlux)
      .subscribe(item -> log.info("Inserted item: {}", item));
  }

  private void createCappedCollection() {
    mongoOperations.dropCollection(ItemCapped.class);
    mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

  }
}
