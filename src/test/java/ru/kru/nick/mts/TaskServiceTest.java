package ru.kru.nick.mts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kru.nick.mts.dbe.Task;
import ru.kru.nick.mts.repository.TaskRepository;
import ru.kru.nick.mts.service.TaskService;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private ScheduledExecutorService executorService;

    @Test
    public void testGetTaskByGUID() {
        taskService.getTaskByGUID("test");
        verify(taskRepository).getById("test");
    }

    @Test
    public void testCreateNewTask() {
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        taskService.createNewTask();
        verify(taskRepository).save(taskCaptor.capture());
        Task task = taskCaptor.getValue();
        String guid = task.getId();
        Date createdTime = task.getTimestamp();
        assertEquals(Task.Status.CREATED, task.getStatus());
        verify(executorService).execute(runnableCaptor.capture());
        when(taskRepository.getById(guid)).thenReturn(task);

        runnableCaptor.getValue().run();
        verify(taskRepository, times(2)).save(taskCaptor.getValue());
        task = taskCaptor.getValue();
        assertEquals(guid, task.getId());
        assertTrue(task.getTimestamp().after(createdTime));
        assertEquals(Task.Status.RUNNING, task.getStatus());
        verify(executorService).schedule(runnableCaptor.capture(), anyLong(), any());
        when(taskRepository.getById(guid)).thenReturn(task);

        runnableCaptor.getValue().run();
        verify(taskRepository, times(3)).save(taskCaptor.getValue());
        task = taskCaptor.getValue();
        assertEquals(guid, task.getId());
        assertTrue(task.getTimestamp().after(createdTime));
        assertEquals(Task.Status.FINISHED, task.getStatus());
    }

    @TestConfiguration
    static class LogicTestConfig {
        @Bean
        public TaskService taskService() {
            return new TaskService();
        }
    }
}
