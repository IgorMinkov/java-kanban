package managers;

import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class CSVFileOperator {

    private static final String DELIMITER = ",";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String toString(Task task) {
        if (task == null) {
            System.out.println("не найдена входящая задача в методе toString");
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(DELIMITER)
                .append(task.getType()).append(DELIMITER)
                .append(task.getName()).append(DELIMITER)
                .append(task.getStatus()).append(DELIMITER)
                .append(task.getDescription()).append(DELIMITER)
                .append((task.getStartTime()).format(formatter)).append(DELIMITER)
                .append(task.getDuration());
        if (task.getType() == TaskType.SUBTASK) {
            builder.append(DELIMITER).append(((Subtask) task).getEpicId());
        }
        return builder.toString();
    }

    public static Task fromString(String value) {
        if (value.isBlank()) {
            System.out.println("Передана пустая строка");
            return null;
        }
        String [] split = value.split(DELIMITER);
        Integer id = Integer.parseInt(split[0]);
        String type = split[1].toUpperCase();
        Status status = Status.valueOf((split[3].toUpperCase()));
        LocalDateTime startTime = LocalDateTime.parse(split[5], formatter);
        Integer duration = Integer.parseInt(split[6]);
        Task task;
        switch (TaskType.valueOf(type)) {
            case TASK:
                task = new Task(split[2], split[4], status, startTime, duration);
                task.setId(id);
                break;
            case EPIC:
                task = new Epic(split[2], split[4], status, startTime, duration);
                task.setId(id);
                break;
            case SUBTASK:
                Integer epicId = Integer.parseInt(split[7]);
                task = new Subtask(split[2], split[4], status, startTime, duration, epicId);
                task.setId(id);
                break;
            default:
                throw new IllegalArgumentException("No enum constant model.TaskType." + type);
        }
         return task;
    }

    public static String historyToString(HistoryManager manager) {
        List<String> result = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            result.add(String.valueOf(task.getId()));
        }
        return String.join(DELIMITER,result);
    }

    public static List<Integer> historyFromString(String value) {
        if (value.isBlank()) {
            System.out.println("Передана пустая строка");
            return new ArrayList<>();
        }
        List<Integer> history = new ArrayList<>();
        String [] split = value.split(DELIMITER);
        for (String id : split) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }

    static String setHeader() {
        return "id,type,name,status,description,startTime,duration,epicId" + "\n";
    }

}