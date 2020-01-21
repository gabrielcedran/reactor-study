package br.com.cedran.reactive.studies.learningreactive.initializer;

import br.com.cedran.reactive.studies.learningreactive.document.Item;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

  @Autowired
  private ItemReactiveRepository repository;

  @Override
  public void run(String... args) throws Exception {
    initialDataSetup();
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
    ;

  }
}
