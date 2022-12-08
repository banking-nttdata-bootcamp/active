package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.service.CreditCardService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/creditCard")
public class CreditCardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardController.class);
    @Autowired
    private CreditCardService creditCardService;

    //search all credit card
    @GetMapping("/findAllCreditCard")
    public Flux<Active> findAllCreditCard() {
        Flux<Active> actives = creditCardService.findAllCreditCard();
        LOGGER.info("Registered Actives credit card Products: " + actives);
        return actives;
    }

    //Actives credit card search by customer

    @GetMapping("/findByCustomerCreditCard/{dni}")
    public Flux<Active> findByCustomerCreditCard(@PathVariable("dni") String dni) {
        Flux<Active> actives = creditCardService.findByCustomerCreditCard(dni);
        LOGGER.info("Registered Actives credit card Products by customer of dni: "+dni +"-" + actives);
        return actives;
    }
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
    //Search for active credit card by AccountNumber
    @GetMapping("/findByAccountNumberCreditCard/{accountNumber}")
    public Mono<Active> findByAccountNumberCreditCard(@PathVariable("accountNumber") String accountNumber) {
        LOGGER.info("Searching active credit card product by accountNumber: " + accountNumber);
        return creditCardService.findByAccountNumberCreditCard(accountNumber);
    }

    //Save active credit card
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
    @PostMapping(value = "/saveCreditCard")
    public Mono<Active> saveCreditCard(@RequestBody Active dataCreditCard){
        Mono.just(dataCreditCard).doOnNext(t -> {

                    t.setCreationDate(new Date());
                    t.setModificationDate(new Date());

                }).onErrorReturn(dataCreditCard).onErrorResume(e -> Mono.just(dataCreditCard))
                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

        Mono<Active> activeMono = creditCardService.saveCreditCard(dataCreditCard);
        return activeMono;
    }

    //Update active credit card
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
    @PutMapping("/updateCreditCard/{accountNumber}")
    public Mono<Active> updateBusiness(@PathVariable("accountNumber") String accountNumber,
                                                 @Valid @RequestBody Active dataCreditCard) {
        Mono.just(dataCreditCard).doOnNext(t -> {

                    t.setAccountNumber(accountNumber);
                    t.setModificationDate(new Date());

                }).onErrorReturn(dataCreditCard).onErrorResume(e -> Mono.just(dataCreditCard))
                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

        Mono<Active> updateActive = creditCardService.updateCreditCard(dataCreditCard);
        return updateActive;
    }

    //Delete active credit card
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetCreditCard")
    @DeleteMapping("/deleteCreditCard/{accountNumber}")
    public Mono<Void> deleteCreditCard(@PathVariable("accountNumber") String accountNumber) {
        LOGGER.info("Deleting active by accountNumber: " + accountNumber);
        Mono<Void> delete = creditCardService.deleteCreditCard(accountNumber);
        return delete;
    }

    private Mono<Active> fallBackGetCreditCard(Exception e){
        Active active = new Active();
        Mono<Active> creditCardMono = Mono.just(active);
        return creditCardMono;
    }

}
