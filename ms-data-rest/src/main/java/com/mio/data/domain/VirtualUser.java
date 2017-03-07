package com.mio.data.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name="virtual",types=Customer.class)
public interface VirtualUser {
	@Value("#{target.firstname} #{target.lastname}") 
    String getFullInfo();
}
