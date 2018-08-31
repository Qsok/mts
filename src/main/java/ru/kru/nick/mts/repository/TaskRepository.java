package ru.kru.nick.mts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kru.nick.mts.dbe.Task;

@Repository
public interface TaskRepository extends CrudRepository<Task, String> {
    Task save(Task task);

    Task getById(String id);
}
