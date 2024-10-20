package codes.nikdot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The JSONIOHelper class provides methods to create a JSON structure,
 * add documents to the JSON structure, and save the JSON structure to a file.
 */
public class JSONIOHelper {

    /**
     * The root JSON object that contains the entire JSON structure.
     */
    JSONObject rootObject;
    /**
     * The JSON object that contains the documents.
     */
    JSONObject documentsObject;
    /**
     * The JSON object that contains the lemmas.
     */
    JSONObject lemmasObject;

    /**
     * A hash map to store document identifiers and their contents.
     */
    ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();

    /**
     * A hash map to store document identifiers and their corresponding lemmas.
     */
    ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();

    /**
     * Creates the initial JSON structure with a root object and a documents object.
     */
    public void createJSONStructure() {
        rootObject = new JSONObject();
        documentsObject = new JSONObject();
        lemmasObject = new JSONObject();

        rootObject.put("documents", documentsObject);
        rootObject.put("lemmas", lemmasObject);
    }

    /**
     * Adds documents to the JSON structure.
     * Each document is added as a key-value pair in the documents object.
     *
     * @param documents A ConcurrentHashMap containing document identifiers as keys
     *                  and document contents as values
     */
    public void addDocumentsToJSONStructure(ConcurrentHashMap<String, String> documents) {
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            documentsObject.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds lemmas to the JSON structure.
     * Each lemma is added as a key-value pair in the lemmas object.
     *
     * @param lemmas A ConcurrentHashMap containing document identifiers as keys
     *               and lemma contents as values
     */
    public void addLemmasToJSONStructure(ConcurrentHashMap<String, String> lemmas) {
        for (Map.Entry<String, String> entry : lemmas.entrySet()) {
            if (rootObject.get("lemmas") == null) {
                lemmasObject = new JSONObject();
                rootObject.put("lemmas", lemmasObject);
            }
            lemmasObject.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Saves the JSON structure to a file.
     * The JSON structure is converted to a string and written to the specified file.
     *
     * @param fileName The name of the file to save the JSON structure to
     */
    public void saveJSONStructure(String fileName) {
        String jsonString = rootObject.toJSONString();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(jsonString);
            System.out.println("JSON saved successfully!");
        } catch (Exception e) {
            System.out.println("JSON save failed!");
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the JSON structure from a file.
     * The JSON structure is read from the specified file and parsed into the root object.
     *
     * @param fileName The name of the file to load the JSON structure from
     */
    public void loadJSONStructure(String fileName) {
        try (FileReader file = new FileReader(fileName)) {
            JSONParser parser = new JSONParser();
            rootObject = (JSONObject) parser.parse(file);
            if (rootObject.get("documents") != null) {
                documentsObject = (JSONObject) rootObject.get("documents");
            }
            if (rootObject.get("lemmas") != null) {
                lemmasObject = (JSONObject) rootObject.get("lemmas");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("JSON document loaded successfully! Documents: " + documentsObject.size());
        }
    }

    /**
     * Deletes the specified JSON file.
     *
     * @param fileName The name of the file to be deleted.
     */
    public void deleteJSONStructure(String fileName) {
        File fileToDelete = new File(fileName);
        if (fileToDelete.delete()) {
            System.out.println("Temp File deleted successfully!");
        } else {
            System.out.println("Temp File failed to be deleted.");
        }
    }

    /**
     * Retrieves documents from the JSON structure.
     * The documents are extracted from the document object and returned as a ConcurrentHashMap.
     *
     * @return A ConcurrentHashMap containing document identifiers as keys and document contents as values
     */
    public ConcurrentHashMap<String, String> getDocumentsFromJSONStructure() {
        for (String key : (Iterable<String>)documentsObject.keySet()) {
            documents.put(key, (String) documentsObject.get(key));
        }
        System.out.println("Documents retrieved from JSON: " + documents.size());
        return documents;
    }

    /**
     * Retrieves lemmas from the JSON structure.
     * The lemmas are extracted from the lemmas object and returned as a ConcurrentHashMap.
     *
     * @return A ConcurrentHashMap containing document identifiers as keys and lemma contents as values
     */
    public ConcurrentHashMap<String, String> getLemmasFromJSONStructure() {
        for (String key : (Iterable<String>)lemmasObject.keySet()) {
            lemmas.put(key, (String) lemmasObject.get(key));
        }
        System.out.println("Lemmas retrieved from JSON: " + lemmas.size());
        return lemmas;
    }
}
