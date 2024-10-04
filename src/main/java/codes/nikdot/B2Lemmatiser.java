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
    ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, String> lemmatisedDocuments = new ConcurrentHashMap<>();
    private String inputFile;
    private String outputFile;

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * The main method to start the lemmatization process.
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
     * Starts the lemmatization process on the specified JSON file.
     * It loads the JSON structure, retrieves documents, lemmatizes them, and saves the lemmatized documents.
     */
    void startLemmanisation() {
        System.out.println("Starting Lemmanisation on " + inputFile + "...");
        JSONIOHelper jsonIO = new JSONIOHelper();
        jsonIO.loadJSONStructure(inputFile);
        documents = jsonIO.getDocumentsFromJSONStructure();
        for (String docId : documents.keySet()) {
            String docContent = documents.get(docId);
            String lemmanisedDoc = lemmaniseSingleDocument(docContent);
        }
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
     * Lemmatizes a single document.
     * It removes punctuation, converts text to lowercase, and lemmatizes the words.
     *
     * @param text The text of the document to lemmatize
     * @return The lemmatized text
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