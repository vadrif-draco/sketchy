package asu.foe.sketchy.scenes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.SketchyApplication;
import asu.foe.sketchy.persistence.User;
import asu.foe.sketchy.persistence.UserRepository;
import asu.foe.sketchy.services.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


@Lazy
@Component
public class GUILoginScene {



	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	public Parent getRoot() {

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setStyle("-fx-font-size: 14pt;");

		Label titleLabel = new Label("Login");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		grid.add(titleLabel, 0, 0, 2, 1);

		Label emailLabel = new Label("Email:");
		TextField emailTextField = new TextField();
		emailTextField.setPromptText("Enter your email");
		grid.add(emailLabel, 0, 1);
		grid.add(emailTextField, 1, 1);

		Label passwordLabel = new Label("Password:");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter your password");
		grid.add(passwordLabel, 0, 2);
		grid.add(passwordField, 1, 2);

		// Create a "Login" button and set its action
		Button loginButton = new Button("Login");
		loginButton.setOnAction(event -> {
			// Validate that all fields are filled in
			if (emailTextField.getText().isEmpty() || passwordField.getText().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields.");
				alert.showAndWait();
				return;
			}

			// Check if the email and password match an existing user in the database
			User user = userRepository.findByEmailAndPassword(emailTextField.getText(), passwordField.getText());
			if (user == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect email or password.");
				alert.showAndWait();
				return;
			}

			// Set the current user to this user
			SketchyApplication.currentUser = user;

			// Show a success message or redirect to another page.
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged in successfully!");
			alert.showAndWait();

			// Redirect to the GUISketchList
			//mainStage.scene.setRoot(sketchListScene.getRoot());
			authService.login();
			
		});

		// Create an HBox to hold the "Login"
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(loginButton);
		grid.add(buttonBox, 1, 3);
		buttonBox.setPadding(new Insets(16, 16, 16, 16));

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(grid);

		return root;
	}
}
