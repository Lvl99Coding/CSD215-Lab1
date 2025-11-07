package lab1;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppTest extends App{

    @Test
    public void writeToFile() throws IOException {
        Task task1 = new Task("Test-task-1", "This is a test task", false);
        Task task2 = new Task("Test-task-2", "This is a test task", true);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();
        FileReader fr = new FileReader(tempFile);

        try {
            writeToFile(task1, tempFile);
            writeToFile(task2, tempFile);

            List allLines = fr.readAllLines();
            System.out.println("All lines: " + allLines);
            assertTrue(allLines.toString().contains("Test-task-1"));
            assertTrue(allLines.toString().contains("Test-task-2"));


        }
        catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred during writeToFile test");
        }
        fr.close();


    }

    @Test
    void completeTaskInFile() throws IOException {
        Task task1 = new Task("Test Task 1", "This is a test task", false);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();
        FileReader fr = new FileReader(tempFile);

        try {
            writeToFile(task1, tempFile);

            completeTaskInFile(task1, tempFile);

            List allLines = fr.readAllLines();

            assertTrue(!allLines.toString().contains("Not Complete"));
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred during completeTaskInFile test");
        }
        fr.close();
    }

    @Test
    public void deleteCompletedFromFile() {
        Task task1 = new Task("Test Task 1", "This is a test task", true);
        Task task2 = new Task("Test Task 2", "This is another test task", false);

        try {
            File tempFile = File.createTempFile("testFile", ".txt");
            tempFile.deleteOnExit();
            FileReader fr = new FileReader(tempFile);

            writeToFile(task1, tempFile);
            writeToFile(task2, tempFile);

            deleteCompletedFromFile(tempFile);

            List allLines = fr.readAllLines();

            assertTrue(!allLines.toString().contains("Test Task 1"));
            assertTrue(allLines.toString().contains("Test Task 2"));

            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred during deleteCompletedFromFile test");
        }
    }

    @Test
    public void getAllFileTasks() {

        Task task1 = new Task("Test Task 1", "This is a test task", true);
        Task task2 = new Task("Test Task 2", "This is another test task", false);

        try {
            File tempFile = File.createTempFile("testFile", ".txt");
            tempFile.deleteOnExit();
            ArrayList <Task> tasksFromFile = new ArrayList<>();

            writeToFile(task1, tempFile);
            writeToFile(task2, tempFile);

            getAllFileTasks(tempFile, tasksFromFile);

            assertEquals(2, tasksFromFile.size());



        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred during getAllFileTasks test");
        }

    }
}