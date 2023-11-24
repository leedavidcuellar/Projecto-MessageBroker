package com.messagebroker.javacoin.repositoriy;

import com.messagebroker.javacoin.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Customer findCustomerByDniCustomer(String dniCustomer);
}
