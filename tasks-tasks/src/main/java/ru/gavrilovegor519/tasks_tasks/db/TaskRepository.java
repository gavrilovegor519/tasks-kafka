package ru.gavrilovegor519.tasks_tasks.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, Long> {
    Page<Task> findAllByAuthorEmail(String authorEmail, Pageable pageable);
}
