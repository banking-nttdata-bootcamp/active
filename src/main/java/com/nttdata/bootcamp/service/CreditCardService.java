package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Active;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditCardService {
    Flux<Active> findAllCreditCard();
    Mono<Active> findByAccountNumberCreditCard(String accountNumber);
    Flux<Active> findByCustomerCreditCard(String dni);
    Mono<Active> saveCreditCard(Active dataCreditCard);
    Mono<Active> updateCreditCard(Active dataCreditCard);
    Mono<Void> deleteCreditCard(String accountNumber);
}
