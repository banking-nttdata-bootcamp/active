package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.repository.ActiveRepository;
import com.nttdata.bootcamp.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private ActiveRepository activeRepository;

    @Override
    public Flux<Active> findAllCreditCard() {
        Flux<Active> actives = activeRepository.findAll();
        return actives;
    }

    @Override
    public Flux<Active> findByCustomerCreditCard(String dni) {
        Flux<Active> actives = activeRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return actives;
    }

    @Override
    public Mono<Active> findByAccountNumberCreditCard(String accountNumber) {
        Mono<Active> active = activeRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return active;
    }

    @Override
    public Mono<Active> saveCreditCard(Active dataActiveCreditCard){
        dataActiveCreditCard.setBusiness(false);
        dataActiveCreditCard.setStaff(false);
        dataActiveCreditCard.setCreditCard(true);
        Mono<Active> activeMono = findByAccountNumberCreditCard(dataActiveCreditCard.getAccountNumber())
                .flatMap(__ -> Mono.<Active>error(new Error("The credit card " + dataActiveCreditCard.getAccountNumber() + " exist")))
                .switchIfEmpty(activeRepository.save(dataActiveCreditCard));
        return activeMono;
    }

    @Override
    public Mono<Active> updateCreditCard(Active dataActiveCreditCard) {
        Mono<Active> activeMono = findByAccountNumberCreditCard(dataActiveCreditCard.getAccountNumber());
        //.delayElement(Duration.ofMillis(1000));
        try {
            dataActiveCreditCard.setDni(activeMono.block().getDni());
            dataActiveCreditCard.setCreationDate(activeMono.block().getCreationDate());
            return activeRepository.save(dataActiveCreditCard);
        }catch (Exception e){
            return Mono.<Active>error(new Error("The credit card " + dataActiveCreditCard.getAccountNumber() + " does not exists"));
        }
    }

    @Override
    public Mono<Void> deleteCreditCard(String accountNumber) {
        Mono<Active> activeMono = findByAccountNumberCreditCard(accountNumber);
        try{
            return activeRepository.delete(activeMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("The credit card " + accountNumber + " does not exists"));
        }
    }

}
