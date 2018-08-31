package ru.kru.nick.mts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.kru.nick.mts.dbe.Task;
import ru.kru.nick.mts.service.TaskService;

import java.util.regex.Pattern;

@Controller
@RequestMapping("/task")
public class TaskController {
    private static final Pattern GUID_PATTERN =
            Pattern.compile("/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/");
    @Autowired
    private TaskService businessTaskService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createTask() {
        String taskId = businessTaskService.createNewTask();
        return new ResponseEntity(taskId, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getGuid(@PathVariable("id") String id) {
        if (GUID_PATTERN.matcher(id).matches()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Task task = businessTaskService.getTaskByGUID(id);
        return task == null ? new ResponseEntity(HttpStatus.NOT_FOUND) :
                new ResponseEntity(task, HttpStatus.OK);
    }
}
