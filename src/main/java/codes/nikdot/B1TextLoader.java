package codes.nikdot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * The B1TextLoader class is responsible for loading text files and exporting
 * their contents as a JSON file.
 */
public class B1TextLoader {
    /**
     * A ConcurrentHashMap to store the documents loaded from the text file.
     * The key is a string identifier for each document, and the value is the
     * content of the document.
     */
    ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();

    /**
     * The path to the input text file.
     */
    private String inputFile;

    /**
     * The path to the output JSON file.
     */
    private String outputFile;

    /**
     * Sets the path to the input text file.
     *
     * @param inputFile The path to the input text file
     */
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Sets the path to the output JSON file.
     *
     * @param outputFile The path to the output JSON file
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * The main method to run the B1TextLoader.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        B1TextLoader loader = new B1TextLoader();
        loader.setInputFile(args[0]);
        loader.setOutputFile(args[1]);
        loader.loadTextFile();
        loader.saveDocumentsToJSON();
    }

    /**
     * Loads a text file and stores its contents in the documents map.
     * Each non-empty line in the file is stored as a separate document.
     */
    public void loadTextFile() {
        System.out.println("Loading File...");

        try {
            Stream<String> lines = Files.lines(Path.of(inputFile));
            lines.forEach(line -> {
                if (!line.trim().isEmpty()) {
                    documents.put("doc" + documents.size(), line);
                }
            });
            lines.close();
        } catch (Exception e) {
            System.out.println("File Load Failure!");
        }
    }

    /**
     * Counts the words in each document stored in the documents map.
     * For each document, it calls the countWordsInSingleDocument method.
     *
     * @param documents A ConcurrentHashMap containing document identifiers as keys
     *                  and document contents as values
     */
    public void countWordsInDocuments(ConcurrentHashMap<String, String> documents) {
        documents.forEach(this::countWordsInSingleDocument);
    }

    /**
     * Counts the words in a single document and prints the result.
     *
     * @param key   The identifier of the document
     * @param value The content of the document
     */
    public void countWordsInSingleDocument(String key, String value) {
        String[] words = value.split(" ");
        System.out.println(key + " has " + words.length + " words");
    }

    /**
     * Saves the documents stored in the `documents` map to a JSON file.
     * It creates a JSON structure, adds the documents to the JSON structure,
     * and then saves the JSON structure to the specified file.
     */
    public void saveDocumentsToJSON() {
        JSONIOHelper jsonIO = new JSONIOHelper();
        jsonIO.createJSONStructure();
        jsonIO.addDocumentsToJSONStructure(documents);
        jsonIO.saveJSONStructure(outputFile);
    }

}

