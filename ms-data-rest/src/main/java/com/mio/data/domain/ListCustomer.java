package com.mio.data.domain;

import org.springframework.data.rest.core.config.Projection;

@Projection(name="list",types=Customer.class)
public interface ListCustomer {
	String getFirstname();
	Long getCustomerId();

}
