package codes.nikdot;

import edu.stanford.nlp.simple.*;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The B2Lemmatiser class provides methods to lemmatise documents.
 * It reads documents from a JSON file, lemmatises the content, and saves the lemmatised documents back to a JSON file.
 */
public class B2Lemmatiser {
    /**
     * A ConcurrentHashMap to store the documents loaded from the JSON file.
     * The key is a string identifier for each document, and the value is the
     * content of the document.
     */
    ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();

    /**
     * A ConcurrentHashMap to store the lemmatised documents.
     * The key is a string identifier for each document, and the value is the
     * lemmatised content of the document.
     */
    ConcurrentHashMap<String, String> lemmatisedDocuments = new ConcurrentHashMap<>();

    /**
     * The path to the input JSON file.
     */
    private String inputFile;

    /**
     * The path to the output JSON file.
     */
    private String outputFile;

    /**
     * Sets the path to the input JSON file.
     *
     * @param inputFile The path to the input JSON file
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
     * The main method to start the lemmatisation process.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        B2Lemmatiser lem = new B2Lemmatiser();
        lem.setInputFile(args[0]);
        lem.setOutputFile(args[1]);

        lem.startLemmanisation();
    }

    /**
     * Starts the lemmatisation process on the specified JSON file.
     * It loads the JSON structure, retrieves documents, lemmatises them, and saves the lemmatised documents.
     */
    void startLemmanisation() {
        System.out.println("Starting Lemmanisation on " + inputFile + "...");
        JSONIOHelper jsonIO = new JSONIOHelper();
        jsonIO.loadJSONStructure(inputFile);
        documents = jsonIO.getDocumentsFromJSONStructure();
        for(Map.Entry<String, String> entry: documents.entrySet()) {
            System.out.println("Reading Document: " + entry.getKey());
            String lemmanisedDoc = lemmaniseSingleDocument(entry.getValue());
            lemmatisedDocuments.put(entry.getKey(), lemmanisedDoc);
        }
        jsonIO.addLemmasToJSONStructure(lemmatisedDocuments);
        jsonIO.saveJSONStructure(outputFile);

        jsonIO.deleteJSONStructure(inputFile);
    }

    /**
     * Lemmatises a single document.
     * It removes punctuation, converts text to lowercase, and lemmatises the words.
     *
     * @param text The text of the document to lemmatise
     * @return The lemmatised text
     */
    String lemmaniseSingleDocument(String text) {
        System.out.println("Lemmanising Document...");
        text = text.replaceAll("\\p{Punct}", " ");
        text = text.replaceAll("\\s+", " ");
        text = text.toLowerCase();
        text = text.trim();

        Sentence sentence = new Sentence(text);
        List<String> lemmas = sentence.lemmas();
        System.out.println("Lemmanisation Complete!");

        return String.join(" ", lemmas);
    }
}