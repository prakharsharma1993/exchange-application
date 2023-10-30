package com.currency.exchange.controller;

import com.currency.exchange.dto.ConversionRequestDTO;
import com.currency.exchange.dto.ConversionResponseDTO;
import com.currency.exchange.dto.CurrencyInfoDTO;
import com.currency.exchange.exception.APINotAvailableException;
import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.service.ExchangeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyExchangeControllerTest {

    @Mock
    private ExchangeServiceImpl exchangeService;

    private CurrencyExchangeController controller;

    @BeforeEach
    void setUp() {
        exchangeService = Mockito.mock(ExchangeServiceImpl.class);
        controller = new CurrencyExchangeController(exchangeService);
    }

    @Test
    void testGetExchangeRate_Success() throws InvalidCurrencyException, APINotAvailableException {
        String currencyA = "USD";
        String currencyB = "EUR";
        ConversionResponseDTO responseDTO = new ConversionResponseDTO();
        Mockito.when(exchangeService.getExchangeData(currencyA, currencyB)).thenReturn(responseDTO);

        ResponseEntity responseEntity = controller.getExchangeRate(currencyA, currencyB);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());
    }

    @Test
    void testGetExchangeRate_InvalidCurrency() throws InvalidCurrencyException, APINotAvailableException {
        String currencyA = "InvalidCurrency";
        String currencyB = "EUR";
        Mockito.doThrow(new InvalidCurrencyException("Invalid currency")).when(exchangeService).getExchangeData(currencyA, currencyB);

        ResponseEntity responseEntity = controller.getExchangeRate(currencyA, currencyB);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testGetCurrencyConversion_Success() throws InvalidCurrencyException, APINotAvailableException {
        ConversionRequestDTO requestDTO = new ConversionRequestDTO();
        ConversionResponseDTO responseDTO = new ConversionResponseDTO();
        Mockito.when(exchangeService.getCurrencyConversion(requestDTO)).thenReturn(responseDTO);

        ResponseEntity responseEntity = controller.getCurrencyConversion(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());
    }

    @Test
    void testGetCurrencyConversion_InvalidCurrency() throws InvalidCurrencyException, APINotAvailableException {
        ConversionRequestDTO requestDTO = new ConversionRequestDTO();
        Mockito.doThrow(new InvalidCurrencyException("Invalid currency")).when(exchangeService).getCurrencyConversion(requestDTO);

        ResponseEntity responseEntity = controller.getCurrencyConversion(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testGetCurrencyList_Success() {
        List<CurrencyInfoDTO> currencyList = Collections.singletonList(new CurrencyInfoDTO("USD", "United States Dollar", null));
        Mockito.when(exchangeService.getCurrencyList()).thenReturn(currencyList);

        ResponseEntity responseEntity = controller.getCurrencyList();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(currencyList, responseEntity.getBody());
    }
}
