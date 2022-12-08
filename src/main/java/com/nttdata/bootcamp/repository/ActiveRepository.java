package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Active;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ActiveRepository extends ReactiveCrudRepository<Active, String> {
}
