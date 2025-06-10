package com.branch.nequi_test.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class StockProductDto {
        private int existencia;
    }

