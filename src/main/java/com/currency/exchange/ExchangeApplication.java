package com.currency.exchange;

import com.currency.exchange.dto.CurrencyApiResponseDTO;
import com.currency.exchange.service.CurrencyExchangeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ExchangeApplication {



	public static void main(String[] args) {

		SpringApplication.run(ExchangeApplication.class, args);
	}


}
