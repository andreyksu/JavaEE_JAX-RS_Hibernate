package ru.annikonenkov.atry.logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.jboss.logging.Logger;

public class LoggingProducer {
	@Produces
	private Logger createLogger(InjectionPoint injectPoint) {
		return Logger.getLogger(injectPoint.getMember().getDeclaringClass().getName());
	}

}
