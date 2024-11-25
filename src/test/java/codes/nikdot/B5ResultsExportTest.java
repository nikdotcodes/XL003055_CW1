package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class B5ResultsExportTest {

    private B5ResultsExport resultsExport;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        resultsExport = new B5ResultsExport();
    }

    @Test
    void testExportModelResults() throws IOException {
        // Set up the model top words
        Object[][] modelTopWords = {
                {"word1", "word2", "word3"},
                {"word4", "word5", "word6"}
        };
        resultsExport.setModelTopWords(modelTopWords);

        // Set the file template
        Path outputFile = tempDir.resolve("output.json");
        resultsExport.setFileTemplate(outputFile.toString());

        // Set output options
        resultsExport.setOutputCSV(true);
        resultsExport.setOutputParquet(false);
        resultsExport.setOutputDuckDB(false);
        resultsExport.setOutputJSON(false);

        // Export model results
        resultsExport.exportModelResults();

        // Verify the output CSV file exists
        Path csvFile = tempDir.resolve("output_ModelResults.csv");
        assertTrue(Files.exists(csvFile));

        // Verify the content of the CSV file
        String csvContent = Files.readString(csvFile);
        assertNotNull(csvContent);
        assertTrue(csvContent.contains("word1"));
        assertTrue(csvContent.contains("word4"));
    }
}