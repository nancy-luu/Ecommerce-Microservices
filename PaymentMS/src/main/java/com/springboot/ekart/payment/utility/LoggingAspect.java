package com.springboot.ekart.payment.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;

import com.springboot.ekart.payment.utility.LoggingAspect;

public class LoggingAspect {
	
	private static Log logger = LogFactory.getLog(LoggingAspect.class);
	
	@AfterThrowing(pointcut = "execution(* com.springboot.ekart.payment.service.*Impl.*(..))", throwing = "exception")
	public void logExceptionFromService(Exception exception) {
		logger.error(exception.getMessage(), exception);
	}

}
