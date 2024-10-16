package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class B2LemmatiserTest {

    private B2Lemmatiser lemmatiser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        lemmatiser = new B2Lemmatiser();
    }

    @Test
    void testLemmaniseSingleDocument() {
        String text = "This is a test document.";
        String expected = "this be a test document";
        String result = lemmatiser.lemmaniseSingleDocument(text);
        assertEquals(expected, result);
    }

    @Test
    void testStartLemmanisation() throws IOException {
        // Create a temporary input JSON file
        Path inputFile = tempDir.resolve("input.json");
        String jsonContent = "{\"documents\":{\"doc1\":\"This is a test document.\"}}";
        Files.writeString(inputFile, jsonContent);

        // Create a temporary output JSON file
        Path outputFile = tempDir.resolve("output.json");

        // Set the input and output file paths
        lemmatiser.setInputFile(inputFile.toString());
        lemmatiser.setOutputFile(outputFile.toString());

        // Start the lemmatization process
        lemmatiser.startLemmanisation();

        // Verify the output file exists
        assertTrue(Files.exists(outputFile));

        // Verify the content of the output file
        String outputContent = Files.readString(outputFile);
        assertNotNull(outputContent);
        assertTrue(outputContent.contains("this be a test document"));

        // Verify the input file is deleted
        assertFalse(Files.exists(inputFile));
    }
}