package com.learnbatch.basicbatch.repositories;

import com.learnbatch.basicbatch.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer,Integer> {
}
