package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.service.BusinessService;
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
@RequestMapping(value = "/business")
public class BusinessController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    private BusinessService businessService;

    //search all active business account
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
    @GetMapping("/findAllBusiness")
    public Flux<Active> findAllBusiness() {
        Flux<Active> actives = businessService.findAllBusiness();
        LOGGER.info("Registered Actives business Products: " + actives);
        return actives;
    }

    //Actives business search by customer
    @GetMapping("/findByCustomerBusiness/{dni}")
    public Flux<Active> findByCustomerBusiness(@PathVariable("dni") String dni) {
        Flux<Active> actives = businessService.findByCustomerBusiness(dni);
        LOGGER.info("Registered Actives business Products by customer of dni: "+dni +"-" + actives);
        return actives;
    }

    //Search business for active by AccountNumber
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetBusiness")
    @GetMapping("/findByAccountNumberBusiness/{accountNumber}")
    public Mono<Active> findByAccountNumberBusiness(@PathVariable("accountNumber") String accountNumber) {
        LOGGER.info("Searching active Business product by accountNumber: " + accountNumber);
        return businessService.findByAccountNumberBusiness(accountNumber);
    }

    //Save active business
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetBusiness")
    @PostMapping(value = "/saveBusiness")
    public Mono<Active> saveBusiness(@RequestBody Active dataBusiness){
        Mono.just(dataBusiness).doOnNext(t -> {

                    t.setCreationDate(new Date());
                    t.setModificationDate(new Date());

                }).onErrorReturn(dataBusiness).onErrorResume(e -> Mono.just(dataBusiness))
                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

        Mono<Active> activeMono = businessService.saveBusiness(dataBusiness);
        return activeMono;
    }

    //Update active business
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetBusiness")
    @PutMapping("/updateBusiness/{accountNumber}")
    public Mono<Active> updateBusiness(@PathVariable("accountNumber") String accountNumber,
                                               @Valid @RequestBody Active dataBusiness) {
        Mono.just(dataBusiness).doOnNext(t -> {

                    t.setAccountNumber(accountNumber);
                    t.setModificationDate(new Date());

                }).onErrorReturn(dataBusiness).onErrorResume(e -> Mono.just(dataBusiness))
                .onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

        Mono<Active> updateActive = businessService.updateBusiness(dataBusiness);
        return updateActive;
    }

    //Delete active business
    @CircuitBreaker(name = "active", fallbackMethod = "fallBackGetBusiness")
    @DeleteMapping("/deleteBusiness/{accountNumber}")
    public Mono<Void> deleteBusiness(@PathVariable("accountNumber") String accountNumber) {
        LOGGER.info("Deleting active business by accountNumber: " + accountNumber);
        Mono<Void> delete = businessService.deleteBusiness(accountNumber);
        return delete;
    }

    private Mono<Active> fallBackGetBusiness(Exception e){
        Active active= new Active();
        Mono<Active> businessMono = Mono.just(active);
        return businessMono;
    }

}
