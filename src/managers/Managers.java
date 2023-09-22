package managers;

import java.io.File;

import static server.HttpTaskServer.PORT;

public final class Managers {

    public static TaskManager getDefault() {
        File file = new File("./resources/BackupFile.csv");
        String url = "http://localhost:" + PORT + "/";
        return new HttpTaskManager(file, url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}