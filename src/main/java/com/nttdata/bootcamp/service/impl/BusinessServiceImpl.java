package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.repository.ActiveRepository;
import com.nttdata.bootcamp.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private ActiveRepository activeRepository;

    @Override
    public Flux<Active> findAllBusiness() {
        Flux<Active> actives = activeRepository.findAll();
        return actives;
    }

    @Override
    public Flux<Active> findByCustomerBusiness(String dni) {
        Flux<Active> actives = activeRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return actives;
    }

    @Override
    public Mono<Active> findByAccountNumberBusiness(String accountNumber) {
        Mono<Active> active = activeRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return active;
    }

    @Override
    public Mono<Active> saveBusiness(Active dataBusiness) {
        Mono<Active> activeMono= findByAccountNumberBusiness(dataBusiness.getAccountNumber())
                .flatMap(__ -> Mono.<Active>error(new Error("La cuenta activa empresarial con numero " + dataBusiness.getAccountNumber() + " YA EXISTE")))
                .switchIfEmpty(activeRepository.save(dataBusiness));
        return activeMono;
    }

    @Override
    public Mono<Active> updateBusiness(Active dataBusiness) {
        Mono<Active> activeMono = findByAccountNumberBusiness(dataBusiness.getAccountNumber());
        //.delayElement(Duration.ofMillis(1000));
        try {
            dataBusiness.setDni(activeMono.block().getDni());
            dataBusiness.setCreationDate(activeMono.block().getCreationDate());
            return activeRepository.save(dataBusiness);
        }catch (Exception e){
            return Mono.<Active>error(new Error("La cuenta activa empresarial con numero " + dataBusiness.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> deleteBusiness(String accountNumber) {
        Mono<Active> activeMono = findByAccountNumberBusiness(accountNumber);
        try{
            return activeRepository.delete(activeMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("La cuenta activa empresarial con numero \" + dataActive.getAccountNumber() + \" NO EXISTE"));
        }
    }

}
