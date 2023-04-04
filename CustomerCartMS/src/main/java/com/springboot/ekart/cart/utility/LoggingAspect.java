package com.springboot.ekart.cart.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;

public class LoggingAspect {
	
	private static Log logger = LogFactory.getLog(LoggingAspect.class);
	
	@AfterThrowing(pointcut = "execution(* com.springboot.ekart.cart.service.*Impl.*(..))", throwing = "exception")
	public void logExceptionFromService(Exception exception) {
		logger.error(exception.getMessage(), exception);
	}
	
}
