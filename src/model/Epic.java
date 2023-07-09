package model;

import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subtaskIDs;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIDs = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIDs;
    }

}
