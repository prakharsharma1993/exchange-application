package com.currency.exchange.controller;


import com.currency.exchange.dto.ConversionRequestDTO;
import com.currency.exchange.dto.ConversionResponseDTO;
import com.currency.exchange.dto.CurrencyInfoDTO;
import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.service.ExchangeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/currency")
@Tag(name = "Currency Exchange", description = "API for currency exchange and conversion")

public class CurrencyExchangeController {

    private final ExchangeServiceImpl exchangeService;

    @Autowired
    public CurrencyExchangeController(ExchangeServiceImpl exchangeService) {
        this.exchangeService = exchangeService;
    }


    @Operation(summary = "Get exchange rate", description = "Retrieve exchange rate information between two currencies. " +
            "If currencyB is not provided, it returns rates for all available currencies.")
    @GetMapping("/exchange-rate")
    public ResponseEntity getExchangeRate(@RequestParam(defaultValue = "USD") String currencyA,
                                          @RequestParam(required = false) String currencyB) {
        try {
            log.info("Received request for getExchange with currencyA: {} and currencyB: {}", currencyA, currencyB);
            return ResponseEntity.ok(exchangeService.getExchangeData(currencyA, currencyB));
        } catch (InvalidCurrencyException e) {
            log.error("Invalid currency request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while fetching exchange rates.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @Operation(summary = "Convert Currency", description = "Convert an amount from one currency to another.")
    @PostMapping("/currency-convertor")
    public ResponseEntity getCurrencyConversion(@RequestBody ConversionRequestDTO conversionRequestDTO) {
        try {
            log.info("Received request for getCurrencyConversion with request: {}", conversionRequestDTO);
            ConversionResponseDTO conversionResponse = exchangeService.getCurrencyConversion(conversionRequestDTO);
            return ResponseEntity.ok(conversionResponse);
        } catch (InvalidCurrencyException e) {
            log.error("Invalid currency conversion request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while performing currency conversion.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @Operation(summary = "Get Currency List", description = "Retrieve a list of available currencies.")
    @GetMapping("/currency-list")
    public ResponseEntity getCurrencyList() {
        try {
            log.info("Received request for getCurrencyList.");

            List<CurrencyInfoDTO> currencyList = exchangeService.getCurrencyList();
            return ResponseEntity.ok(currencyList);
        } catch (Exception e) {
            log.error("An error occurred while fetching the currency list.", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
