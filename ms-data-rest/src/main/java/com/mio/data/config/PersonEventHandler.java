package com.mio.data.config;

import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import com.mio.data.domain.Customer;

@RepositoryEventHandler
public class PersonEventHandler {

	  @HandleBeforeSave
	  public void handlePersonSave(Customer p) {
		  System.out.println(" the entity before the Repository saves it");
	  }

	  @HandleBeforeSave
	  public void handleProfileSave(Profile p) {
		  System.out.println(" the entity before the Repository saves it");
	  }
}
