package com.currency.exchange.service;


import com.currency.exchange.constant.CurrencyEnum;
import com.currency.exchange.dto.ConversionRequestDTO;
import com.currency.exchange.dto.ConversionResponseDTO;
import com.currency.exchange.dto.CurrencyApiResponseDTO;
import com.currency.exchange.dto.CurrencyInfoDTO;
import com.currency.exchange.exception.APINotAvailableException;
import com.currency.exchange.exception.InvalidCurrencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class ExchangeServiceImplTest {

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Mock
    private CurrencyExchangeClient currencyExchangeClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void test_Get_ExchangeRate_From_CurrencyA_To_CurrencyB() throws InvalidCurrencyException, APINotAvailableException {
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        ConversionResponseDTO response = exchangeService.getExchangeData("USD", "AUD");

        assertNotNull(response);
        assertEquals("USD", response.getCurrency());
        assertEquals(BigDecimal.ONE, response.getAmount());
        assertEquals("AUD", response.getConvertedCurrencyInfo().get(0).getCurrencySymbol());
        assertEquals(BigDecimal.valueOf(1.569095), response.getConvertedCurrencyInfo().get(0).getAmount());

    }

    @Test
    public void test_GetAll_ExchangeRate_From_CurrencyA() throws InvalidCurrencyException, APINotAvailableException {
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        ConversionResponseDTO response = exchangeService.getExchangeData("INR", null);

        assertNotNull(response);
        assertEquals("INR", response.getCurrency());
        assertEquals(BigDecimal.ONE, response.getAmount());
        assertEquals(21, response.getConvertedCurrencyInfo().size());
        assertEquals(BigDecimal.valueOf(0.043736), response.getConvertedCurrencyInfo().get(0).getAmount());
        assertEquals("AED", response.getConvertedCurrencyInfo().get(0).getCurrencySymbol());

    }

    @Test
    public void test_Get_ExchangeRate_For_InvalidCurrency() throws InvalidCurrencyException, APINotAvailableException {
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        InvalidCurrencyException exceptionCase1 = assertThrows(InvalidCurrencyException.class,
                () -> exchangeService.getExchangeData("USD", "LKS"));
        assertEquals("Invalid Currency : LKS", exceptionCase1.getMessage());

        InvalidCurrencyException exceptionCase2 = assertThrows(InvalidCurrencyException.class,
                () -> exchangeService.getExchangeData("LLM", "USD"));
        assertEquals("Invalid Currency : LLM", exceptionCase2.getMessage());
    }


    @Test
    public void test_Get_CurrencyConversion_From_CurrencyA_To_CurrencyB() throws InvalidCurrencyException, APINotAvailableException {
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        ConversionRequestDTO requestDTO = new ConversionRequestDTO();
        requestDTO.setFromCurrency("USD");
        requestDTO.setToCurrency(List.of("AZN"));
        requestDTO.setAmount(BigDecimal.valueOf(100));

        ConversionResponseDTO response = exchangeService.getCurrencyConversion(requestDTO);

        assertNotNull(response);
        assertEquals("USD", response.getCurrency());
        assertEquals(BigDecimal.valueOf(100), response.getAmount());
        assertEquals("AZN", response.getConvertedCurrencyInfo().get(0).getCurrencySymbol());
        assertEquals(0, BigDecimal.valueOf(170.299600).compareTo(response.getConvertedCurrencyInfo().get(0).getAmount()));


    }


    @Test
    public void test_Get_CurrencyConversion_From_CurrencyA_To_ListOfCurrencies() throws InvalidCurrencyException, APINotAvailableException {
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        ConversionRequestDTO requestDTO = new ConversionRequestDTO();
        requestDTO.setFromCurrency("INR");
        requestDTO.setToCurrency(List.of("USD", "BMD", "AUD"));
        requestDTO.setAmount(BigDecimal.valueOf(100));

        ConversionResponseDTO response = exchangeService.getCurrencyConversion(requestDTO);

        assertNotNull(response);
        assertEquals("INR", response.getCurrency());
        assertEquals(BigDecimal.valueOf(100), response.getAmount());
        assertEquals("USD", response.getConvertedCurrencyInfo().get(0).getCurrencySymbol());
        assertEquals(0, BigDecimal.valueOf(1.190800).compareTo(response.getConvertedCurrencyInfo().get(0).getAmount()));
        assertEquals("BMD", response.getConvertedCurrencyInfo().get(1).getCurrencySymbol());
        assertEquals(0, BigDecimal.valueOf(1.190800).compareTo(response.getConvertedCurrencyInfo().get(1).getAmount()));
        assertEquals("AUD", response.getConvertedCurrencyInfo().get(2).getCurrencySymbol());
        assertEquals(0, BigDecimal.valueOf(1.868400).compareTo(response.getConvertedCurrencyInfo().get(2).getAmount()));


    }


    @Test
    public void test_Get_CurrencyConversion_For_InvalidCurrency() throws InvalidCurrencyException, APINotAvailableException {
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        ConversionRequestDTO requestDTO1 = new ConversionRequestDTO();
        requestDTO1.setFromCurrency("LLM");
        requestDTO1.setToCurrency(List.of("USD", "BMD", "AUD"));
        requestDTO1.setAmount(BigDecimal.valueOf(100));

        InvalidCurrencyException exceptionCase1 = assertThrows(InvalidCurrencyException.class,
                () -> exchangeService.getCurrencyConversion(requestDTO1));
        assertEquals("Invalid Currency : LLM", exceptionCase1.getMessage());

        ConversionRequestDTO requestDTO2 = new ConversionRequestDTO();
        requestDTO2.setFromCurrency("USD");
        requestDTO2.setToCurrency(List.of("XLM", "SSD", "MND"));
        requestDTO2.setAmount(BigDecimal.valueOf(100));

        InvalidCurrencyException exceptionCase2 = assertThrows(InvalidCurrencyException.class,
                () -> exchangeService.getCurrencyConversion(requestDTO2));
        assertEquals("Invalid Currency : [XLM, SSD, MND]. Please enter at least one valid currency", exceptionCase2.getMessage());

    }


    @Test
    public void testFetchAndPrepareQuotesWithValidData() throws APINotAvailableException {
        // Mock the response from the API with valid quotes
        CurrencyApiResponseDTO apiResponse = createDummyCurrencyApiResponseDTO();
        when(currencyExchangeClient.getDataFromApi()).thenReturn(apiResponse);

        // Act
        Map<String, BigDecimal> quotes = exchangeService.fetchAndPrepareQuotes();

        // Assert
        assertNotNull(quotes);
        assertTrue(quotes.containsKey("USD"));
        assertEquals(BigDecimal.ONE, quotes.get("USD"));
        assertTrue(quotes.containsKey("AUD"));
        assertEquals(0, BigDecimal.valueOf(1.569095).compareTo(quotes.get("AUD")));
        assertTrue(quotes.containsKey("INR"));
        assertEquals(0, BigDecimal.valueOf(83.979299).compareTo(quotes.get("INR")));
    }


    @Test
    void testFetchAndPrepareQuotesWithNullData() throws APINotAvailableException {
        // Mock the response from the API with null quotes
        CurrencyApiResponseDTO apiResponse = new CurrencyApiResponseDTO();
        apiResponse.setQuotes(null);

        when(currencyExchangeClient.getDataFromApi()).thenReturn(Mono.just(apiResponse).block());

        APINotAvailableException exception = assertThrows(APINotAvailableException.class, () -> {
            exchangeService.fetchAndPrepareQuotes();
        });

        assertEquals("Exchange is not available", exception.getMessage());
    }


    @Test
    void testGetCurrencyList() {
        List<CurrencyInfoDTO> currencyList = exchangeService.getCurrencyList();

        assertEquals(CurrencyEnum.values().length, currencyList.size());

        for (int i = 0; i < CurrencyEnum.values().length; i++) {
            assertEquals(CurrencyEnum.values()[i].name(), currencyList.get(i).getCurrencySymbol());
            assertEquals(CurrencyEnum.values()[i].getValue(), currencyList.get(i).getCurrency());
        }
    }

    CurrencyApiResponseDTO createDummyCurrencyApiResponseDTO() {
        CurrencyApiResponseDTO response = new CurrencyApiResponseDTO();
        response.setSuccess(true);
        response.setTerms("https://currencylayer.com/terms");
        response.setPrivacy("https://currencylayer.com/privacy");
        response.setTimestamp(1698668463L);
        response.setSource("USD");

        Map<String, BigDecimal> quotes = new HashMap<>();
        quotes.put("USDAED", new BigDecimal("3.672949"));
        quotes.put("USDAFN", new BigDecimal("73.319583"));
        quotes.put("USDALL", new BigDecimal("99.781934"));
        quotes.put("USDAMD", new BigDecimal("402.233477"));
        quotes.put("USDANG", new BigDecimal("1.800984"));
        quotes.put("USDAOA", new BigDecimal("827.484101"));
        quotes.put("USDARS", new BigDecimal("350.025101"));
        quotes.put("USDAUD", new BigDecimal("1.569095"));
        quotes.put("USDAWG", new BigDecimal("1.8025"));
        quotes.put("USDAZN", new BigDecimal("1.702996"));
        quotes.put("USDBAM", new BigDecimal("1.846052"));
        quotes.put("USDBBD", new BigDecimal("2.017606"));
        quotes.put("USDBDT", new BigDecimal("110.172891"));
        quotes.put("USDBGN", new BigDecimal("1.84627"));
        quotes.put("USDBHD", new BigDecimal("0.377109"));
        quotes.put("USDBIF", new BigDecimal("2835.956859"));
        quotes.put("USDBMD", new BigDecimal("1"));
        quotes.put("USDBND", new BigDecimal("1.365303"));
        quotes.put("USDBOB", new BigDecimal("6.905275"));
        quotes.put("USDBRL", new BigDecimal("4.979299"));
        quotes.put("USDINR", new BigDecimal("83.979299"));


        response.setQuotes(quotes);

        return response;
    }


}