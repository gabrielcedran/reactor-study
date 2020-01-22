package br.com.cedran.reactive.studies.learningreactive.controller.v1;

import br.com.cedran.reactive.studies.learningreactive.constants.ItemConstants;
import br.com.cedran.reactive.studies.learningreactive.document.ItemCapped;
import br.com.cedran.reactive.studies.learningreactive.repository.ItemCappedReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {

  @Autowired
  private ItemCappedReactiveRepository reactiveRepository;

  @GetMapping(value = ItemConstants.STREAM_ITEM_ENDPOINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<ItemCapped> getItemsStream() {
    return reactiveRepository.findItemsBy();
  }

}
