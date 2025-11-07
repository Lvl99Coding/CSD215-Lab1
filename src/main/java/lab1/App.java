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
        getAllFileTasks(toDoFile, tasks);
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
                        writeToFile(newTask, toDoFile);
                        System.out.println("Task Added!\n");
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
                        completeTaskInFile(taskCompleted, toDoFile);
                        System.out.println("Task Completed! Good Job!\n");
                        break;

                    case 3:
                        System.out.println("Remove Completed Tasks:\n");
                        tasks.removeIf(Task::getCompleted);
                        deleteCompletedFromFile(toDoFile);
                        System.out.println("Completed Tasks Removed!\n");
                        break;
                    case 4:
                        System.out.println("List of Tasks:");
                        count = 0;
                        for (Task task : tasks) {
                            count++;
                            System.out.println(count + "." + task.getTaskTitle() + " - " + (task.getCompleted() ? "Completed" : "Not Completed") + "\n   Description: " + task.getTaskDescription());
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

    public void writeToFile(Task task, File file){
        try(PrintWriter toDoWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            String status;
            if (task.getCompleted()){
                status = "Complete";
            }else{
                status = "Not Complete";
            }
            toDoWriter.append("Task:"+ task.getTaskTitle() + ",\n" +
                    " Description:" + task.getTaskDescription() + ",\n" +
                    " Status:" + status);
            toDoWriter.append("\n");
            toDoWriter.append("\n");
        } catch (Exception e){
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void getAllFileTasks(File file, ArrayList taskList) {
        try (Scanner fileReader = new Scanner(file)) {
            int count = 0;
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String[] parts = line.split(",");
                if (parts[count].contains("Task:")) {
                    Task fileTask = new Task();
                    fileTask.setTaskTitle(parts[count]);
                    taskList.add(fileTask);
                }

                if (parts[count].contains("Description:")) {
                    Task fileTask =(Task) taskList.get(taskList.size() - 1);
                    fileTask.setTaskDescription(parts[count]);
                }

                if (parts[count].contains("Status:")) {
                    Task fileTask =(Task) taskList.get(taskList.size() - 1);
                    if (!parts[count].contains("Not")) {
                        fileTask.setCompleted(true);
                    } else {
                        fileTask.setCompleted(false);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }

    public void completeTaskInFile(Task task, File file) {
            if (file.exists()) {
                try (Scanner fileReader = new Scanner(file)) {
                    List<String> fileLines = new ArrayList<>();
                    while (fileReader.hasNextLine()) {
                        String line = fileReader.nextLine();
                        fileLines.add(line);
                    }

                    for (int i = 0; i < fileLines.size(); i++) {
                        if (fileLines.get(i).contains(task.getTaskTitle())) {
                            if (i + 2 < fileLines.size()) {
                                fileLines.set(i + 2, " Status:Complete");
                            }
                            break;
                        }
                    }

                    try (PrintWriter toDoWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
                        for (String fileLine : fileLines) {
                            toDoWriter.println(fileLine);
                        }
                    } catch (Exception e) {
                        System.out.println("Error writing to file: " + e.getMessage());
                    }

                } catch (Exception e) {
                    System.out.println("Error reading from file: " + e.getMessage());
                }
            }
        }

        public void deleteCompletedFromFile(File file) {
            if (file.exists()) {
                try (Scanner fileReader = new Scanner(file)) {
                    List<String> fileLines = new ArrayList<>();
                    while (fileReader.hasNextLine()) {
                        String line = fileReader.nextLine();
                        fileLines.add(line);
                    }

                    for (int c = 0; c < fileLines.size(); c++) {
                        if (fileLines.get(c).contains("Status:Complete")) {
                            fileLines.remove(c);
                            fileLines.remove(c-1);
                            fileLines.remove(c-2);
                        }
                    }

                    try (PrintWriter toDoWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
                        for (String fileLine : fileLines) {
                            toDoWriter.println(fileLine);
                        }
                    } catch (Exception e) {
                        System.out.println("Error writing to file: " + e.getMessage());
                    }

                } catch (Exception e) {
                    System.out.println("Error reading from file: " + e.getMessage());
                }
            }
        }

}
