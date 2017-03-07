package com.mio.data.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mio.data.domain.Customer;
import com.mio.data.domain.CustomerRepository;

@RepositoryRestController
public class CustomerController {

	@Autowired
	private CustomerRepository repository;

	@GetMapping(value = "/test/listCustomers")
	public @ResponseBody Iterable<Customer> getProducers() {
		Iterable<Customer> producers = repository.findAll();

		//
		// do some intermediate processing, logging, etc. with the producers
		//

		// Resources<String> resources = new Resources<String>(producers);
		//
		// resources.add(linkTo(methodOn(CustomerController.class).getProducers()).withSelfRel());

		// add other links as needed

		return producers;
	}
}
