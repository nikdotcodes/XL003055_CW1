package codes.nikdot;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * The B4TopicModelling class provides methods to perform topic modeling on lemmatised documents.
 * It reads lemmas from a JSON file, saves them to a flat file, and runs the topic modeling process.
 */
public class B4TopicModelling {

    /**
     * A ConcurrentHashMap to store the lemmatized documents.
     * The key is a string identifier for each document, and the value is the lemmatised content of the document.
     */
    ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();

    private String lemmaFile;
    private String topicModelFile;

    public void setLemmaFile(String lemmaFile) {
        this.lemmaFile = lemmaFile;
    }

    public void setTopicModelFile(String topicModelFile) {
        this.topicModelFile = topicModelFile;
    }

    /**
     * The main method to start the topic modeling process.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        B4TopicModelling topicModelling = new B4TopicModelling();
        topicModelling.startTopicModelling();
    }

    /**
     * Starts the topic modeling process from the specified JSON file.
     * It loads the JSON structure, retrieves lemmas, saves them to a flat file, and runs the topic modeling process.
     *
     */
    public void startTopicModelling() {
        JSONIOHelper json = new JSONIOHelper();
        json.loadJSONStructure(lemmaFile);
        lemmas = json.getLemmasFromJSONStructure();
        saveLemmasToFlatFile(topicModelFile, lemmas);
        runTopicModelling(topicModelFile, 10, 8, 2000);
    }

    /**
     * Saves the lemmatised documents to a flat file.
     *
     * @param flatFile The path to the flat file to save the lemmas
     * @param lemmas   A ConcurrentHashMap containing the lemmatised documents
     */
    private void saveLemmasToFlatFile(String flatFile, ConcurrentHashMap<String, String> lemmas) {
        try (FileWriter writer = new FileWriter(flatFile)) {
            for (Map.Entry<String, String> entry : lemmas.entrySet()) {
                writer.write(entry.getKey() + "\ten\t" + entry.getValue() + System.lineSeparator());
            }
        } catch (Exception e) {
            System.out.println("Failed to save lemmas to flat file!");
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs the topic modeling process using the specified parameters.
     *
     * @param flatFile    The path to the flat file containing the lemmas
     * @param nTopics     The number of topics to generate
     * @param nThreads    The number of threads to use
     * @param nIterations The number of iterations to run
     */
    private void runTopicModelling(String flatFile, int nTopics, int nThreads, int nIterations) {
        ArrayList pipeList = new ArrayList();
        // Pipes: tokenize, map to features
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        InputStreamReader fileReader = null;
        File file = new File(flatFile);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileReader = new InputStreamReader(fileInputStream);
            instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));
        } catch (Exception e) {
            System.out.println("Failed to read flat file!");
            System.exit(1);
        }

        ParallelTopicModel model = new ParallelTopicModel(nTopics, 1.0, 0.01);

        model.addInstances(instances);
        model.setNumThreads(nThreads);
        model.setNumIterations(nIterations);

        try {
            model.estimate();
        } catch (Exception e) {
            System.out.println("Failed to estimate model!");
            System.exit(1);
        }
    }
}