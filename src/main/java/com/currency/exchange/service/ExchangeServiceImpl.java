package com.currency.exchange.service;

import com.currency.exchange.constant.CurrencyEnum;
import com.currency.exchange.dto.ConversionRequestDTO;
import com.currency.exchange.dto.ConversionResponseDTO;
import com.currency.exchange.dto.CurrencyApiResponseDTO;
import com.currency.exchange.dto.CurrencyInfoDTO;
import com.currency.exchange.exception.APINotAvailableException;
import com.currency.exchange.exception.InvalidCurrencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private static final Integer DECIMAL_PLACE = 6;
    private static final String EXCEPTION_MESSAGE = " Enter a valid currency";
    private static final String INVALID_CURRENCY_MESSAGE = "Invalid Currency";
    private static final String EXCHANGE_UNAVAILABLE = "Exchange is not available";


    @Autowired
    CurrencyExchangeClient currencyExchangeClient;

    public ConversionResponseDTO getExchangeData(String currencyA, String currencyB) throws InvalidCurrencyException, APINotAvailableException {
        validateCurrencyCodes(currencyA, currencyB);
        Map<String, BigDecimal> quotes = fetchAndPrepareQuotes();
        return setCurrencyData(currencyA, currencyB, quotes, new ArrayList<>(), BigDecimal.ONE);


    }

    public ConversionResponseDTO getCurrencyConversion(ConversionRequestDTO conversionRequestDTO) throws InvalidCurrencyException, APINotAvailableException {

        validCurrencyForConversion(conversionRequestDTO);
        Map<String, BigDecimal> quotes = fetchAndPrepareQuotes();
        return setCurrencyData(conversionRequestDTO.getFromCurrency(), "", quotes, conversionRequestDTO.getToCurrency(), conversionRequestDTO.getAmount());


    }

    private ConversionResponseDTO setCurrencyData(String currencyA, String currencyB, Map<String, BigDecimal> quotes, List<String> currencyList, BigDecimal amount) throws InvalidCurrencyException {
        ConversionResponseDTO conversionResponseDTO = new ConversionResponseDTO();
        List<CurrencyInfoDTO> currencyInfoDTOS = new ArrayList<>();
        conversionResponseDTO.setCurrency(currencyA);
        conversionResponseDTO.setAmount(amount);
        conversionResponseDTO.setCurrencyName(CurrencyEnum.valueOf(currencyA).getValue());

        BigDecimal currencyARateInDollar = quotes.get(currencyA);


        if (currencyB != null && isValidCurrency(currencyB)) {
            CurrencyInfoDTO currencyInfoDTO = new CurrencyInfoDTO();
            BigDecimal currencyBRate = quotes.get(currencyB);
            BigDecimal finalRate = currencyBRate.divide(currencyARateInDollar, DECIMAL_PLACE, RoundingMode.HALF_DOWN);
            setCurrencyInfoDTO(currencyInfoDTO, currencyB, finalRate);
            currencyInfoDTOS.add(currencyInfoDTO);
        } else if (!currencyList.isEmpty()) {
            for (String currency : currencyList) {
                CurrencyInfoDTO currencyInfoDTO = new CurrencyInfoDTO();
                BigDecimal currencyBRate = quotes.get(currency);

                // when currency is not available in exchange
                if (currencyBRate == null) {
                    currencyInfoDTO.setCurrency(INVALID_CURRENCY_MESSAGE);
                    currencyInfoDTO.setCurrencySymbol(currency);

                } else {
                    BigDecimal finalOutCome = currencyBRate.divide(currencyARateInDollar, DECIMAL_PLACE, RoundingMode.HALF_DOWN).multiply(amount);
                    setCurrencyInfoDTO(currencyInfoDTO, currency, finalOutCome);
                }
                currencyInfoDTOS.add(currencyInfoDTO);
            }
        } else {
            // to remove the duplicate currency
            quotes.remove(currencyA);
            for (String currency : quotes.keySet()) {
                CurrencyInfoDTO currencyInfoDTO = new CurrencyInfoDTO();
                BigDecimal decimal = quotes.get(currency);
                BigDecimal finalOutCome = decimal.divide(currencyARateInDollar, DECIMAL_PLACE, RoundingMode.HALF_DOWN);
                setCurrencyInfoDTO(currencyInfoDTO, currency, finalOutCome);
                currencyInfoDTOS.add(currencyInfoDTO);
            }
        }
        conversionResponseDTO.setConvertedCurrencyInfo(currencyInfoDTOS);
        return conversionResponseDTO;
    }


    private void validCurrencyForConversion(ConversionRequestDTO conversionRequestDTO) throws InvalidCurrencyException {

        if (Objects.isNull(conversionRequestDTO.getFromCurrency()) || !isValidCurrencyCode(conversionRequestDTO.getFromCurrency())) {
            throw new InvalidCurrencyException(INVALID_CURRENCY_MESSAGE + " : " + conversionRequestDTO.getFromCurrency());

        } else if (conversionRequestDTO.getToCurrency().stream().noneMatch(this::isValidCurrencyCode)) {
            throw new InvalidCurrencyException(INVALID_CURRENCY_MESSAGE+ " : "+conversionRequestDTO.getToCurrency() +". Please enter at least one valid currency");

        }

    }

    private void validateCurrencyCodes(String currencyA, String currencyB) throws InvalidCurrencyException {
        if (!isValidCurrency(currencyA)) {
            throw new InvalidCurrencyException(INVALID_CURRENCY_MESSAGE + " : " + currencyA);
        }
        if (currencyB != null && !isValidCurrency(currencyB)) {
            throw new InvalidCurrencyException(INVALID_CURRENCY_MESSAGE + " : " + currencyB);

        }
    }


    private boolean isValidCurrency(String currency) {
        try {
            CurrencyEnum.valueOf(currency);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    Map<String, BigDecimal> fetchAndPrepareQuotes() throws APINotAvailableException {
        CurrencyApiResponseDTO currencyData = currencyExchangeClient.getDataFromApi();
        if (Objects.nonNull(currencyData.getQuotes())) {
            Map<String, BigDecimal> quotes = removeUSDPrefix(currencyData.getQuotes());
            quotes.put(CurrencyEnum.USD.name(), BigDecimal.ONE);
            return quotes;
        } else {
            throw new APINotAvailableException(EXCHANGE_UNAVAILABLE);
        }

    }

    private void setCurrencyInfoDTO(CurrencyInfoDTO currencyInfoDTO, String currency, BigDecimal finalOutCome) {
        currencyInfoDTO.setAmount(finalOutCome);
        currencyInfoDTO.setCurrency(CurrencyEnum.valueOf(currency).getValue());
        currencyInfoDTO.setCurrencySymbol(currency);
    }

    private boolean isValidCurrencyCode(String currencyCode) {
        try {
            CurrencyEnum.valueOf(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<CurrencyInfoDTO> getCurrencyList() {
        return Arrays.stream(CurrencyEnum.values())
                .map(currency -> new CurrencyInfoDTO(currency.name(), currency.getValue(), null))
                .toList();
    }

    public Map<String, BigDecimal> removeUSDPrefix(Map<String, BigDecimal> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().replace("USD", ""), // Remove the "USD" prefix
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }


}

