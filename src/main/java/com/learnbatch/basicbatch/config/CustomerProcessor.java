package com.learnbatch.basicbatch.config;

import com.learnbatch.basicbatch.entities.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomerProcessor implements ItemProcessor<Customer,Customer> {
    @Override
    public Customer process(Customer item) throws Exception {

        //logic to process data
        return item;
    }
}
