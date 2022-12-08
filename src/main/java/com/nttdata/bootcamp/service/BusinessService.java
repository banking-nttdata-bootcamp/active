package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Active;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessService {
    Flux<Active> findAllBusiness();
    Mono<Active> findByAccountNumberBusiness(String accountNumber);
    Flux<Active> findByCustomerBusiness(String dni);
    Mono<Active> saveBusiness(Active dataBusiness);
    Mono<Active> updateBusiness(Active dataBusiness);
    Mono<Void> deleteBusiness(String accountNumber);
}
