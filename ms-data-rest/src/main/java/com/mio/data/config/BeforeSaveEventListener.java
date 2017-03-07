package com.mio.data.config;

import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;

/**
 * BeforeCreateEvent

AfterCreateEvent

BeforeSaveEvent

AfterSaveEvent

BeforeLinkSaveEvent

AfterLinkSaveEvent

BeforeDeleteEvent

AfterDeleteEvent
 * @author admin
 *
 */
public class BeforeSaveEventListener extends AbstractRepositoryEventListener {

	  @Override
	  public void onBeforeSave(Object entity) {
		  System.out.println(" the entity before the Repository saves it");
	  }

	  @Override
	  public void onAfterDelete(Object entity) {
	   System.out.println("this entity has been deleted");
	  }
}
