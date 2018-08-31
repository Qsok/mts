package ru.kru.nick.mts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kru.nick.mts.dbe.Task;
import ru.kru.nick.mts.repository.TaskRepository;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {
    @Autowired
    private ScheduledExecutorService executor;

    @Autowired
    private TaskRepository taskRepository;


    public String createNewTask() {
        Task createdTask = new Task();
        String taskGUID = UUID.randomUUID().toString();
        createdTask.setId(taskGUID);
        updateTaskStatus(createdTask, Task.Status.CREATED);
        //для обновления состояния берем актуальную таску
        executor.execute(() -> {
            updateTaskStatus(taskRepository.getById(taskGUID), Task.Status.RUNNING);
            executor.schedule(() -> updateTaskStatus(taskRepository.getById(taskGUID), Task.Status.FINISHED),
                    2, TimeUnit.MINUTES);
        });
        return createdTask.getId();
    }

    public Task getTaskByGUID(String id) {
        return taskRepository.getById(id);
    }

    private void updateTaskStatus(Task task, Task.Status status) {
        task.setStatus(status);
        task.setTimestamp(new Date());
        taskRepository.save(task);
    }

}
