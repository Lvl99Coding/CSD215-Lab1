package lab1;

import java.io.*;
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
    private File toDoFile = new File("C:\\Users\\Caleb\\Desktop\\toDoList.txt");

    public App() {
        this(System.in, System.out);
    }


    public App(InputStream in, PrintStream out) {
        this.input = new Scanner(in);
        this.out = out;
    }

    public void run() {
        boolean done = false;
        getAllFileTasks();
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
                        writeToFile(newTask);
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
                        taskCompleted.setCompleted(true);
                        completeTaskInFile(taskCompleted);
                        break;

                    case 3:
                        System.out.println("Remove Completed Tasks:\n");
                        for (Task task : tasks) {
                            if (task.getCompleted()) {
                                tasks.remove(task);
                            }

                        }
                        deleteCompletedFromFile();
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

    private void writeToFile(Task task){
        try(PrintWriter toDoWriter = new PrintWriter(toDoFile)){
            String status;
            if (task.getCompleted()){
                status = "Complete";
            }else{
                status = "Not Complete";
            }
            toDoWriter.append("Task:" + task.getTaskTitle() + ",\n" +
                    " Description:" + task.getTaskDescription() + ",\n" +
                    " Status:" + status);
            toDoWriter.append("");
            toDoWriter.append("");
        } catch (Exception e){
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void getAllFileTasks() {
        try (Scanner fileReader = new Scanner(toDoFile)) {
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Task fileTask = new Task();
                    fileTask.setTaskTitle(parts[0]);
                    fileTask.setTaskDescription(parts[1]);

                    if (parts[2].equals("true")) {
                        fileTask.setCompleted(true);
                    } else {
                        fileTask.setCompleted(false);
                    }
                    tasks.add(fileTask);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }

        public void completeTaskInFile(Task task) {
            if (toDoFile.exists()) {
                try (Scanner fileReader = new Scanner(toDoFile)) {
                    int count = 0;
                    int toCompleteLine = 0;
                    while (fileReader.hasNextLine()) {
                        count++;
                        String line = fileReader.nextLine();
                        String[] parts = line.split(",");
                        if (parts[count] == task.getTaskTitle()) {
                            toCompleteLine = count + 2; // Assuming status is two lines after title
                        }
                        if (count == toCompleteLine) {
                            parts[count] = " Status:Complete";
                            break;
                        }

                    }
                }catch (Exception e){
                    System.out.println("Error updating file: " + e.getMessage());
                }
            }
        }

        public void deleteCompletedFromFile() {
            if (toDoFile.exists()) {
                try (Scanner fileReader = new Scanner(toDoFile)) {
                    int count = 0;
                    while (fileReader.hasNextLine()) {
                        count++;
                        String line = fileReader.nextLine();
                        String[] parts = line.split(",");

                        if (parts[count].contains("Status:Complete")) {
                            parts[count] = "";
                            parts[count-1] = "";
                            parts[count-2] = "";
                        }

                    }
                }catch (Exception e){
                    System.out.println("Error updating file: " + e.getMessage());
                }
            }
        }

}
