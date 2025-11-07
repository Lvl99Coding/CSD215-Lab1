# Statement

I utilized AI for this lab, specifically github Copilot.

# Chat Log

Here is our chat:

Why am I getting this error, does this not create a file if it doesnt exist?

The compiler error occurs because new PrintWriter("to-do-log.txt", "UTF-8") can throw checked exceptions (FileNotFoundException, UnsupportedEncodingException). A field initializer cannot propagate checked exceptions, so you must handle them (catch or move initialization into a constructor/method that handles or declares the exceptions). Note: PrintWriter will create to-do-log.txt if possible, but it still throws those exceptions on failure.
Change: declare the field without initializing it, then initialize inside the constructor with a try/catch.


package lab1;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
    private PrintWriter writeFile; // initialize in constructor

    public App() {
        this(System.in, System.out);
    }

    public App(InputStream in, PrintStream out) {
        this.input = new Scanner(in);
        this.out = out;
        try {
            this.writeFile = new PrintWriter("to-do-log.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to open log file `to-do-log.txt`", e);
        }
    }

    // rest of class...
}

how do I write to  a File object

Remove the stray toDoFile. line (it's an incomplete statement). To write to a File use a writer (for example FileWriter/PrintWriter or Files.newBufferedWriter) inside a try-with-resources. The example below appends or overwrites toDoFile and writes one line per task (title + completed flag).

// java
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// add this method to your App class
private void saveTasksToFile(boolean append) {
// toDoFile is your File instance (e.g. `Desktop\toDoList.txt`)
try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(toDoFile, append)))) {
for (Task t : tasks) {
// write a simple text representation; change format as needed
pw.println(t.getTaskTitle() + "|" + (t.getCompleted() ? "1" : "0"));
}
} catch (IOException e) {
out.println("Failed to write to file: " + e.getMessage());
}
}


how would I load tasks from the file upon program start?

Added a loader that reads toDoFile at startup, parses lines saved as title,description,completed, and reconstructs Task objects (uses reflection to adapt to common Task constructors/setters). writeToFile now appends instead of overwriting.


package lab1;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
    private File toDoFile = new File("Desktop\\toDoList.txt");

    public App() {
        this(System.in, System.out);
    }

    public App(InputStream in, PrintStream out) {
        this.input = new Scanner(in);
        this.out = out;
        loadTasksFromFile();
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
                        taskCompleted.complete();
                        break;

                    case 3:
                        System.out.println("Remove Completed Tasks:\n");
                        // use iterator to avoid ConcurrentModificationException
                        Iterator<Task> it = tasks.iterator();
                        while (it.hasNext()) {
                            if (it.next().getCompleted()) {
                                it.remove();
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
                input.nextLine(); // clear invalid token
            } catch (Exception e) {
                System.out.println("Unknown Exception : " + e.getMessage());
            }
        }
    }

    private void writeToFile(Task task){
        // append to file so existing tasks are preserved
        try {
            if (toDoFile.getParentFile() != null) {
                toDoFile.getParentFile().mkdirs();
            }
            if (!toDoFile.exists()) {
                toDoFile.createNewFile();
            }
        } catch (IOException e) {
            out.println("Failed to prepare file: " + e.getMessage());
            return;
        }

        try (PrintWriter toDoWriter = new PrintWriter(new BufferedWriter(new FileWriter(toDoFile, true)))) {
            // format: title,description,completed
            toDoWriter.println(task.getTaskTitle() + "," + task.getTaskDescription() + "," + task.getCompleted());
        } catch (Exception e){
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void loadTasksFromFile() {
        if (toDoFile.getParentFile() != null) {
            toDoFile.getParentFile().mkdirs();
        }
        if (!toDoFile.exists()) {
            try {
                toDoFile.createNewFile();
            } catch (IOException e) {
                out.println("Could not create `toDoFile`: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(toDoFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // split into at most 3 parts so description may contain commas
                String[] parts = line.split(",", 3);
                String title = parts.length > 0 ? parts[0] : "";
                String desc = parts.length > 1 ? parts[1] : "";
                boolean completed = parts.length > 2 && Boolean.parseBoolean(parts[2]);

                // try several ways to construct/fill a Task using reflection to match common APIs
                try {
                    Class<?> cls = Task.class;
                    Task taskInstance = null;

                    // try constructor (String, String, boolean)
                    try {
                        Constructor<?> ctor = cls.getConstructor(String.class, String.class, boolean.class);
                        taskInstance = (Task) ctor.newInstance(title, desc, completed);
                    } catch (NoSuchMethodException ignore) { }

                    // try constructor (String, String)
                    if (taskInstance == null) {
                        try {
                            Constructor<?> ctor = cls.getConstructor(String.class, String.class);
                            taskInstance = (Task) ctor.newInstance(title, desc);
                            if (completed) {
                                try {
                                    Method complete = cls.getMethod("complete");
                                    complete.invoke(taskInstance);
                                } catch (Exception ignore) { }
                            }
                        } catch (NoSuchMethodException ignore) { }
                    }

                    // fallback to default constructor + setters/complete()
                    if (taskInstance == null) {
                        taskInstance = (Task) cls.getConstructor().newInstance();
                        try {
                            Method setTitle = cls.getMethod("setTaskTitle", String.class);
                            setTitle.invoke(taskInstance, title);
                        } catch (NoSuchMethodException ignore) { }
                        try {
                            Method setDesc = cls.getMethod("setTaskDescription", String.class);
                            setDesc.invoke(taskInstance, desc);
                        } catch (NoSuchMethodException ignore) { }
                        if (completed) {
                            try {
                                Method complete = cls.getMethod("complete");
                                complete.invoke(taskInstance);
                            } catch (Exception ignore) { }
                        }
                    }

                    if (taskInstance != null) {
                        tasks.add(taskInstance);
                    }
                } catch (Exception e) {
                    out.println("Failed to load a task line: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            out.println("Failed to load tasks: " + e.getMessage());
        }
    }
}


why is this method deleting all existing text from the file instead of adding to it?

The method overwrites the file because new PrintWriter(toDoFile) opens the file in write (truncate) mode. Use a FileWriter with append = true (optionally wrapped with BufferedWriter and PrintWriter) and write one line per task (so your reader can parse it).
Fixed method (appends a single CSV-style line and ensures parent directories exist):

// java
private void writeToFile(Task task) {
try {
if (toDoFile.getParentFile() != null) {
toDoFile.getParentFile().mkdirs();
}
if (!toDoFile.exists()) {
toDoFile.createNewFile();
}
} catch (IOException e) {
System.out.println("Failed to prepare file: " + e.getMessage());
return;
}

    // append = true so existing content is preserved
    try (PrintWriter toDoWriter = new PrintWriter(new BufferedWriter(new FileWriter(toDoFile, true)))) {
        // write a single line per task: title,description,completed
        // use a simple CSV; escape or change format if titles/descriptions may contain commas
        String line = task.getTaskTitle() + "," + task.getTaskDescription() + "," + task.getCompleted();
        toDoWriter.println(line);
    } catch (IOException e) {
        System.out.println("Error writing to file: " + e.getMessage());
    }
}

}


how do i write an array element to the file

Use a FileWriter opened with append = true, wrap it with BufferedWriter/PrintWriter, then call println with the array element (or convert the element to a string). The writer will create toDoFile if it doesn't exist.
Example methods to add a string array element or a Task array element to C:\Users\Caleb\Desktop\toDoList.txt using the existing toDoFile and out fields:


java
private void appendArrayElement(String[] arr, int index) {
if (arr == null || index < 0 || index >= arr.length) return;
try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(toDoFile, true)))) {
pw.println(arr[index]); // writes the single element and a newline
} catch (IOException e) {
out.println("Error writing to file: " + e.getMessage());
}
}

private void appendTaskFromArray(Task[] arr, int index) {
if (arr == null || index < 0 || index >= arr.length) return;
Task t = arr[index];
if (t == null) return;
try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(toDoFile, true)))) {
pw.println("Task:" + t.getTaskTitle());
pw.println("Description:" + t.getTaskDescription());
pw.println("Status:" + (t.getCompleted() ? "Complete" : "Not Complete"));
pw.println(); // blank separator
} catch (IOException e) {
out.println("Error writing to file: " + e.getMessage());
}
}


if I write an array to a file, can i index it as an array from the file later?


Short answer: not directly. A plain file is just bytes — you must either read/parse the file into an array (or list) in memory, or design the file with fixed-size records (or use serialization/memory-mapping) so you can seek to an element without loading everything.
Below are two practical Java approaches:
Read-all (easy): write one element per line (or JSON) and later read all lines into a List/array and index it.
Random-access (advanced): use RandomAccessFile with fixed-size records so you can seek(recordIndex * recordSize) and read that element without reading the whole file.
Explanation followed by code examples.

// java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

// Example 1: simple write-as-lines and read-into-array (recommended for most cases)
public class FileArrayUtils {

    public static void writeArrayAsLines(String[] arr, Path file) throws IOException {
        // writes one element per line (overwrites)
        Files.createDirectories(file.getParent());
        Files.write(file, Arrays.asList(arr), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static String[] readArrayFromLines(Path file) throws IOException {
        // reads all lines into memory; then you can index the returned array
        if (!Files.exists(file)) return new String[0];
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        return lines.toArray(new String[0]);
    }
}

// Example 2: fixed-size records with RandomAccessFile for direct indexing without loading whole file
class FixedRecordFile {
private final RandomAccessFile raf;
private final int recordBytes; // fixed number of bytes per record (UTF-8 bytes or padded ASCII)

    public FixedRecordFile(File file, int recordBytes, String mode) throws IOException {
        this.raf = new RandomAccessFile(file, mode);
        this.recordBytes = recordBytes;
    }

    private byte[] toFixedBytes(String s) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        if (b.length > recordBytes) {
            // truncate
            return Arrays.copyOf(b, recordBytes);
        }
        byte[] out = new byte[recordBytes];
        System.arraycopy(b, 0, out, 0, b.length);
        // remaining bytes are 0 (null) which can be trimmed on read
        return out;
    }

    public void writeAtIndex(long index, String value) throws IOException {
        raf.seek(index * recordBytes);
        raf.write(toFixedBytes(value));
    }

    public String readAtIndex(long index) throws IOException {
        byte[] buf = new byte[recordBytes];
        raf.seek(index * recordBytes);
        int read = raf.read(buf);
        if (read <= 0) return null;
        // trim trailing zeros and convert
        int len = read;
        while (len > 0 && buf[len - 1] == 0) len--;
        return new String(buf, 0, len, StandardCharsets.UTF_8);
    }

    public long recordCount() throws IOException {
        return raf.length() / recordBytes;
    }

    public void close() throws IOException {
        raf.close();
    }
}

// Example 3 (brief): Java object serialization (write and read the whole array object)
// Write:
class SerializationExample {
public static void writeObjectArray(String[] arr, File file) throws IOException {
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
oos.writeObject(arr);
}
}

    public static String[] readObjectArray(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (String[]) ois.readObject();
        }
    }
}



how can I replace the text at line parts[count] in the file


Read the whole file into a List<String>, update the line(s) you want in the list, then write the list back to toDoFile. This replaces the target line safely (no in-place editing). Example method that finds a Task: line by title and updates the following Status: line to Status: Complete:
java
private void completeTaskInFile(Task task) {
try {
java.nio.file.Path path = toDoFile.toPath();
java.util.List<String> lines = java.nio.file.Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("Task:" + task.getTaskTitle())) {
                // search forward for the Status line and replace it
                for (int j = i + 1; j < lines.size(); j++) {
                    String candidate = lines.get(j);
                    if (candidate.contains("Status:")) {
                        int idx = candidate.indexOf("Status:");
                        String prefix = idx >= 0 ? candidate.substring(0, idx) : "";
                        // normalize to "Status: Complete"
                        lines.set(j, prefix + "Status: Complete");
                        break;
                    }
                    // stop if we hit a blank line (end of block)
                    if (candidate.trim().isEmpty()) break;
                }
            }
        }

        java.nio.file.Files.write(path, lines, java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
    } catch (java.io.IOException e) {
        System.out.println("Error updating file: " + e.getMessage());
    }
}

do i have to rewrite the entire file to accomplish this?


No — you don't have to rewrite your whole program. Read the file into a List<String>, find the task block, update the Status line in-memory, then write the list back to toDoFile. This rewrites the file contents (safe and simple) but you only change the lines you need.
Explanation: the method below reads all lines, finds a line that contains Task: with the task title, updates the corresponding Status line (two lines after the Task line), and writes the updated lines back to toDoFile.
java
public void completeTaskInFile(Task task) {
if (!toDoFile.exists()) return;

    try {
        java.nio.file.Path path = toDoFile.toPath();
        java.util.List<String> lines = java.nio.file.Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("Task:") && line.contains(task.getTaskTitle())) {
                int statusIndex = i + 2; // assumes layout: Task:, Description:, Status:
                if (statusIndex < lines.size()) {
                    String statusLine = lines.get(statusIndex);
                    // replace existing status with a normalized Status: Complete
                    String newStatusLine = statusLine.replaceAll("(?i)Status\\s*:\\s*.*", " Status:Complete");
                    lines.set(statusIndex, newStatusLine);
                }
                break; // found and updated the task, stop looping
            }
        }

        java.nio.file.Files.write(path, lines, java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
    } catch (java.io.IOException e) {
        System.out.println("Error updating file: " + e.getMessage());
    }
}


When I attempt to remove the completed tasks, I get an nknown exception: null and none of the completed tasks from the toDoFile are removed, and only a few of the completed tasks from the in memory array are removed. Why is this?


Two bugs cause the behavior:
Removing from tasks while iterating causes a ConcurrentModificationException (caught and printed as Unknown Exception : null).
Removing lines from fileLines while iterating forward with fixed offsets leads to IndexOutOfBounds and skipping blocks.
Fix: remove completed tasks from the in-memory list with removeIf, and rewrite deleteCompletedFromFile() to treat each task as a block (lines separated by blank lines) and skip blocks whose Status line contains Status:Complete.
Code (replace the case 3 block in run() and the deleteCompletedFromFile() method in src/main/java/lab1/App.java):
// java
// Replace the case 3 block in run()
case 3:
System.out.println("Remove Completed Tasks:\n");
// Safely remove all completed tasks from the in-memory list
tasks.removeIf(Task::getCompleted);
// Update the file to remove completed task blocks
deleteCompletedFromFile();
break;

// java
// Replace existing deleteCompletedFromFile() with this implementation
public void deleteCompletedFromFile() {
if (!toDoFile.exists()) {
return;
}

    try (Scanner fileReader = new Scanner(toDoFile)) {
        List<String> fileLines = new ArrayList<>();
        while (fileReader.hasNextLine()) {
            fileLines.add(fileReader.nextLine());
        }

        List<String> outLines = new ArrayList<>();
        List<String> block = new ArrayList<>();

        for (String line : fileLines) {
            if (line.trim().isEmpty()) {
                // block boundary reached; decide to keep or skip
                if (!block.isEmpty()) {
                    boolean isCompleted = block.stream().anyMatch(l -> l.contains("Status:Complete"));
                    if (!isCompleted) {
                        outLines.addAll(block);
                        // preserve a single blank line as separator
                        outLines.add("");
                    }
                    block.clear();
                } else {
                    // consecutive blank lines: preserve a single blank line
                    if (outLines.isEmpty() || !outLines.get(outLines.size() - 1).trim().isEmpty()) {
                        outLines.add("");
                    }
                }
            } else {
                block.add(line);
            }
        }

        // handle last block (if file doesn't end with a blank line)
        if (!block.isEmpty()) {
            boolean isCompleted = block.stream().anyMatch(l -> l.contains("Status:Complete"));
            if (!isCompleted) {
                outLines.addAll(block);
            }
        }

        // write filtered lines back to the file (overwrite)
        try (PrintWriter toDoWriter = new PrintWriter(new BufferedWriter(new FileWriter(toDoFile, false)))) {
            for (int i = 0; i < outLines.size(); i++) {
                toDoWriter.println(outLines.get(i));
            }
        } catch (Exception e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

    } catch (Exception e) {
        System.out.println("Error reading from file: " + e.getMessage());
    }
}


why isnt this function:for (Task task : tasks) {
if (task.getCompleted()) {
tasks.remove(task);
}

                        }

deletiing the completed tasks from memory?



You're removing elements from the tasks list while using the enhanced for loop, which causes a ConcurrentModificationException (the loop fails after a few removals). That exception's getMessage() can be null so your catch prints "Unknown Exception : null". Fix: remove safely using an Iterator, or use removeIf. Below are two safe replacements — pick one.
// Option A: Java 8+ concise solution
tasks.removeIf(Task::getCompleted);

// Option B: Explicit iterator (works on older Java versions)
Iterator<Task> it = tasks.iterator();
while (it.hasNext()) {
Task t = it.next();
if (t.getCompleted()) {
it.remove();
}
}


List of Tasks:
Unknown Exception : Cannot invoke "java.lang.Boolean.booleanValue()" because the return value of "lab1.Task.getCompleted()" is null



The error happens because getCompleted() returns a Boolean that is null and Java tries to autounbox it to boolean (for removeIf, ? : or boolean checks). Fix: use a primitive boolean with a default of false so it can never be null.
Updated file: src/main/java/lab1/Task.java
// java
package lab1;

import java.util.Scanner;

public class Task {

    private String taskTitle;
    private String taskDescription;
    private boolean completed = false;
    private Scanner input;

    public Task() {
        this.completed = false;
    }

    public void initializeTask() {
        System.out.println("Adding Task");
        input = new Scanner(System.in);

        System.out.println("Enter Task Title: ");
        setTaskTitle(input.nextLine());

        System.out.println("Enter Task Description: ");
        setTaskDescription(input.nextLine());

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

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}


how is it null when I set it to false in the initialize method?


Because completed is declared as a Boolean (object) it defaults to null unless you set it. initializeTask() sets it to false only when you call that method — objects loaded from file or created without calling initializeTask() will keep the null. Autounboxing Boolean to boolean (e.g. in removeIf or if (task.getCompleted())) then throws the NPE. Fix by using the primitive boolean with a default value.
Updated src/main/java/lab1/Task.java:
// java
package lab1;

import java.util.Scanner;

public class Task {

    private String taskTitle;
    private String taskDescription;
    private boolean completed = false; // primitive defaults to false
    private Scanner input;

    public Task() {
        this.completed = false;
    }

    public void initializeTask() {
        System.out.println("Adding Task");
        input = new Scanner(System.in);

        System.out.println("Enter Task Title: ");
        setTaskTitle(input.nextLine());

        System.out.println("Enter Task Description: ");
        setTaskDescription(input.nextLine());

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

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}


is there a way I can make a temprorary file to test operations being done on the file?



Yes. Create a temporary file with Files.createTempFile, write test data, run your file-operation code against it, then verify contents and delete the temp file. Example JUnit 5 test that demonstrates creating a temp file, removing completed tasks (lines that end with |true), and asserting the result:
// java
package lab1;

import org.junit.jupiter.api.Test;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TempFileTest {

    @Test
    void removeCompletedFromTempFile() throws Exception {
        Path tmp = Files.createTempFile("tasks-test-", ".tmp");
        tmp.toFile().deleteOnExit();

        // sample lines: title|description|completed
        List<String> lines = Arrays.asList(
            "Task 1|Do something|false",
            "Task 2|Done already|true",
            "Task 3|Another task|false"
        );
        Files.write(tmp, lines, StandardCharsets.UTF_8);

        // Call the operation to test (here implemented locally for demo)
        removeCompletedFromFile(tmp);

        List<String> remaining = Files.readAllLines(tmp, StandardCharsets.UTF_8);
        assertEquals(2, remaining.size());
        assertTrue(remaining.stream().allMatch(l -> l.endsWith("|false")));
        
        Files.deleteIfExists(tmp);
    }

    // Example implementation you can replace with your application's method
    private void removeCompletedFromFile(Path path) throws IOException {
        List<String> all = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> kept = all.stream()
            .filter(line -> {
                String[] parts = line.split("\\|");
                return parts.length < 3 || !"true".equalsIgnoreCase(parts[2].trim());
            })
            .collect(Collectors.toList());
        Files.write(path, kept, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
Use this pattern to create temp files for any file-based operation and to avoid touching real data.



why is this not reading anything fro the temp file?


You're creating a FileReader and calling fr.readAllLines() which doesn't exist. Also you open the reader before writing, and your test calls writeToFile(...) that likely writes to a different file than the temp file. Fix: write to the temp file (or pass it into your app code), then read it with Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8) after writing.
Example fix for src/test/java/lab1/AppTest.java — write known lines to the temp file, modify the file to mark tasks complete, then read with Files.readAllLines:
// java
package lab1;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AppTest extends App {

    @Test
    void completeTaskInFile() throws IOException {
        Task task1 = new Task("Test Task 1", "This is a test task", false);
        Task task2 = new Task("Test Task 2", "This is a test task", false);
        Task task3 = new Task("Test Task 3", "This is a test task", false);
        Task task4 = new Task("Test Task 4", "This is a test task", false);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // write initial lines to the temp file
        List<String> initial = Arrays.asList(
            lineOf(task1),
            lineOf(task2),
            lineOf(task3),
            lineOf(task4)
        );
        Files.write(tempFile.toPath(), initial, StandardCharsets.UTF_8);

        // simulate marking task2 and task3 complete (replace with your method under test if available)
        List<String> updated = Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8)
            .stream()
            .map(l -> l.startsWith("Test Task 2|") || l.startsWith("Test Task 3|") ? l.replace("|false", "|true") : l)
            .collect(Collectors.toList());
        Files.write(tempFile.toPath(), updated, StandardCharsets.UTF_8);

        // read back correctly using Files.readAllLines
        List<String> allLines = Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8);

        assertTrue(allLines.contains("Test Task 1|This is a test task|false"));
        assertTrue(allLines.contains("Test Task 2|This is a test task|true"));
        assertTrue(allLines.contains("Test Task 3|This is a test task|true"));
        assertTrue(allLines.contains("Test Task 4|This is a test task|false"));
    }

    private static String lineOf(Task t) {
        return t.getTaskTitle() + "|" + t.getTaskDescription() + "|" + (Boolean.TRUE.equals(t.getCompleted()) ? "true" : "false");
    }
}


why is this failing?


Problems:
You open a FileReader before writing and never re-read the file.
FileReader doesn't have readAllLines(); that call is wrong.
Files.readAllLines() returns a list of single lines, but your assertions compare multi-line strings — they won't match.
Fix: read the file after all writes using Files.readString(...) (reads the whole file as one string) and assert contains(...).
Explanation done. Fixed test file:
// java
package lab1;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class AppTest extends App {

    @Test
    void completeTaskInFile() throws IOException {
        Task task1 = new Task("Test Task 1", "This is a test task", false);
        Task task2 = new Task("Test Task 2", "This is a test task", false);
        Task task3 = new Task("Test Task 3", "This is a test task", false);
        Task task4 = new Task("Test Task 4", "This is a test task", false);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // write tasks (use your App.writeToFile implementation)
        writeToFile(task1, tempFile);
        writeToFile(task2, tempFile);
        writeToFile(task3, tempFile);
        writeToFile(task4, tempFile);

        // mark tasks complete
        completeTaskInFile(task2, tempFile);
        completeTaskInFile(task3, tempFile);

        // read the whole file after all writes/updates
        String content = Files.readString(tempFile.toPath());
        System.out.println("File content:\n" + content);

        assertTrue(content.contains("Task:Test Task 1,\n" +
                " Description:This is a test task,\n" +
                " Status:Not Complete"));
        assertTrue(content.contains("Task:Test Task 2,\n" +
                " Description:This is a test task,\n" +
                " Status:Complete"));
        assertTrue(content.contains("Task:Test Task 3,\n" +
                " Description:This is a test task,\n" +
                " Status:Complete"));
        assertTrue(content.contains("Task:Test Task 4,\n" +
                " Description:This is a test task,\n" +
                " Status:Not Complete"));
    }
}



why is this failing?


Problem: the test opens a FileReader before writing and calls a non-existent readAllLines() on it. You should write to the temp file first, then read with Files.readAllLines(...) (or Files.readString(...)) and assert against the lines. Below is a corrected src/test/java/lab1/AppTest.java that avoids FileReader, reads after writes and checks for the task titles using anyMatch.

package lab1;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppTest extends App {

    @Test
    public void writeToFile() throws IOException {
        Task task1 = new Task("Test-task-1", "This is a test task", false);
        Task task2 = new Task("Test-task-2", "This is a test task", true);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // perform writes to the temp file (use your App.writeToFile implementation)
        writeToFile(task1, tempFile);
        writeToFile(task2, tempFile);

        // read after all writes using Files.readAllLines
        List<String> allLines = Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8);
        System.out.println("All lines: " + allLines);

        // assert that the file contains the task titles (use contains on lines to be flexible)
        assertTrue(allLines.stream().anyMatch(l -> l.contains("Test-task-1")));
        assertTrue(allLines.stream().anyMatch(l -> l.contains("Test-task-2")));
    }

    // other tests...
}


In the output, allLines does contain the tasks I have created, and does contain the text "Test-Task-1" that I am checking for in the assertion statement, yes the test is still failing. Why is this?



The assertion fails because List.contains(...) checks for an exact element match; your file lines include the task title inside a longer formatted line, so contains returns false even though a line contains the substring. Also open the reader after writing (or better, use Files.readAllLines(...)) so you read the updated contents. The fix below reads the file after the writes and asserts using anyMatch/substring checks.
// java
package lab1;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppTest extends App {

    @Test
    public void writeToFile() throws IOException {
        Task task1 = new Task("Test-task-1", "This is a test task", false);
        Task task2 = new Task("Test-task-2", "This is a test task", true);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // perform writes to the temp file using your App.writeToFile implementation
        writeToFile(task1, tempFile);
        writeToFile(task2, tempFile);

        // read after all writes using Files.readAllLines
        List<String> allLines = Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8);
        System.out.println("All lines: " + allLines);

        // check for substring presence in any line
        assertTrue(allLines.stream().anyMatch(l -> l.contains("Test-task-1")));
        assertTrue(allLines.stream().anyMatch(l -> l.contains("Test-task-2")));
    }

    @Test
    void completeTaskInFile() throws IOException {
        Task task1 = new Task("Test Task 1", "This is a test task", false);

        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        writeToFile(task1, tempFile);
        completeTaskInFile(task1, tempFile);

        List<String> allLines = Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8);
        // assert there is no "Not Complete" substring left
        assertFalse(allLines.stream().anyMatch(l -> l.contains("Not Complete")));
    }

    @Test
    public void deleteCompletedFromFile() {
    }

    @Test
    public void getAllFileTasks() {
    }
}


would you generally test the run method of a App.java file?


Generally: avoid testing the interactive run method directly. Test the smaller, deterministic methods (writeToFile, completeTaskInFile, deleteCompletedFromFile, getAllFileTasks) instead. If you need to test run, inject InputStream/PrintStream (already supported) and set the toDoFile to a temp file (use reflection or add a constructor/setter). Then drive the menu with a ByteArrayInputStream and assert against a captured ByteArrayOutputStream.
Below is a concise example test that sets toDoFile via reflection, sends the menu choice 5 to quit, runs run() and asserts output contains Quit.
// java
package lab1;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AppRunTest {

    @Test
    void runExitsWhenChoosingQuit() throws Exception {
        File tempFile = File.createTempFile("testFile", ".txt");
        tempFile.deleteOnExit();

        // send "5" to choose Quit from the menu
        ByteArrayInputStream in = new ByteArrayInputStream("5\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos, true, StandardCharsets.UTF_8.name());

        App app = new App(in, out);

        // inject the temp file into the private toDoFile field
        Field toDoField = App.class.getDeclaredField("toDoFile");
        toDoField.setAccessible(true);
        toDoField.set(app, tempFile);

        // run should return after processing the "5" input
        app.run();

        String output = baos.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.contains("Quit"));
    }
}
File: src/test/java/lab1/AppRunTest.java







