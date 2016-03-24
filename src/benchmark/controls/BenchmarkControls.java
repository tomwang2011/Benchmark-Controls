/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package benchmark.controls;

import benchmark.controls.controllers.MainControlPanelController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author tom
 */
public class BenchmarkControls extends Application {

	@Override
	public void start(Stage primaryStage) {
		_primaryStage = primaryStage;
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				MainControlPanelController controller = new MainControlPanelController();

				try {
					controller.showStage();
				} catch (IOException ex) {
					Logger.getLogger(
						BenchmarkControls.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		StackPane root = new StackPane();
		root.getChildren().add(btn);

		Scene scene = new Scene(root, 1000, 800);

		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	public static Stage getPrimaryStage() {
		return _primaryStage;
	}

	private static Stage _primaryStage;
}
