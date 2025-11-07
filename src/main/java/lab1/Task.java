package lab1;

import java.util.Scanner;

public class Task {

    private String taskTitle;
    private String taskDescription;
    private Boolean completed;
    private Scanner input;

    public Task() {
        this.completed = false;
    }

    public Task(String taskTitle, String taskDescription, Boolean completed) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.completed = completed;
    }

    public void initializeTask() {
        System.out.println("Adding Task");
        input = new Scanner(System.in);

        System.out.println("Enter Task Title: ");
        setTaskTitle(input.nextLine());
        if (getTaskTitle().isEmpty()) {
            setTaskTitle("No Title");
        }

        System.out.println("Enter Task Description: ");
        setTaskDescription(input.nextLine());
        if (getTaskDescription().isEmpty()) {
            setTaskDescription("No Description");
        }

        this.completed = false;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
