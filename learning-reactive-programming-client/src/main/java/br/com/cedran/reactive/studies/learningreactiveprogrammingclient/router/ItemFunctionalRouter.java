package br.com.cedran.reactive.studies.learningreactiveprogrammingclient.router;

import br.com.cedran.reactive.studies.learningreactiveprogrammingclient.handler.ItemFunctionalHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemFunctionalRouter {

  @Bean
  public RouterFunction<ServerResponse> functionalRouter(ItemFunctionalHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.PUT("/functional/client/retrieve/{id}")
          .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::updateItem)
        .andRoute(RequestPredicates.GET("/functional/client/exchange"), handler::getAll);
  }

}
