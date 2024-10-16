package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class B1TextLoaderTest {

    private B1TextLoader textLoader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        textLoader = new B1TextLoader();
    }

    @Test
    void testLoadTextFile() throws IOException {
        // Create a temporary input file
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "This is a test document.\nAnother line.");

        // Set the input file path
        textLoader.setInputFile(inputFile.toString());

        // Load the text file
        textLoader.loadTextFile();

        // Verify the documents map
        ConcurrentHashMap<String, String> documents = textLoader.documents;
        assertEquals(2, documents.size());
        assertEquals("This is a test document.", documents.get("doc0"));
        assertEquals("Another line.", documents.get("doc1"));
    }

    @Test
    void testCountWordsInSingleDocument() {
        // Create a sample document
        String key = "doc1";
        String value = "This is a test document";

        // Capture the output
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Count words in the document
        textLoader.countWordsInSingleDocument(key, value);

        // Verify the output
        String expectedOutput = "doc1 has 5 words\n";
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testSaveDocumentsToJSON() throws IOException {
        // Create a temporary output file
        Path outputFile = tempDir.resolve("output.json");

        // Set the output file path
        textLoader.setOutputFile(outputFile.toString());

        // Add sample documents
        textLoader.documents.put("doc1", "This is a test document");

        // Save documents to JSON
        textLoader.saveDocumentsToJSON();

        // Verify the output file exists
        assertTrue(Files.exists(outputFile));

        // Verify the content of the output file
        String outputContent = Files.readString(outputFile);
        assertNotNull(outputContent);
        assertTrue(outputContent.contains("This is a test document"));
    }
}