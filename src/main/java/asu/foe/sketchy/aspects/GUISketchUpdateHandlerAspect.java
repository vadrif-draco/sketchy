package asu.foe.sketchy.aspects;

import java.util.HashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIPen;
import asu.foe.sketchy.SketchyApplication;
import asu.foe.sketchy.kafka.KafkaGUISketchUpdateTransaction;
import asu.foe.sketchy.kafka.KafkaGUISketchUpdateTransaction.SketchUpdateType;
import asu.foe.sketchy.scenes.GUISketchScene;

@Aspect
@Component
public class GUISketchUpdateHandlerAspect {

	private HashMap<String, Integer> prevNumOfShapesPerSketch = new HashMap<>();

	@Lazy
	@Autowired
	private KafkaTemplate<String, KafkaGUISketchUpdateTransaction> guiSketchUpdateKafkaTemplate;

//	// Pointcut to execute on all the methods of all classes in application
//	@Pointcut("within(asu.foe.sketchy.*)")
//	public void allMethodsPointcut() {}
//
//	@Before("allMethodsPointcut()")
//	public void adviceBeforeAllMethodsPointcut(JoinPoint joinPoint) {
//		System.out.println("\n(asu.foe.sketchy.*) Before method: " + joinPoint.getSignature());
//	}

	// Pointcut for GUI Sketch Scene Handler
	@Pointcut("execution(* asu.foe.sketchy.services.GUISketchUpdateHandlerService.handle*(..))")
	public void guiSketchSceneHandlerServicePointcut() {}

	@After("guiSketchSceneHandlerServicePointcut()")
	public void adviceAfterGUISketchSceneHandlerServicePointcut(JoinPoint joinPoint) {

		// Prepare a new sketch update transaction to send through kafka
		KafkaGUISketchUpdateTransaction transaction = new KafkaGUISketchUpdateTransaction();

		// Get the sketch at this moment (right after the handler updated it)
		GUISketchScene sketch = (GUISketchScene) joinPoint.getArgs()[0];

		// Set the session Id (unique per sketch session) for the transaction
		// It is necessary so that a producer session doesn't consume what it produces
		transaction.setSessionId(sketch.getSessionId());

		// Set the transaction's pen and mouse coordinates (which were passed as arguments to the handler)
		transaction.setPen((GUIPen) joinPoint.getArgs()[1]);
		transaction.setMouseX((double) joinPoint.getArgs()[2]);
		transaction.setMouseY((double) joinPoint.getArgs()[3]);

		// Extract current number of shapes in the sketch
		Integer numOfShapes = sketch.getShapesPane().getChildren().size() - sketch.getNumOfActiveCollaborators();
		Integer prevNumOfShapes = prevNumOfShapesPerSketch.get(sketch.getSketchId());

		// According to the current number of shapes compared to the previous one, determine the type of change
		if (prevNumOfShapes != null) {
			if (numOfShapes > prevNumOfShapes) transaction.setUpdateType(SketchUpdateType.ADD);
			else if (numOfShapes == prevNumOfShapes) transaction.setUpdateType(SketchUpdateType.EDIT);
			else if (numOfShapes < prevNumOfShapes) transaction.setUpdateType(SketchUpdateType.REMOVE);
		} else {
			transaction.setUpdateType(SketchUpdateType.ADD);
		}

		// Update memory for the next comparison for this sketch
		prevNumOfShapesPerSketch.put(sketch.getSketchId(), numOfShapes);

		// Finally, send the transaction
		String sketchId = SketchyApplication.getCurrentSketch().getId().toString();
		guiSketchUpdateKafkaTemplate.send("sketch-updates-" + sketchId, "transaction", transaction);

	}

}
