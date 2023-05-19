package asu.foe.sketchy.scenes;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIMainStage;
import asu.foe.sketchy.SketchyApplication;
import asu.foe.sketchy.persistence.User;
import asu.foe.sketchy.persistence.UserRepository;
import asu.foe.sketchy.services.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Lazy
@Component
public class GUIRegistrationScene {
	
	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GUIMainStage mainStage;



	@Autowired
	private GUISketchListScene sketchListScene;

	public Parent getRoot() {

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setStyle("-fx-font-size: 14pt;");

		Label titleLabel = new Label("Create Account");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		grid.add(titleLabel, 0, 0, 2, 1);

		Label nameLabel = new Label("Username:");
		TextField nameTextField = new TextField();
		nameTextField.setPromptText("Enter your username");
		grid.add(nameLabel, 0, 1);
		grid.add(nameTextField, 1, 1);

		Label emailLabel = new Label("Email:");
		TextField emailTextField = new TextField();
		emailTextField.setPromptText("Enter your email");
		grid.add(emailLabel, 0, 2);
		grid.add(emailTextField, 1, 2);

		Label passwordLabel = new Label("Password:");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter your password");
		grid.add(passwordLabel, 0, 3);
		grid.add(passwordField, 1, 3);

		// Create a "Register" button and set its action
		Button registerButton = new Button("Register");
		registerButton.setOnAction(event -> {
			// Validate that all fields are filled in
			if (nameTextField.getText().isEmpty() || emailTextField.getText().isEmpty()
						|| passwordField.getText().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields.");
				alert.showAndWait();
				return;
			}
			// Validate email format
			if (!isValidEmail(emailTextField.getText())) {
				// Show error message
				showAlert("Invalid email format!");
				return;
			}
			// Create a new User object with the entered information
			User user = new User();
			user.setId(new Random().nextLong(0, Long.MAX_VALUE));
			user.setName(nameTextField.getText());
			user.setEmail(emailTextField.getText());
			user.setPassword(passwordField.getText());

			// Save the User object to the database using the UserRepository and set it as the current user
			SketchyApplication.currentUser = userRepository.save(user);

			// Redirect to sketch list upon successful registration...
			mainStage.scene.setRoot(sketchListScene.getRoot());
		});

		// Create an HBox to hold the "Register" and "Login" buttons
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(registerButton);
		buttonBox.setPadding(new Insets(16, 16, 16, 16));

		grid.add(buttonBox, 1, 4);

		Label loginLabel = new Label("Already have an account? ");
		Button gotoLoginButton = new Button("Login");
		gotoLoginButton.setOnAction(event -> {

			// Get the new scene and set it to the stage
//			mainStage.scene.setRoot(loginScene.getRoot());
			authService.register();

		});

		HBox loginBox = new HBox(5);
		loginBox.setAlignment(Pos.CENTER_RIGHT);
		loginBox.getChildren().addAll(loginLabel, gotoLoginButton);
		grid.add(loginBox, 1, 5);

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(grid);

		return root;
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
					+ "[a-zA-Z0-9_+&*-]+)*@"
					+ "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

		return email.matches(emailRegex);
	}

	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR, message);
		alert.showAndWait();
	}
}
