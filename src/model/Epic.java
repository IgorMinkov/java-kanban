package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIdList;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIds) {
        this.subtaskIdList = subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIdList=" + subtaskIdList +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}
