package codes.nikdot;

import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The B3DescriptiveStatistics class provides methods to generate descriptive statistics
 * from lemmatised documents. It reads lemmas from a JSON file, counts word occurrences,
 * and outputs the statistics to CSV files.
 */
public class B3DescriptiveStatistics {

    /**
     * A ConcurrentHashMap to store the lemmatised documents.
     * The key is a string identifier for each document, and the value is the
     * lemmatised content of the document.
     */
    ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();

    /**
     * A ConcurrentHashMap to store the count of each lemma in the corpus.
     * The key is the lemma, and the value is the count of occurrences.
     */
    ConcurrentHashMap<String, Integer> lemmaCounts = new ConcurrentHashMap<>();

    /**
     * A ConcurrentHashMap to store the word count for each document.
     * The key is a string identifier for each document, and the value is the word count.
     */
    ConcurrentHashMap<String, Integer> documentWordCounts = new ConcurrentHashMap<>();

    /**
     * The path to the CSV file for corpus word counts.
     */
    private String corpusWordCountFile;

    /**
     * The path to the CSV file for document word counts.
     */
    private String documentWordCountFile;

    /**
     * The path to the JSON file containing the lemmas.
     */
    private String lemmasFile;

    /**
     * The main method to start the descriptive statistics generation process.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        B3DescriptiveStatistics descriptiveStatistics = new B3DescriptiveStatistics();
        descriptiveStatistics.startCreatingStatistics();
    }

    /**
     * Sets the path to the CSV file for corpus word counts.
     *
     * @param corpusWordCountFile The path to the CSV file for corpus word counts
     */
    public void setCorpusWordCountFile(String corpusWordCountFile) {
        this.corpusWordCountFile = corpusWordCountFile;
    }

    /**
     * Sets the path to the CSV file for document word counts.
     *
     * @param documentWordCountFile The path to the CSV file for document word counts
     */
    public void setDocumentWordCountFile(String documentWordCountFile) {
        this.documentWordCountFile = documentWordCountFile;
    }

    /**
     * Sets the path to the JSON file containing the lemmas.
     *
     * @param lemmasFile The path to the JSON file containing the lemmas
     */
    public void setLemmasFile(String lemmasFile) {
        this.lemmasFile = lemmasFile;
    }

    /**
     * Starts the process of creating descriptive statistics from the specified JSON file.
     * It loads the JSON structure, retrieves lemmas, counts words, and outputs the statistics to CSV files.
     */
    public void startCreatingStatistics() {
        System.out.println("Creating statistics...");
        JSONIOHelper json = new JSONIOHelper();
        json.loadJSONStructure(lemmasFile);
        lemmas = json.getLemmasFromJSONStructure();
        for (Map.Entry<String, String> entry : lemmas.entrySet()) {
            String lem = entry.getValue();
            String[] words = lem.split(" ");
            int wordCount = words.length;
            System.out.println("Word count for lemma " + entry.getKey() + ": " + wordCount);
        }
        countWordsInCorpus(lemmas);
        countWordInDocuments(lemmas);
        outputCountsAsCSV(lemmaCounts, corpusWordCountFile);
        outputCountsAsCSV(documentWordCounts, documentWordCountFile);
    }

    /**
     * Counts the occurrences of each word in the corpus.
     *
     * @param lemmas A ConcurrentHashMap containing the lemmatised documents
     */
    void countWordsInCorpus(ConcurrentHashMap<String, String> lemmas) {
        for (Map.Entry<String, String> entry : lemmas.entrySet()) {
            String lem = entry.getValue();
            String[] words = lem.split(" ");
            for (String word : words) {
                lemmaCounts.merge(word, 1, Integer::sum);
            }
        }
    }

    /**
     * Outputs the word counts to a CSV file.
     *
     * @param counts   A ConcurrentHashMap containing the word counts
     * @param fileName The path to the CSV file to save the word counts
     */
    void outputCountsAsCSV(ConcurrentHashMap<String, Integer> counts, String fileName) {
        StringBuilder csv = new StringBuilder();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            csv.append(entry.getKey()).append(",").append(entry.getValue());
            csv.append(System.lineSeparator());
        }
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(csv.toString());
            System.out.println("CSV saved successfully!");
        } catch (Exception e) {
            System.out.println("CSV save failed!");
            throw new RuntimeException(e);
        }
    }

    /**
     * Counts the number of words in each document.
     *
     * @param lemmas A ConcurrentHashMap containing the lemmatised documents
     */
    void countWordInDocuments(ConcurrentHashMap<String, String> lemmas) {
        for (Map.Entry<String, String> entry : lemmas.entrySet()) {
            String lem = entry.getValue();
            String[] words = lem.split(" ");
            documentWordCounts.put(entry.getKey(), words.length);
        }
    }
}