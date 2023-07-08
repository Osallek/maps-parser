package fr.osallek.mapsparser.service.laposte;

import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Data;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Service
@Order(0)
public class ZipCodeSource extends GenerationService {

    private final WebClient webClient;

    public ZipCodeSource(WebClient.Builder builder) {
        super("La Poste", "https://www.lapostegroupe.com");
        this.webClient = builder.baseUrl("https://datanova.laposte.fr/api/records/1.0/search")
                                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofMinutes(5))))
                                .build();
    }

    @Override
    public void addData(Data data) {
        int page = 0;


    }
}
