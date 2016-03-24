/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package benchmark.controls.container;

import java.io.File;
import java.util.List;

/**
 * @author tom
 */
public class AntTask {

	public AntTask(List<String> taskList, File runLocation) {
		_taskList = taskList;
		_runLocation = runLocation;
	}

	public List<String> getTaskList() {
		return _taskList;
	}

	public File getRunLocation() {
		return _runLocation;
	}

	private List<String> _taskList;
	private File _runLocation;
}
