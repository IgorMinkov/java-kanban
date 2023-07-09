package model;

public class Subtask extends Task {

    public int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

}
