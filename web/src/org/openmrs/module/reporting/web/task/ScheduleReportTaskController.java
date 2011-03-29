package org.openmrs.module.reporting.web.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.propertyeditor.RenderingModeEditor;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.task.RunReportTask;
import org.openmrs.module.reporting.web.validator.RunReportTaskValidator;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ScheduleReportTaskController {

	@InitBinder
	public void initBinder(WebDataBinder wdb) {
		wdb.registerCustomEditor(Mapped.class, new MappedEditor());
		wdb.registerCustomEditor(RenderingMode.class, new RenderingModeEditor());
		wdb.registerCustomEditor(Date.class, "startTime",
			new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
	}
	
	@ModelAttribute("reportDefinitions")
	public List<DefinitionSummary> getAllReportDefinitions() {
		return Context.getService(ReportDefinitionService.class).getAllDefinitionSummaries(false);
	}
	
	@ModelAttribute("repeatIntervals")
	public Map<Long, String> getRepeatIntervals() {
		Map<Long, String> ret = new LinkedHashMap<Long, String>();
		ret.put(7 * 24 * 60 * 60l, "weekly");
		ret.put(24 * 60 * 60l, "daily");
		ret.put(60 * 60l, "hourly");
		return ret;
	}
	
	@RequestMapping("/module/reporting/task/listScheduledReportTasks")
	public void listScheduledReportTasks(Model model) {
		List<RunReportTask> tasks = new ArrayList<RunReportTask>();
		for (TaskDefinition taskDef : Context.getSchedulerService().getRegisteredTasks()) {
			if (taskDef.getTaskClass().equals(RunReportTask.class.getName())) {
				RunReportTask task = new RunReportTask();
				task.initialize(taskDef);
				tasks.add(task);
			}
		}
		model.addAttribute("tasks", tasks);
	}
	
	@RequestMapping(value="/module/reporting/task/editScheduledReportTask", method=RequestMethod.GET)
	public void showEditScheduledReportTask() {
	}
	
	@RequestMapping("/module/reporting/task/deleteScheduledReportTask")
	public String deleteScheduledReportTask(@RequestParam("id") Integer id) {
		Context.getSchedulerService().deleteTask(id);
		return "redirect:listScheduledReportTasks.list";
	}
	
	@ModelAttribute("task")
	RunReportTask getTask(@RequestParam(value="id", required=false) Integer taskId) {
		RunReportTask task = new RunReportTask();
		if (taskId != null) {
			task.initialize(Context.getSchedulerService().getTask(taskId));
		} else {
			TaskDefinition td = new TaskDefinition();
			td.setStartTime(null);
			task.initialize(td);
			task.setPriority(Priority.NORMAL);
		}
		return task;
	}
	
	@RequestMapping(value="/module/reporting/task/editScheduledReportTask", method=RequestMethod.POST)
	public String saveScheduledReportTask(@ModelAttribute("task") RunReportTask task,
	                                    Errors errors,
	                                    Model model) throws SchedulerException {
		
		new RunReportTaskValidator().validate(task, errors);
		if (errors.hasErrors()) {
			model.addAttribute("task", task);
			model.addAttribute("errors", errors);
			return null;
		}

		TaskDefinition taskDef = task.getTaskDefinition();
		taskDef.setStartOnStartup(true);
		taskDef.setStarted(true);
		Context.getSchedulerService().saveTask(taskDef);
		Context.getSchedulerService().scheduleTask(taskDef);
			
		Context.getService(ReportService.class).ensureScheduledTasksRunning();
		return "redirect:listScheduledReportTasks.list";
	}

}
