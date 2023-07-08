package fr.osallek.mapsparser.config;

import fr.osallek.mapsparser.common.Constants;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {


    @Bean
    public WebClientCustomizer webClientCustomizer() {
        return builder -> {
            HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMinutes(5)).compress(true);
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
            builder.filter((request, next) -> Constants.log(request, next, LoggerFactory.getLogger("WebCLient")));
        };
    }
}
