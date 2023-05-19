package asu.foe.sketchy.aspects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIMainStage;
import asu.foe.sketchy.SketchyApplication;
import asu.foe.sketchy.kafka.KafkaLoggingTransaction;

@Aspect
@Component
public class LoggingAspect {

	@Autowired
	GUIMainStage mainStage;

	@Autowired
	private KafkaTemplate<String, KafkaLoggingTransaction> loggingKafkaTemplate;

	@Pointcut("execution(* asu.foe.sketchy.scenes.GUISketchScene.getRoot*(..))")
	public void openSketchPointcut() {}

	@After("openSketchPointcut()")
	public void loggingAfterOpenSketchPotincut() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = now.format(formatter);
		String userName = SketchyApplication.getCurrentUser().getName();
		KafkaLoggingTransaction transaction = new KafkaLoggingTransaction();
		transaction.setMessage(userName + " opened the sketch at: " + formattedDateTime);
		String sketchId = SketchyApplication.getCurrentSketch().getId().toString();
		loggingKafkaTemplate.send("sketch-log-" + sketchId, "transaction", transaction);
	}

	@Pointcut("execution(* asu.foe.sketchy.services.GUISketchDataHandlerService.updateSketchData(..))")
	public void updateSketchDataPointcut() {}

	@Before("updateSketchDataPointcut()")
	public void adviceBeforeUpdateSketchDataPointcut(JoinPoint joinPoint) {
		// The 3rd argument is the "internal" flag which determines whether this change was induced internally
		// We want to propagate the update event ONLY IF it is indeed an internally induced change
		if (((Boolean) joinPoint.getArgs()[2])) {
			String newTitle = (String) joinPoint.getArgs()[0];
			String newDesc = (String) joinPoint.getArgs()[1];
			String oldTitle = SketchyApplication.getCurrentSketch().getTitle();
			String oldDesc = SketchyApplication.getCurrentSketch().getDescription();
			String userName = SketchyApplication.getCurrentUser().getName();
			String sketchId = SketchyApplication.getCurrentSketch().getId().toString();
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = now.format(formatter);
			if (newTitle != null && !newTitle.equals(oldTitle)) {
				KafkaLoggingTransaction transaction = new KafkaLoggingTransaction();
				transaction.setMessage(userName + " changed sketch title from " + oldTitle + " to " + newTitle + " at: " + formattedDateTime);
				loggingKafkaTemplate.send("sketch-log-" + sketchId, "transaction", transaction);
			}
			if (newDesc != null && !newDesc.equals(oldDesc)) {
				KafkaLoggingTransaction transaction = new KafkaLoggingTransaction();
				transaction.setMessage(userName + " changed sketch description from " + oldDesc + " to " + newDesc + " at: " + formattedDateTime);
				loggingKafkaTemplate.send("sketch-log-" + sketchId, "transaction", transaction);
			}

		}
	}

}
