package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Active;
import com.nttdata.bootcamp.entity.dto.StaffAccountDto;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/staff")
public class StaffController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StaffController.class);
	@Autowired
	private StaffService staffService;

	//Actives staff search
	@GetMapping("/findAllStaff")
	public Flux<Active> findAllStaff() {
		Flux<Active> actives = staffService.findAllStaff();
		LOGGER.info("Registered Actives Staff Products: " + actives);
		return actives;
	}

	//Actives staff search by customer
	@GetMapping("/findByCustomerStaff/{dni}")
	public Flux<Active> findByCustomerStaff(@PathVariable("dni") String dni) {
		Flux<Active> actives = staffService.findByCustomerStaff(dni);
		LOGGER.info("Registered Actives Staff Products by customer of dni: "+dni +"-" + actives);
		return actives;
	}
	@CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
	//Search for active staff by AccountNumber
	@GetMapping("/findByAccountNumberStaff/{accountNumber}")
	public Mono<Active> findByAccountNumberStaff(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching active Staff product by accountNumber: " + accountNumber);
		return staffService.findByAccountNumberStaff(accountNumber);
	}

	//Save active staff
	@CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
	@PostMapping(value = "/saveStaff")
	public Mono<Active> saveStaff(@RequestBody StaffAccountDto dataStaff){
		Active active= new Active();
		Mono.just(active).doOnNext(t -> {
			t.setDni(dataStaff.getDni());
			t.setTypeCustomer(Constant.PERSONAL_CUSTOMER);
			t.setAccountNumber(dataStaff.getAccountNumber());
			t.setCreditLimit(dataStaff.getCreditLimit());
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
			t.setStatus(Constant.ACTIVE_ACTIVE);
				}).onErrorReturn(active).onErrorResume(e -> Mono.just(active))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Active> activeMono = staffService.saveStaff(active);
		return activeMono;
	}
	@CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
	//Update active staff
	@PutMapping("/updateStaff/{accountNumber}")
	public Mono<Active> updateStaff(@PathVariable("accountNumber") String accountNumber,
                                          @Valid @RequestBody Active dataStaff) {
		Mono.just(dataStaff).doOnNext(t -> {

					t.setAccountNumber(accountNumber);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataStaff).onErrorResume(e -> Mono.just(dataStaff))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Active> updateActive = staffService.updateStaff(dataStaff);
		return updateActive;
	}
	@CircuitBreaker(name = "active", fallbackMethod = "fallBackGetStaff")
	//Delete active staff
	@DeleteMapping("/deleteStaff/{accountNumber}")
	public Mono<Void> deleteStaff(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting active by accountNumber: " + accountNumber);
		Mono<Void> delete = staffService.deleteStaff(accountNumber);
		return delete;
	}

	//circuit breaker
	private Mono<Active> fallBackGetStaff(Exception e){
		Active active = new Active();
		Mono<Active> staffMono = Mono.just(active);
		return staffMono;
	}

}
