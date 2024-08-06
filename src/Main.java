import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            TaskManager manager = new InMemoryTaskManager();
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске HTTP-сервера: " + e.getMessage());
        }
    }
}
