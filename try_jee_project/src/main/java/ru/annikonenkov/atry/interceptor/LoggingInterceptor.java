package ru.annikonenkov.atry.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@Loggable
public class LoggingInterceptor {
	@Inject
	private Logger logger;

	@AroundInvoke
	public Object logMethod(InvocationContext ic) throws Exception {
		logger.info("Enter:   " + ic.getTarget().getClass().getName() + "____" + ic.getMethod().getName());
		try {
			return ic.proceed();
		} finally {
			logger.info("Exit:   " + ic.getTarget().getClass().getName() + "____" + ic.getMethod().getName());
		}
	}
}
