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
import javafx.stage.Stage;

/**
 *
 * @author tom
 */
public class BenchmarkControls extends Application {

	@Override
	public void start(Stage primaryStage) {
		_primaryStage = primaryStage;

		MainControlPanelController controller =
			new MainControlPanelController();

		try {
			controller.showStage();
		}
		catch (IOException ex) {
			Logger.getLogger(
				BenchmarkControls.class.getName()).log(Level.SEVERE, null, ex);
		}
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
