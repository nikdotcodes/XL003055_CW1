package codes.nikdot;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import cc.mallet.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class B4TopicModelling {

    ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        B4TopicModelling topicModelling = new B4TopicModelling();
        topicModelling.startTopicModelling("outputs/ComplexTextFile.json");
    }

    private void startTopicModelling(String fileName) {
        JSONIOHelper json = new JSONIOHelper();
        json.loadJSONStructure(fileName);
        lemmas = json.getLemmasFromJSONStructure();
        saveLemmasToFlatFile("outputs/topicdata.txt", lemmas);
        runTopicModelling("outputs/topicdata.txt", 10, 8, 500);
    }

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
