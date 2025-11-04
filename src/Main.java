import java.io.*;
import java.util.*;

class Task {
    String description;
    boolean done;

    Task(String description) {
        this.description = description;
        this.done = false;
    }

    @Override
    public String toString() {
        return (done ? "[x] " : "[ ] ") + description;
    }
}

public class Main {
    private static final String FILE = "tasks.txt";
    private static List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        loadTasks();
        Scanner sc = new Scanner(System.in);

try {
    while (true) {
        System.out.print("> ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.startsWith("q")) break;

        try {
            String[] parts = input.split("\\s+", 2); // Split into command + argument
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "add":
                case "mk":
                    if (!arg.isEmpty()) addTask(arg);
                    else System.out.println("Please provide a task description.");
                    break;

                case "remove":
                case "rm":
                    if (!arg.isEmpty()) {
                        try {
                            removeTask(Integer.parseInt(arg));
                        } catch (NumberFormatException e) {
                            System.out.println("Please provide a valid task number.");
                        }
                    } else {
                        System.out.println("Please provide a task number.");
                    }
                    break;

                case "done":
                case "d":
                    if (!arg.isEmpty()) {
                        try {
                            markDone(Integer.parseInt(arg));
                        } catch (NumberFormatException e) {
                            System.out.println("Please provide a valid task number.");
                        }
                    } else {
                        System.out.println("Please provide a task number.");
                    }
                    break;

                case "list":
                case "ls":
                    listTasks();
                    break;

                case "wipe":
                case "w":
                    tasks.clear();
                    System.out.println("All tasks have been removed!");
                break;


                default:
                    System.out.println("Commands: make (or mk), remove (or rm), done (or d), list (or ls), wipe (or w), exit (or q)");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} catch (Exception e) {
    System.out.println("Unexpected error: " + e.getMessage());
} finally {
    saveTasks();  // Always save tasks, even if an exception occurs
    sc.close();
}

    }


    private static void addTask(String desc) {
        tasks.add(new Task(desc));
        System.out.println("Added: " + desc);
    }

    private static void removeTask(int index) {
        if (index > 0 && index <= tasks.size()) {
            System.out.println("Removed: " + tasks.remove(index - 1).description);
        } else {
            System.out.println("Invalid task number");
        }
    }

    private static void markDone(int index) {
        if (index > 0 && index <= tasks.size()) {
            tasks.get(index - 1).done = true;
            System.out.println("Marked done: " + tasks.get(index - 1).description);
        } else {
            System.out.println("Invalid task number");
        }
    }

    private static void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks!");
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
                Task t = new Task(line.substring(4));
                t.done = line.startsWith("[x]");
                tasks.add(t);
            }
        } catch (IOException e) {
            // File not found is fine
        }
    }

    private static void saveTasks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            for (Task t : tasks) {
                bw.write(t.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks");
        }
    }
}