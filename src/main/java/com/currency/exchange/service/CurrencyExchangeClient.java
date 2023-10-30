package com.currency.exchange.service;


import com.currency.exchange.dto.CurrencyApiResponseDTO;
import com.currency.exchange.exception.APINotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Objects;

@Service
public class CurrencyExchangeClient {


    @Value("${exchange.api.key}")
    private String API_KEY;

    @Value("${exchange.api.path}")
    private String API_URL;
    @Autowired
    private WebClient.Builder webClientBuilder;


    @Cacheable(cacheNames = "exchangeRates", key = "'exchangeRates'", unless = "#result == null")
    public CurrencyApiResponseDTO getDataFromApi() throws APINotAvailableException {
        CurrencyApiResponseDTO response = webClientBuilder
                .baseUrl(API_URL)
                .build()
                .get()
                .uri("?access_key=" + API_KEY)
                .retrieve()
                .bodyToMono(CurrencyApiResponseDTO.class)
                .block();

        if (Objects.isNull(response)) {
            throw new APINotAvailableException("Global exchange is down");
        }

        return response;
    }

}
