package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    private Pipeline pipeline;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pipeline = new Pipeline();
    }

    @Test
    void testRun() throws IOException {
        // Create a temporary input file
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "This is a test document.");

        // Define the output file
        Path outputFile = tempDir.resolve("output.json");

        // Set the command-line arguments
        String[] args = {inputFile.toString(), outputFile.toString()};

        // Execute the pipeline
        int exitCode = new CommandLine(pipeline).execute(args);
        assertEquals(0, exitCode);

        // Verify the output file exists
        assertTrue(Files.exists(outputFile));

        // Verify the content of the output file
        String outputContent = Files.readString(outputFile);
        assertNotNull(outputContent);
        assertTrue(outputContent.contains("test"));
    }

    @Test
    void testMain() throws IOException {
        // Create a temporary input file
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "This is another test document.");

        // Define the output file
        Path outputFile = tempDir.resolve("output.json");

        // Set the command-line arguments
        String[] args = {inputFile.toString(), outputFile.toString()};

        // Execute the main method
        Pipeline.main(args);

        // Verify the output file exists
        assertTrue(Files.exists(outputFile));

        // Verify the content of the output file
        String outputContent = Files.readString(outputFile);
        assertNotNull(outputContent);
        assertTrue(outputContent.contains("test"));
    }
}