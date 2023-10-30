package com.currency.exchange.service;


import com.currency.exchange.dto.ConversionRequestDTO;
import com.currency.exchange.dto.ConversionResponseDTO;
import com.currency.exchange.dto.CurrencyInfoDTO;
import com.currency.exchange.exception.APINotAvailableException;
import com.currency.exchange.exception.InvalidCurrencyException;

import java.util.List;

public interface ExchangeService {

    // Give exchange rate from one currency to another
    public ConversionResponseDTO getExchangeData(String currencyA, String currencyB) throws InvalidCurrencyException, APINotAvailableException;

    // Convert total amount from one currency to another
    public ConversionResponseDTO getCurrencyConversion(ConversionRequestDTO conversionRequestDTO) throws InvalidCurrencyException, APINotAvailableException;


    // Give a list of available currency and its symbol
    public List<CurrencyInfoDTO> getCurrencyList();

}
