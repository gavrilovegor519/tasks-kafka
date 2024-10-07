package ru.gavrilovegor519.tasks_tasks.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import ru.gavrilovegor519.tasks_tasks.constants.TaskPriority;
import ru.gavrilovegor519.tasks_tasks.constants.TaskStatus;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Task {
    @Id
    private Long id;

    private String name;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private List<Long> commentIds;

    private String authorEmail;

    private String assignedEmail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
