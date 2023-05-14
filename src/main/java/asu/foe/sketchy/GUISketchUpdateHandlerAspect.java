package asu.foe.sketchy;

import java.time.Instant;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUISketchUpdateTransaction.UpdateType;

@Aspect
@Component
public class GUISketchUpdateHandlerAspect {

	private int previousNumOfShapes = 0;

	@Lazy
	@Autowired
	private KafkaTemplate<String, GUISketchUpdateTransaction> guiSketchUpdateKafkaTemplate;

//	// Pointcut to execute on all the methods of all classes in application
//	@Pointcut("within(asu.foe.sketchy.*)")
//	public void allMethodsPointcut() {}
//
//	@Before("allMethodsPointcut()")
//	public void adviceBeforeAllMethodsPointcut(JoinPoint joinPoint) {
//		System.out.println("\n(asu.foe.sketchy.*) Before method: " + joinPoint.getSignature());
//	}

	// Pointcut for GUI Sketch Scene Handler
	@Pointcut("execution(* asu.foe.sketchy.GUISketchUpdateHandlerService.handle*(..))")
	public void guiSketchSceneHandlerServicePointcut() {}

	@After("guiSketchSceneHandlerServicePointcut()")
	public void adviceAfterGUISketchSceneHandlerServicePointcut(JoinPoint joinPoint) {

		// Prepare a new sketch update transaction to send through kafka
		GUISketchUpdateTransaction transaction = new GUISketchUpdateTransaction();

		// Set the session Id (unique per program run) for the transaction
		transaction.setSessionId(SketchyApplication.sessionId);

		// Set the transaction timestamp as the current epoch in milliseconds
		transaction.setTimestamp(Instant.now().toEpochMilli());

		// Get the sketch at this moment (right after the handler updated it)
		GUISketchScene sketch = (GUISketchScene) joinPoint.getArgs()[0];

		// Set the transaction's pen and mouse coordinates (which were passed as arguments to the handler)
		transaction.setPen((Pen) joinPoint.getArgs()[1]);
		transaction.setMouseX((double) joinPoint.getArgs()[2]);
		transaction.setMouseY((double) joinPoint.getArgs()[3]);

		// Extract current number of shapes in the sketch
		int numOfShapes = sketch.shapesPane.getChildren().size();

		// According to the current number of shapes compared to the previous one, determine the type of change
		if (numOfShapes > previousNumOfShapes) transaction.setUpdateType(UpdateType.ADD);
		else if (numOfShapes == previousNumOfShapes) transaction.setUpdateType(UpdateType.EDIT);
		else if (numOfShapes < previousNumOfShapes) transaction.setUpdateType(UpdateType.REMOVE);

		// Update memory for the next comparison
		previousNumOfShapes = numOfShapes;

		// Finally, send the transaction
		guiSketchUpdateKafkaTemplate.send("sketch-updates", "", transaction);

	}

}
