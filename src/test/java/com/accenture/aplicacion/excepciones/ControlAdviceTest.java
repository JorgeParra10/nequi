package com.accenture.aplicacion.excepciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.accenture.aplicacion.exceptiones.ControlAdvice;
import com.accenture.aplicacion.exceptiones.ExceptionResponse;
import com.accenture.dominio.excepcion.ErrorBadRequest;
import com.accenture.dominio.excepcion.ErrorException;
import com.accenture.dominio.excepcion.ErrorNotFound;
import com.accenture.dominio.excepcion.ExceptionAlreadyExist;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class ControlAdviceTest {

    private ControlAdvice controlAdvice;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        controlAdvice = new ControlAdvice();
        exchange = Mockito.mock(ServerWebExchange.class, Mockito.RETURNS_DEEP_STUBS);
        
        Mockito.when(exchange.getRequest().getPath().toString()).thenReturn("/api/test");
        Mockito.when(exchange.getRequest().getMethod().name()).thenReturn("GET");
    }

    @Test
    void handleBadRequest_ShouldReturnBadRequestStatus() {
        ErrorBadRequest ex = new ErrorBadRequest("Bad request message");
        Mono<ResponseEntity<ExceptionResponse>> result = controlAdvice.handleBadRequest(ex, exchange);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    ExceptionResponse body = response.getBody();
                    return response.getStatusCode() == HttpStatus.BAD_REQUEST
                            && body.getStatusCode() == HttpStatus.BAD_REQUEST.value()
                            && body.getMessage().equals("Bad request message");
                })
                .verifyComplete();
    }

    @Test
    void handleAlreadyExist_ShouldReturnConflictStatus() {
        ExceptionAlreadyExist ex = new ExceptionAlreadyExist("Already exists message");
        Mockito.when(exchange.getRequest().getMethod().name()).thenReturn("POST");

        Mono<ResponseEntity<ExceptionResponse>> result = controlAdvice.handleAlreadyExist(ex, exchange);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    ExceptionResponse body = response.getBody();
                    return response.getStatusCode() == HttpStatus.CONFLICT
                            && body.getStatusCode() == HttpStatus.CONFLICT.value()
                            && body.getMessage().equals("Already exists message");
                })
                .verifyComplete();
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundStatus() {
        ErrorNotFound ex = new ErrorNotFound("Not found message");
        Mockito.when(exchange.getRequest().getPath().toString()).thenReturn("/api/test/123");

        Mono<ResponseEntity<ExceptionResponse>> result = controlAdvice.handleNotFound(ex, exchange);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    ExceptionResponse body = response.getBody();
                    return response.getStatusCode() == HttpStatus.NOT_FOUND
                            && body.getStatusCode() == HttpStatus.NOT_FOUND.value()
                            && body.getMessage().equals("Not found message");
                })
                .verifyComplete();
    }

    @Test
    void handleGenericError_ShouldReturnServiceUnavailableStatus() {
        ErrorException ex = new ErrorException("Service unavailable message");
        Mockito.when(exchange.getRequest().getPath().toString()).thenReturn("/api/test");
        Mockito.when(exchange.getRequest().getMethod().name()).thenReturn("GET");
        Mono<ResponseEntity<ExceptionResponse>> result = controlAdvice.handleGenericError(ex, exchange);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    ExceptionResponse body = response.getBody();
                    return response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE
                            && body.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE.value()
                            && body.getMessage().equals("Service unavailable message");
                })
                .verifyComplete();
    }

    @Test
    void handleResponseStatusException_ShouldReturnAppropriateStatus() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request reason");
        Mockito.when(exchange.getRequest().getMethod().name()).thenReturn("POST");

        Mono<ResponseEntity<ExceptionResponse>> result = controlAdvice.handleResponseStatusException(ex, exchange);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    ExceptionResponse body = response.getBody();
                    return response.getStatusCode() == HttpStatus.BAD_REQUEST
                            && body.getStatusCode() == HttpStatus.BAD_REQUEST.value()
                            && body.getMessage().equals("Bad request reason");
                })
                .verifyComplete();
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerErrorStatus() {
        Exception ex = new RuntimeException("Unexpected error");
        Mockito.when(exchange.getRequest().getPath().toString()).thenReturn("/api/test");
        Mockito.when(exchange.getRequest().getMethod().name()).thenReturn("GET");

        Mono<ResponseEntity<ExceptionResponse>> result = controlAdvice.handleAllExceptions(ex, exchange);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    ExceptionResponse body = response.getBody();
                    return response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
                            && body.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()
                            && body.getMessage().contains("Unexpected error");
                })
                .verifyComplete();
    }

}
