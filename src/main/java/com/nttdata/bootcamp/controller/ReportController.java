package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.dto.ActiveDto;
import com.nttdata.bootcamp.service.BusinessService;
import com.nttdata.bootcamp.service.StaffService;
import com.nttdata.bootcamp.service.CreditCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/report")
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    @Autowired
    private BusinessService businessService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private CreditCardService creditCardService;


    @GetMapping("/reportAccountsByCustomer/{dni}")
    public Flux<ActiveDto> findCurrentAccountByAccountNumber(@PathVariable("dni") String dni) {

        Flux<Active> listBusiness= businessService.findByCustomerBusiness(dni);
        Flux<Active> listCreditCard = creditCardService.findByCustomerCreditCard(dni);
        Flux<Active> listStaff= staffService.findByCustomerStaff(dni);

        ArrayList<ActiveDto> listActive = new ArrayList<>();

        listBusiness.
                toStream().
                forEach(x-> listActive.add(new ActiveDto(x.getDni(), x.getTypeCustomer(), x.getAccountNumber(),"CurrentAccount")));
        listCreditCard.
                toStream().
                forEach(x-> listActive.add(new ActiveDto(x.getDni(), x.getTypeCustomer(), x.getAccountNumber(),"SavingAccount")));
        listStaff.
                toStream().
                forEach(x-> listActive.add(new ActiveDto(x.getDni(), x.getTypeCustomer(), x.getAccountNumber(),"FixedTermAccount")));

        return Flux.fromStream(listActive.stream());


    }

}
