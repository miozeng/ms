package com.mio.data.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Customer {

	private @GeneratedValue @Id Long customerId;
	
	private  String firstname;
	
	private  String  lastname;
	
	@JsonIgnore
	private String hobies;
	

	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getHobies() {
		return hobies;
	}
	public void setHobies(String hobies) {
		this.hobies = hobies;
	}
	
	
}
