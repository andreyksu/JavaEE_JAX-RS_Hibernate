package ru.annikonenkov.rs.atry;

import org.jboss.logging.Logger;

public class SimpleClassForCreateViaNew {
	private final static Logger log = Logger.getLogger(SimpleClassForCreateViaNew.class.getSimpleName());

	public void hello() {
		log.info("From   SimpleClassForCreateViaNew -> hello()");
	}

}
