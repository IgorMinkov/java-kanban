package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Integer duration) {
        super(name, description, status, startTime, duration);
        this.subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (endTime == null) {
            return super.getEndTime();
        }
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", subtaskIdList=" + subtaskIdList +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime.format(formatter) +
                ", duration=" + duration +
                ", endTime=" + endTime.format(formatter) +
                ", status=" + status +
                '}';
    }

}
