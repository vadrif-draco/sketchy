package asu.foe.sketchy.aspects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogginAspect {
	
	
		
	@Pointcut("execution(* asu.foe.sketchy.services.AuthService.login*(..))")
	public void LoggingServiceHandling() {}
	
	@After("LoggingServiceHandling()")
	public void adviceLogging() {
		LocalDateTime now = LocalDateTime.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    String formattedDateTime = now.format(formatter);
	    System.out.println("User logged in at: " + formattedDateTime);
		
	}
	
}
