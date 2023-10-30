package com.currency.exchange.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversionResponseDTO {

     String currency;
     String currencyName;
     BigDecimal amount;
     List<CurrencyInfoDTO> convertedCurrencyInfo;



}
