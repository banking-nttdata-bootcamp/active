package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.repository.ActiveRepository;
import com.nttdata.bootcamp.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private ActiveRepository activeRepository;

    @Override
    public Flux<Active> findAllStaff() {
        Flux<Active> actives = activeRepository.findAll();
        return actives;
    }

    @Override
    public Flux<Active> findByCustomerStaff(String dni) {
        Flux<Active> actives = activeRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return actives;
    }

    @Override
    public Mono<Active> findByAccountNumberStaff(String accountNumber) {
        Mono<Active> active = activeRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return active;
    }

    @Override
    public Mono<Active> saveStaff(Active dataStaff) {
        Mono<Active> activeMono = Mono.empty();

        if(dataStaff.getTypeCustomer().equals("PERSONAL")){
            activeMono = this.findByCustomerStaff(dataStaff.getDni()).next();
        }
        return activeMono
                .flatMap(__ -> Mono.<Active>error(new Error("The customer have a staff account")))
                .switchIfEmpty(activeRepository.save(dataStaff));
    }


    @Override
    public Mono<Active> updateStaff(Active dataStaff) {
        Mono<Active> activeMono = findByAccountNumberStaff(dataStaff.getAccountNumber());
        //.delayElement(Duration.ofMillis(1000));
        try {
            dataStaff.setDni(activeMono.block().getDni());
            dataStaff.setCreationDate(activeMono.block().getCreationDate());
            return activeRepository.save(dataStaff);
        }catch (Exception e){
            return Mono.<Active>error(new Error("La cuenta activa personal con numero " + dataStaff.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> deleteStaff(String accountNumber) {
        Mono<Active> activeMono = findByAccountNumberStaff(accountNumber);
        try{
            return activeRepository.delete(activeMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("La cuenta activa personal con numero \" + dataActive.getAccountNumber() + \" NO EXISTE"));
        }
    }

}
