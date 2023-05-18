package asu.foe.sketchy.aspects;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import asu.foe.sketchy.persistence.User;

@Aspect
public class LoggingAspect {

	@Autowired
	private LoggingRepository logRepository;

	@AfterReturning(pointcut = "execution(* asu.foe.sketchy.*.*(..)) && args(user,..)")
	public void logUserAction(JoinPoint joinPoint, User user) {
		String methodName = joinPoint.getSignature().getName();
		String args = Arrays.toString(joinPoint.getArgs());

		String message = getMessageForUserAction(methodName, args, user);
		logRepository.save(new Log(LocalDateTime.now(), message));
	}

	private String getMessageForUserAction(String methodName, String args, User user) {
		String message = "";
		switch (methodName) {
		case "login":
			message = "User with name " + user.getName() + " and ID " + user.getId() + " logged in at " + LocalDateTime.now();
			break;
		case "createSketch":
			message = "User with name " + user.getName() + " and ID " + user.getId() + " created a sketch at " + LocalDateTime.now();
			break;
		case "editSketch":
			message = "User with name " + user.getName() + " and ID " + user.getId() + " edited a sketch at " + LocalDateTime.now();
			break;
		default:
			message = "User with name " + user.getName() + " and ID " + user.getId() + " performed " + methodName + " with args " + args + " at " + LocalDateTime.now();
			break;
		}
		return message;
	}
}
