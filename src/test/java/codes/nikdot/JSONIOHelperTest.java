package codes.nikdot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class JSONIOHelperTest {

    private JSONIOHelper jsonIOHelper;

    @BeforeEach
    void setUp() {
        jsonIOHelper = new JSONIOHelper();
    }

    @Test
    void testCreateJSONStructure() {
        jsonIOHelper.createJSONStructure();
        assertNotNull(jsonIOHelper.rootObject);
        assertNotNull(jsonIOHelper.documentsObject);
        assertTrue(jsonIOHelper.rootObject.containsKey("documents"));
        assertEquals(jsonIOHelper.documentsObject, jsonIOHelper.rootObject.get("documents"));
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
}