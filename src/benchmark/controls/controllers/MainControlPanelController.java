/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package benchmark.controls.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import benchmark.controls.BenchmarkControls;
import benchmark.controls.container.AntTask;
import benchmark.controls.util.PropertiesUtil;
import benchmark.controls.util.StringUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * FXML Controller class
 *
 * @author tom
 */
public class MainControlPanelController implements Initializable {

	@FXML
    private Button addToQueueBtn;

	@FXML
	private Button changeScriptBtn;

	@FXML
	private Button deleteTaskBtn;

	@FXML
	private Button displayQueueBtn;

	@FXML
    private Button runBtn;

	@FXML
    private CheckBox skipBuildPortalCheck;

	@FXML
    private ChoiceBox<String> scriptList;

	@FXML
    private Label taskLabel;

	@FXML
    private RadioButton actualRadio;

	@FXML
    private RadioButton bothRadio;

	@FXML
    private RadioButton warmupRadio;

	@FXML
    private ToggleGroup runGroup;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		_tasks = new ArrayList<>();

		_scriptMap = new HashMap<>();

		_scriptMap.put("Asset", "asset");
		_scriptMap.put("Blog", "blog");
		_scriptMap.put("Content", "content");
		_scriptMap.put("DocumentLibrary", "dl");
		_scriptMap.put("Login", "login");
		_scriptMap.put("Messageboard", "mb");
		_scriptMap.put("Wiki", "wiki");
	}

	@FXML
	private void addToQueueBtnAction(ActionEvent event) throws IOException {
		Properties properties = PropertiesUtil.loadProperties(
			Paths.get("build.properties"));

		Path benchmarkPath = Paths.get(
			properties.getProperty("benchmark.dir"));

		File benchmarkPathFile = benchmarkPath.toFile();

		if (!warmupRadio.isSelected() && !actualRadio.isSelected() &&
			!bothRadio.isSelected()) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("Option not selected");
			alert.setContentText("Please choose the run type");

			alert.showAndWait();

			return;
		}
		if (warmupRadio.isSelected()) {
			_addTask(
				skipBuildPortalCheck.isSelected(), benchmarkPathFile, WARMUP);
		}
		else if (actualRadio.isSelected()) {
			_addTask(
				skipBuildPortalCheck.isSelected(), benchmarkPathFile, ACTUAL);
		}
		else {
			_addTask(
				skipBuildPortalCheck.isSelected(), benchmarkPathFile, WARMUP);

			_addTask(true, benchmarkPathFile, ACTUAL);
		}

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Task Added");
		alert.setHeaderText(null);
		alert.setContentText("Task has been added to the queue");

		alert.showAndWait();
	}

	@FXML
	private void changeScriptBtnAction(ActionEvent event) throws IOException {
		if (scriptList.getValue() != null) {
			_addScriptChangeTask(scriptList.getValue());

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Task Added");
			alert.setHeaderText(null);
			alert.setContentText("Script change added");

			alert.showAndWait();
		}
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("No Script Specified");
			alert.setContentText("Please choose a script first");

			alert.showAndWait();
		}
	}

	@FXML
	private void deleteTaskBtnAction(ActionEvent event) {
		final Stage dialog = new Stage();

		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(_primaryStage);

		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setTitle("Delete Task");
		inputDialog.setHeaderText("Delete Task");
		inputDialog.setContentText("Please enter task number:");

		Optional<String> result = inputDialog.showAndWait();
		if (result.isPresent()){
			int deleteTaskNum = Integer.parseInt(result.get());
			_tasks.remove(deleteTaskNum -1);
		}

		dialog.close();

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Task Deleted");
		alert.setHeaderText(null);
		alert.setContentText("Task has been deleted from queue");

		alert.showAndWait();
	}

	@FXML
	private void displayQueueBtnAction(ActionEvent event) {
		String taskList = _getTaskList();

		final Stage dialog = new Stage();

		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(_primaryStage);

		VBox dialogVbox = new VBox(20);

		dialogVbox.getChildren().add(new Text(taskList));

		Scene dialogScene = new Scene(dialogVbox, 1000, 400);

		dialog.setScene(dialogScene);
		dialog.show();
	}

	@FXML
	private void runBtnAction(ActionEvent event) throws IOException {
		for (int i = 0; i < _tasks.size(); i++) {
			AntTask antTask = _tasks.get(i);

			ProcessBuilder processBuilder = new ProcessBuilder(
				antTask.getTaskList());

			processBuilder.directory(antTask.getRunLocation());

			Process process = processBuilder.start();

			String line;

			try(BufferedReader br = new BufferedReader(new InputStreamReader(
				process.getInputStream()))) {

				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			}

			try(BufferedReader br = new BufferedReader(new InputStreamReader(
				process.getErrorStream()))) {

				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			}
		}

		_tasks.clear();

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Tasks Completed");
		alert.setHeaderText(null);
		alert.setContentText("Task Completed");

		alert.showAndWait();
	}

	public void showStage() throws IOException {
		_primaryStage = BenchmarkControls.getPrimaryStage();

		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(
			BenchmarkControls.class.getResource("views/MainControlPanel.fxml"));

		Pane rootLayout = (Pane) loader.load();

		Scene scene = new Scene(rootLayout);

		_primaryStage.setTitle("Main Control Panel");
		_primaryStage.setScene(scene);
		_primaryStage.show();
	}

	private void _addScriptChangeTask(String script) {
		Path pwd = Paths.get(System.getProperty("user.dir"));

		_tasks.add(
			new AntTask(
				_createCopyPropertiesTask(script),
				pwd.toFile()));
	}

	private void _addTask(
			Boolean buildPortal, File benchmarkPathFile, String runType)
		throws IOException {

		_tasks.add(
			new AntTask(_createTask(runType, buildPortal), benchmarkPathFile));
	}

	private List<String> _createCopyPropertiesTask(String testCase) {
		List<String> task = new ArrayList<>();

		task.add("ant");
		task.add("switch-to-script");
		task.add("-Dtest-case=" + _scriptMap.get(testCase));

		return task;
	}

	private List<String> _createTask(String taskType, boolean skipBuildPortal)
		throws IOException {

		List<String> task = new ArrayList<>();

		task.add("ant");

		String[] tasks = StringUtil.split(taskType, ' ');

		task.addAll(Arrays.asList(tasks));

		if (skipBuildPortal) {
			task.add("-Dskip.build.portal=true");
		}

		return task;
	}

	private String _getTaskList() {
		StringBuilder sb = new StringBuilder();

		if (_tasks != null) {
			for (int i = 0; i < _tasks.size(); i++) {
				AntTask antTask = _tasks.get(i);

				sb.append(i + 1);
				sb.append(": ");

				List<String> task = antTask.getTaskList();

				for (String test : task) {
					sb.append(test);
					sb.append(' ');
				}
				sb.append('\n');
			}
		}

		return sb.toString();
	}

	@FXML
	private void updateWindowAction() {
		taskLabel.setText("Tasks: \n" + _getTaskList());
	}

	private List<AntTask> _tasks;
	private static Map<String, String> _scriptMap;
	private static Stage _primaryStage;
	private static final String WARMUP = "all-database";
	private static final String ACTUAL = "stop reload-warmup-database "
		+ "all-portal start-visualvm all-grinder all-sample stop";
}
