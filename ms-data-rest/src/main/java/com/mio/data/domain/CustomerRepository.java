package com.mio.data.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path="customer")
public interface CustomerRepository extends CrudRepository<Customer,Long>{
	
	
	 @RestResource(exported = false)
	 @Override
	 public void delete(Long id);

}
