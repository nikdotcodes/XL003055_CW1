package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class B4TopicModellingTest {

    private B4TopicModelling topicModelling;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        topicModelling = new B4TopicModelling();
    }

    @Test
    void testStartTopicModelling() throws IOException {
        // Create a temporary input JSON file
        Path inputFile = tempDir.resolve("input.json");
        String jsonContent = "{\"documents\":{\"doc1\":\"this is a test document\"},\"lemmas\":{\"doc1\":\"this be a test document\"}}";
        Files.writeString(inputFile, jsonContent);

        // Set the input file path
        topicModelling.setLemmaFile(inputFile.toString());
        topicModelling.setTopicModelFile(tempDir.resolve("topicdata.txt").toString());

        // Start the topic modeling process
        topicModelling.startTopicModelling();

        // Verify the output file exists
        Path topicDataFile = tempDir.resolve("topicdata.txt");
        assertTrue(Files.exists(topicDataFile));

        // Verify the content of the output file
        String topicDataContent = Files.readString(topicDataFile);
        assertNotNull(topicDataContent);
        assertTrue(topicDataContent.contains("this be a test document"));
    }

    @Test
    void testSaveLemmasToFlatFile() throws IOException {
        ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();
        lemmas.put("doc1", "this be a test document");

        Path flatFile = tempDir.resolve("lemmas.txt");
        topicModelling.saveLemmasToFlatFile(flatFile.toString(), lemmas);

        // Verify the output file exists
        assertTrue(Files.exists(flatFile));

        // Verify the content of the output file
        String flatFileContent = Files.readString(flatFile);
        assertNotNull(flatFileContent);
        assertTrue(flatFileContent.contains("this be a test document"));
    }

    @Test
    void testRunTopicModelling() throws IOException {
        // Create a temporary flat file
        Path flatFile = tempDir.resolve("lemmas.txt");
        Files.writeString(flatFile, "doc1\ten\tthis be a test document\n");

        // Run the topic modeling process
        topicModelling.runTopicModelling(flatFile.toString(), 2, 1, 10);

        // Verify the model is created
        assertNotNull(topicModelling.model);
        assertNotNull(topicModelling.modelTopWords);
    }
}