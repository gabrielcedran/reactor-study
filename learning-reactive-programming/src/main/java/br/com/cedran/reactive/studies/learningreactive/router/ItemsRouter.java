package br.com.cedran.reactive.studies.learningreactive.router;

import br.com.cedran.reactive.studies.learningreactive.constants.ItemConstants;
import br.com.cedran.reactive.studies.learningreactive.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemsRouter {


  @Bean
  public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
    return RouterFunctions.route(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1), itemsHandler::getAll)
        .andRoute(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1+"/{id}"), itemsHandler::getById)
        .andRoute(RequestPredicates.POST(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1).and(RequestPredicates.accept(
            MediaType.APPLICATION_JSON)), itemsHandler::createItem)
        .andRoute(RequestPredicates.DELETE(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1+"/{id}"), itemsHandler::deleteById)
        .andRoute(RequestPredicates.PUT(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1+"/{id}")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemsHandler::updateById);
  }

  @Bean
  public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
    return RouterFunctions.route(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1+"/runtime/exception"),itemsHandler::exceptionHandler);
  }

  @Bean
  public RouterFunction<ServerResponse> itemStreamRoute(ItemsHandler itemsHandler) {
    return RouterFunctions.route(RequestPredicates.GET(ItemConstants.STREAM_ITEM_FUNCTIONAL_ENDPOINT_V1)
        , itemsHandler::itemsStream);

  }
}
