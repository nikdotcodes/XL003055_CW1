package codes.nikdot;

import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class B3DescriptiveStatistics{

    ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> lemmaCounts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> documentWordCounts = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        B3DescriptiveStatistics descriptiveStatistics = new B3DescriptiveStatistics();
        descriptiveStatistics.startCreatingStatistics("outputs/ComplexTextFile.json");
    }

    private void startCreatingStatistics(String fileName){
        System.out.println("Creating statistics...");
        JSONIOHelper json = new JSONIOHelper();
        json.loadJSONStructure("outputs/ComplexTextFile.json");
        lemmas = json.getLemmasFromJSONStructure();
        for (Map.Entry<String, String> entry: lemmas.entrySet()){
            String lem = entry.getValue();
            String[] words = lem.split(" ");
            int wordCount = words.length;
            System.out.println("Word count for lemma " + entry.getKey() + ": " + wordCount);
        }
        countWordsInCorpus(lemmas);
        countWordInDocuments(lemmas);
        outputCountsAsCSV(lemmaCounts, "outputs/lemmasCounts.csv");
        outputCountsAsCSV(documentWordCounts, "outputs/documentWordCounts.csv");
    }

    private void countWordsInCorpus(ConcurrentHashMap<String, String> lemmas){
        for (Map.Entry<String, String> entry: lemmas.entrySet()){
            String lem = entry.getValue();
            String[] words = lem.split(" ");
            for (String word: words){
                if (lemmaCounts.containsKey(word)){
                    lemmaCounts.put(word, lemmaCounts.get(word) + 1);
                } else {
                    lemmaCounts.put(word, 1);
                }
            }
        }
    }

    private void outputCountsAsCSV(ConcurrentHashMap<String, Integer> counts, String fileName){
        StringBuilder csv = new StringBuilder();
        for (Map.Entry<String, Integer> entry: counts.entrySet()){
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

    private void countWordInDocuments(ConcurrentHashMap<String, String> lemmas) {
        for (Map.Entry<String, String> entry: lemmas.entrySet()){
            String lem = entry.getValue();
            String[] words = lem.split(" ");
            documentWordCounts.put(entry.getKey(), words.length);
        }
    }
}
