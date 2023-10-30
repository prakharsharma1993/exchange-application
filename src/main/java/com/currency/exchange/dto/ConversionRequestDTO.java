package com.currency.exchange.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ConversionRequestDTO {

    private String fromCurrency;

    private BigDecimal amount;

    private List<String> toCurrency;


}
