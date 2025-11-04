import java.io.*;
import java.util.*;
import java.net.URISyntaxException;

public class Main {

    // =========================
    // Configuration storage
    // =========================
    private static Properties config = new Properties();
    private static Map<String, List<String>> commands = new HashMap<>();
    private static String COLOR_CURSOR, COLOR_BOX_PENDING, COLOR_BOX_DONE, COLOR_X_DONE;
    private static String COLOR_SUCCESS, COLOR_ERROR, COLOR_WARNING, COLOR_RESET;
    private static String MSG_ADDED, MSG_REMOVED, MSG_DONE, MSG_INVALID_TASK_NUMBER;
    private static String MSG_NO_TASKS, MSG_ALL_REMOVED, MSG_MISSING_TASK_DESC, MSG_MISSING_TASK_NUM;

    private static final String FILE = "tasks.txt";
    private static List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        loadConfig();   // load config properties
        loadTasks();    // load tasks

        Scanner sc = new Scanner(System.in);

        try {
            while (true) {
                System.out.print(COLOR_CURSOR + "> " + COLOR_RESET);
                String input = sc.nextLine().trim();

                if (isCommand(input, "exit")) break;

                try {
                    String[] parts = input.split("\\s+", 2);
                    String cmd = parts[0].toLowerCase();
                    String arg = parts.length > 1 ? parts[1] : "";

                    if (isCommand(cmd, "add")) {
                        if (!arg.isEmpty()) addTask(arg);
                        else System.out.println(COLOR_ERROR + MSG_MISSING_TASK_DESC + COLOR_RESET);

                    } else if (isCommand(cmd, "remove")) {
                        if (!arg.isEmpty()) {
                            try {
                                removeTask(Integer.parseInt(arg));
                            } catch (NumberFormatException e) {
                                System.out.println(COLOR_ERROR + MSG_INVALID_TASK_NUMBER + COLOR_RESET);
                            }
                        } else System.out.println(COLOR_ERROR + MSG_MISSING_TASK_NUM + COLOR_RESET);

                    } else if (isCommand(cmd, "done")) {
                        if (!arg.isEmpty()) {
                            try {
                                markDone(Integer.parseInt(arg));
                            } catch (NumberFormatException e) {
                                System.out.println(COLOR_ERROR + MSG_INVALID_TASK_NUMBER + COLOR_RESET);
                            }
                        } else System.out.println(COLOR_ERROR + MSG_MISSING_TASK_NUM + COLOR_RESET);

                    } else if (isCommand(cmd, "list")) {
                        listTasks();

                    } else if (isCommand(cmd, "wipe")) {
                        tasks.clear();
                        System.out.println(COLOR_WARNING + MSG_ALL_REMOVED + COLOR_RESET);
                    } else if (isCommand(cmd, "clear")) {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                    } else {
                        System.out.println("Commands: add, remove, done, list, wipe, clear, exit");
                    }

                } catch (Exception e) {
                    System.out.println(COLOR_ERROR + "Error: " + e.getMessage() + COLOR_RESET);
                }
            }
        } catch (Exception e) {
            System.out.println(COLOR_ERROR + "Unexpected error: " + e.getMessage() + COLOR_RESET);
        } finally {
            saveTasks();
            sc.close();
        }
    }

    // =========================
    // Check if input matches command aliases
    // =========================
    private static boolean isCommand(String input, String commandKey) {
        List<String> aliases = commands.get(commandKey);
        return aliases != null && aliases.contains(input.toLowerCase());
    }

    // =========================
    // Load configuration
    // =========================
private static void loadConfig() {
    try {
        // Get the folder where the Main.class file is located
        String configPath = new File(Main.class
                                .getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .toURI())
                                .getParent() + "/config.properties";

        // Load properties from that path
        config.load(new FileReader(configPath));

        // --- rest of your existing code loading commands, colors, messages ---
        commands.put("add", Arrays.asList(config.getProperty("cmd_add", "add").split(",")));
        commands.put("remove", Arrays.asList(config.getProperty("cmd_remove", "remove").split(",")));
        commands.put("done", Arrays.asList(config.getProperty("cmd_done", "done").split(",")));
        commands.put("list", Arrays.asList(config.getProperty("cmd_list", "list").split(",")));
        commands.put("wipe", Arrays.asList(config.getProperty("cmd_wipe", "wipe").split(",")));
        commands.put("exit", Arrays.asList(config.getProperty("cmd_exit", "exit").split(",")));
        commands.put("clear", Arrays.asList(config.getProperty("cmd_clear", "clear").split(",")));

        // Load colors
        COLOR_CURSOR = config.getProperty("color_cursor", "\u001B[95m");
        COLOR_BOX_PENDING = config.getProperty("color_box_pending", "\u001B[94m");
        COLOR_BOX_DONE = config.getProperty("color_box_done", "\u001B[92m");
        COLOR_X_DONE = config.getProperty("color_x_done", "\u001B[95m");
        COLOR_SUCCESS = config.getProperty("color_success", "\u001B[32m");
        COLOR_ERROR = config.getProperty("color_error", "\u001B[31m");
        COLOR_WARNING = config.getProperty("color_warning", "\u001B[33m");
        COLOR_RESET = config.getProperty("color_reset", "\u001B[0m");

        // Load messages
        MSG_ADDED = config.getProperty("msg_added", "Added: %s");
        MSG_REMOVED = config.getProperty("msg_removed", "Removed: %s");
        MSG_DONE = config.getProperty("msg_done", "Marked done: %s");
        MSG_INVALID_TASK_NUMBER = config.getProperty("msg_invalid_task_number", "Invalid task number");
        MSG_NO_TASKS = config.getProperty("msg_no_tasks", "No tasks!");
        MSG_ALL_REMOVED = config.getProperty("msg_all_removed", "All tasks have been removed!");
        MSG_MISSING_TASK_DESC = config.getProperty("msg_missing_task_description", "Please provide a task description.");
        MSG_MISSING_TASK_NUM = config.getProperty("msg_missing_task_number", "Please provide a task number.");

    } catch (IOException | URISyntaxException e) {
        System.out.println("Could not load config.properties, using defaults.");
    }
}


    // =========================
    // Task class with colored boxes
    // =========================
    static class Task {
        String description;
        boolean done;

        Task(String description) {
            this.description = description;
            this.done = false;
        }

        @Override
        public String toString() {
            if (done) {
                return COLOR_BOX_DONE + "[" + COLOR_X_DONE + "x" + COLOR_BOX_DONE + "] " + COLOR_RESET + description;
            } else {
                return COLOR_BOX_PENDING + "[ ] " + COLOR_RESET + description;
            }
        }
    }

    // =========================
    // Task operations
    // =========================
    private static void addTask(String desc) {
        tasks.add(new Task(desc));
        System.out.println(String.format(COLOR_SUCCESS + MSG_ADDED + COLOR_RESET, desc));
    }

    private static void removeTask(int index) {
        if (index > 0 && index <= tasks.size()) {
            System.out.println(String.format(COLOR_SUCCESS + MSG_REMOVED + COLOR_RESET, tasks.remove(index - 1).description));
        } else {
            System.out.println(COLOR_ERROR + MSG_INVALID_TASK_NUMBER + COLOR_RESET);
        }
    }

    private static void markDone(int index) {
        if (index > 0 && index <= tasks.size()) {
            tasks.get(index - 1).done = true;
            System.out.println(String.format(COLOR_SUCCESS + MSG_DONE + COLOR_RESET, tasks.get(index - 1).description));
        } else {
            System.out.println(COLOR_ERROR + MSG_INVALID_TASK_NUMBER + COLOR_RESET);
        }
    }

    private static void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println(COLOR_WARNING + MSG_NO_TASKS + COLOR_RESET);
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    private static void loadTasks() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() >= 4) {
                    boolean done = line.charAt(1) == 'x' || line.charAt(1) == 'X';
                    String desc = line.substring(4);
                    Task t = new Task(desc);
                    t.done = done;
                    tasks.add(t);
                }
            }
        } catch (IOException ignored) {}
    }

    private static void saveTasks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            for (Task t : tasks) {
                bw.write((t.done ? "[x] " : "[ ] ") + t.description);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(COLOR_ERROR + "Error saving tasks" + COLOR_RESET);
        }
    }
}
