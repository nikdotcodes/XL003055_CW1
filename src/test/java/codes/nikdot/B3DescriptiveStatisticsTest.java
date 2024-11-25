package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class B3DescriptiveStatisticsTest {

    private B3DescriptiveStatistics descriptiveStatistics;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        descriptiveStatistics = new B3DescriptiveStatistics();
        descriptiveStatistics.setCorpusWordCountFile(tempDir.resolve("corpusWordCount.csv").toString());
        descriptiveStatistics.setDocumentWordCountFile(tempDir.resolve("documentWordCount.csv").toString());
        descriptiveStatistics.setLemmasFile(tempDir.resolve("lemmas.json").toString());
    }

    @Test
    void testStartCreatingStatistics() throws IOException {
        // Create a temporary input JSON file
        Path inputFile = tempDir.resolve("lemmas.json");
        String jsonContent = "{\"documents\":{\"doc1\":\"this is a test document\"},\"lemmas\":{\"doc1\":\"this be a test document\"}}";
        Files.writeString(inputFile, jsonContent);

        // Start creating statistics
        descriptiveStatistics.startCreatingStatistics();

        // Verify the output files exist
        Path lemmaCountsFile = tempDir.resolve("corpusWordCount.csv");
        Path documentWordCountsFile = tempDir.resolve("documentWordCount.csv");
        assertTrue(Files.exists(lemmaCountsFile));
        assertTrue(Files.exists(documentWordCountsFile));

        // Verify the content of the output files
        String lemmaCountsContent = Files.readString(lemmaCountsFile);
        String documentWordCountsContent = Files.readString(documentWordCountsFile);
        assertNotNull(lemmaCountsContent);
        assertNotNull(documentWordCountsContent);
        assertTrue(lemmaCountsContent.contains("this,1"));
        assertTrue(documentWordCountsContent.contains("doc1,5"));
    }

    @Test
    void testCountWordsInCorpus() {
        ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();
        lemmas.put("doc1", "this be a test document");
        descriptiveStatistics.countWordsInCorpus(lemmas);

        assertEquals(1, descriptiveStatistics.lemmaCounts.get("this"));
        assertEquals(1, descriptiveStatistics.lemmaCounts.get("be"));
        assertEquals(1, descriptiveStatistics.lemmaCounts.get("a"));
        assertEquals(1, descriptiveStatistics.lemmaCounts.get("test"));
        assertEquals(1, descriptiveStatistics.lemmaCounts.get("document"));
    }

    @Test
    void testCountWordInDocuments() {
        ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();
        lemmas.put("doc1", "this be a test document");
        descriptiveStatistics.countWordInDocuments(lemmas);

        assertEquals(5, descriptiveStatistics.documentWordCounts.get("doc1"));
    }

    @Test
    void testOutputCountsAsCSV() throws IOException {
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        counts.put("this", 1);
        counts.put("be", 1);
        counts.put("a", 1);
        counts.put("test", 1);
        counts.put("document", 1);

        Path outputFile = tempDir.resolve("output.csv");
        descriptiveStatistics.outputCountsAsCSV(counts, outputFile.toString());

        // Verify the output file exists
        assertTrue(Files.exists(outputFile));

        // Verify the content of the output file
        String outputContent = Files.readString(outputFile);
        assertNotNull(outputContent);
        assertTrue(outputContent.contains("this,1"));
        assertTrue(outputContent.contains("be,1"));
        assertTrue(outputContent.contains("a,1"));
        assertTrue(outputContent.contains("test,1"));
        assertTrue(outputContent.contains("document,1"));
    }
}