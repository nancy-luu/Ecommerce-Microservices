package com.springboot.ekart.customer.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;

import com.springboot.ekart.customer.utility.LoggingAspect;

public class LoggingAspect {
	
	private static Log logger = LogFactory.getLog(LoggingAspect.class);
	
	@AfterThrowing(pointcut = "execution(* com.springboot.ekart.customer.service.*Impl.*(..))", throwing = "exception")
	public void logExceptionFromService(Exception exception) {
		logger.error(exception.getMessage(), exception);
	}
	
}
