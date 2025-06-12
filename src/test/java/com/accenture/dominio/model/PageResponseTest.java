package com.accenture.dominio.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {
    @Test
    void testPageResponseConstructorAndGetters() {
        List<String> content = List.of("A", "B");
        PageResponse<String> response = new PageResponse<>(content, 0, 0, 2, 1);
        assertEquals(content, response.getContent());
        assertEquals(2, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getPage());
    }

    @Test
    void testPageResponseSetters() {
        PageResponse<String> response = new PageResponse<>(null, 0, 0, 0, 0);
        response.setContent(List.of("X"));
        response.setTotalElements(1);
        response.setTotalPages(1);
        response.setPage(1);
        assertEquals(List.of("X"), response.getContent());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(1, response.getPage());
    }
}
