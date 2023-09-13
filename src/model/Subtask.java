package model;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, LocalDateTime startTime, int epicId) {
        super(name, description, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status,
                   LocalDateTime startTime, Integer duration, Integer epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime.format(FORMATTER) +
                ", duration=" + duration +
                ", status=" + status +
                '}';
    }

}
