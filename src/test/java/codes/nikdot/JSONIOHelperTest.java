package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class JSONIOHelperTest {

    private JSONIOHelper jsonIOHelper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        jsonIOHelper = new JSONIOHelper();
    }

    @Test
    void testCreateJSONStructure() {
        jsonIOHelper.createJSONStructure();
        assertNotNull(jsonIOHelper.rootObject);
        assertNotNull(jsonIOHelper.documentsObject);
        assertNotNull(jsonIOHelper.lemmasObject);
        assertTrue(jsonIOHelper.rootObject.containsKey("documents"));
        assertTrue(jsonIOHelper.rootObject.containsKey("lemmas"));
        assertEquals(jsonIOHelper.documentsObject, jsonIOHelper.rootObject.get("documents"));
        assertEquals(jsonIOHelper.lemmasObject, jsonIOHelper.rootObject.get("lemmas"));
    }

    @Test
    void testAddDocumentsToJSONStructure() {
        jsonIOHelper.createJSONStructure();
        ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();
        documents.put("doc1", "Content of document 1");
        documents.put("doc2", "Content of document 2");

        jsonIOHelper.addDocumentsToJSONStructure(documents);

        assertEquals("Content of document 1", jsonIOHelper.documentsObject.get("doc1"));
        assertEquals("Content of document 2", jsonIOHelper.documentsObject.get("doc2"));
    }

    @Test
    void testAddLemmasToJSONStructure() {
        jsonIOHelper.createJSONStructure();
        ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();
        lemmas.put("doc1", "Lemma of document 1");
        lemmas.put("doc2", "Lemma of document 2");

        jsonIOHelper.addLemmasToJSONStructure(lemmas);

        assertEquals("Lemma of document 1", jsonIOHelper.lemmasObject.get("doc1"));
        assertEquals("Lemma of document 2", jsonIOHelper.lemmasObject.get("doc2"));
    }

    @Test
    void testSaveAndLoadJSONStructure() throws IOException {
        jsonIOHelper.createJSONStructure();
        ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();
        documents.put("doc1", "Content of document 1");
        jsonIOHelper.addDocumentsToJSONStructure(documents);

        Path jsonFile = tempDir.resolve("test.json");
        jsonIOHelper.saveJSONStructure(jsonFile.toString());

        JSONIOHelper loadedJsonIOHelper = new JSONIOHelper();
        loadedJsonIOHelper.loadJSONStructure(jsonFile.toString());

        assertNotNull(loadedJsonIOHelper.rootObject);
        assertNotNull(loadedJsonIOHelper.documentsObject);
        assertEquals("Content of document 1", loadedJsonIOHelper.documentsObject.get("doc1"));
    }

    @Test
    void testDeleteJSONStructure() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "{}");

        jsonIOHelper.deleteJSONStructure(jsonFile.toString());

        assertFalse(Files.exists(jsonFile));
    }

    @Test
    void testGetDocumentsFromJSONStructure() {
        jsonIOHelper.createJSONStructure();
        ConcurrentHashMap<String, String> documents = new ConcurrentHashMap<>();
        documents.put("doc1", "Content of document 1");
        jsonIOHelper.addDocumentsToJSONStructure(documents);

        ConcurrentHashMap<String, String> retrievedDocuments = jsonIOHelper.getDocumentsFromJSONStructure();

        assertEquals("Content of document 1", retrievedDocuments.get("doc1"));
    }

    @Test
    void testGetLemmasFromJSONStructure() {
        jsonIOHelper.createJSONStructure();
        ConcurrentHashMap<String, String> lemmas = new ConcurrentHashMap<>();
        lemmas.put("doc1", "Lemma of document 1");
        jsonIOHelper.addLemmasToJSONStructure(lemmas);

        ConcurrentHashMap<String, String> retrievedLemmas = jsonIOHelper.getLemmasFromJSONStructure();

        assertEquals("Lemma of document 1", retrievedLemmas.get("doc1"));
    }
}