package lab1;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class App {

    private final String menu = "\n***********************\n"
            + "      To-Do List      \n"
            + "***********************\n"
            + " 1. Add Task\n"
            + " 2. Complete Task\n"
            + " 3. Remove Completed Tasks\n"
            + " 4. List Tasks\n"
            + " 5. Quit\n"
            + "***********************\n"
            + "Enter choice: ";

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final PrintStream out;
    private Scanner input;


    public App() {
        this(System.in, System.out);
    }


    public App(InputStream in, PrintStream out) {
        this.input = new Scanner(in);
        this.out = out;
    }

    public void run() {
        boolean done = false;
        while (!done) {


            try {
                System.out.println(menu);
                int choice = input.nextInt();
                int count = 0;
                switch (choice) {
                    case 1:
                        System.out.println("Add Task:\n");
                        Task newTask = new Task();
                        newTask.initializeTask();
                        tasks.add(newTask);
                        break;
                    case 2:
                        System.out.println("Complete Task:\n");
                        ArrayList<Task> taskArrayList = new ArrayList<>();
                        for (Task task : tasks) {
                            if (task.getClass().equals(Task.class)) {
                                if (!task.getCompleted()) {
                                    count++;
                                    taskArrayList.add(task);
                                    System.out.println(count + ". " + task.getTaskTitle());
                                }
                            }
                        }

                        System.out.println("Which task is completed?:");
                        int sellBookChoice = input.nextInt();
                        Task taskCompleted = taskArrayList.get(sellBookChoice - 1);
                        taskCompleted.complete();
                        break;

                    case 3:
                        System.out.println("Remove Completed Tasks:\n");
                        for (Task task : tasks) {
                            if (task.getCompleted()) {
                                tasks.remove(task);
                            }
                        }
                        break;
                    case 4:
                        System.out.println("List of Tasks:");
                        count = 0;
                        for (Task task : tasks) {
                            count++;
                            System.out.println(count + ". " + task.getTaskTitle() + " - " + (task.getCompleted() ? "Completed" : "Not Completed"));
                        }
                        break;
                    case 5:
                        done = true;
                        System.out.println("***********************");
                        System.out.println("Quit");
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Wrong entry, try again...");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Wrong entry, try again...");
            } catch (Exception e) {
                System.out.println("Unknown Exception : " + e.getMessage());
            }
        }
    }
}
