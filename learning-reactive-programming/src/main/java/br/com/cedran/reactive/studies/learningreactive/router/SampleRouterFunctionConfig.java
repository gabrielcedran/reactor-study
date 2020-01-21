package br.com.cedran.reactive.studies.learningreactive.router;

import br.com.cedran.reactive.studies.learningreactive.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SampleRouterFunctionConfig {

  @Bean
  public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction) {
    return RouterFunctions
        .route(RequestPredicates.GET("/functional/flux")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::flux)
        .andRoute(RequestPredicates.GET("functional/mono")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);

  }

}
