package com.branch.nequi_test.utils;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseObj<T> {

    @Schema(description = "Objeto de datos de la respuesta")
    private T data;
}

