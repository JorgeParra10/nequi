package com.accenture.dominio.interfaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPaginator<T> {
    Mono<Long> count();
    Flux<T> findPage(int page, int size);
}
