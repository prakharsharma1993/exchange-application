# Currency Exchange Application

## Author
- **Name:** Prakhar Sharma
- **Email:** ps3266@gmail.com

## Overview
The Currency Exchange Application is a service that provides real-time exchange rate data and currency conversion capabilities. 
It allows users to fetch exchange rates, perform currency conversions, and obtain information about supported currencies.

## API Endpoints

### Get Exchange Rate
- **Endpoint:** `/exchange-rate`
- **Description:** Get the exchange rate from one currency (Currency A) to another (Currency B).
- **HTTP Method:** GET
- **Parameters:**
   - `currencyA` (required) - The source currency symbol (default value: USD).
   - `currencyB` (optional) - The target currency symbol. If not provided, returns exchange rates for all currencies.

### Currency Conversion
- **Endpoint:** `/currency-convertor`
- **Description:** Perform currency conversion from Currency A to Currency B or multiple target currencies.
- **HTTP Method:** POST
- **Request Body:** JSON object with the following properties:
   - `fromCurrency` (required) - The source currency symbol (Currency A).
   - `toCurrency` (required) - A list of target currency symbols.
   - `amount` (required) - The amount to be converted.

### Supported Currency List
- **Endpoint:** `/currency-list`
- **Description:** Get a list of supported currency symbols.
- **HTTP Method:** GET

## Caching
To optimize the performance of the Currency Exchange Application, in-memory caching is employed. 
The application uses the CaffeineCacheManager to cache exchange rate data for a period of 1 minute. This means that once exchange rates are fetched, they will be stored in memory and reused for subsequent requests within the 1-minute cache expiration period. This helps reduce the load on external resources and enhances response times.



## How to Run the Application

You can run the application using one of the following methods:

1. **Manual Configuration**:
   - Import the project into your preferred IDE.
   - Run it as a Spring Boot application.

2. **Docker**:
   - If you have Docker configured on your machine, you can build and run the application as a Docker container with the following commands:

   ```bash
   docker build -t exchange-service .
   docker run -p 8080:8080 exchange-service

## Testing
The Currency Exchange Application includes comprehensive test coverage to ensure the correctness of the implemented features.

## Swagger API Documentation

To access and explore the API using Swagger, open the following URL in your web browser:

[Swagger API Documentation](http://localhost:8080/exchange/swagger-ui/index.html)




