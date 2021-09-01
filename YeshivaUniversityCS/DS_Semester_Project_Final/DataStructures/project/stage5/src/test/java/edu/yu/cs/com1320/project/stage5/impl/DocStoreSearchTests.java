package edu.yu.cs.com1320.project.stage5.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

public class DocStoreSearchTests {
    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

    //variables to hold possible values for doc3
    private URI uri3;
    private String txt3;

    //variables to hold possible values for doc4
    private URI uri4;
    private String txt4;

    private int bytes1;
    private int bytes2;
    private int bytes3;
    private int bytes4;
 
    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This doc1 plain text string Computer Headphones";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text doc2 plain String";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "This is the text of doc3";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "This is the text of doc4";

        this.bytes1 = this.txt1.getBytes().length;
        this.bytes2 = this.txt2.getBytes().length;
        this.bytes3 = this.txt3.getBytes().length;
        this.bytes4 = this.txt4.getBytes().length;
    }




    @Test
    public void docStoreSingleSearch() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        Document doc1 = store.getDocument(this.uri1);
        assertEquals(doc1.getKey(), this.uri1);
        long first = doc1.getLastUseTime();
        doc1 = store.getDocument(this.uri1);
        long second = doc1.getLastUseTime();
        //was last use time updated on the put?
        assertTrue(first < second, "last use time should be changed when the DocumentStore.getDocument method is called");

        Document doc2 = store.getDocument(this.uri2);
        assertEquals(doc2.getKey(), this.uri2);
        long first2 = doc2.getLastUseTime();
        doc2 = store.getDocument(this.uri2);
        long second2 = doc2.getLastUseTime();
        //was last use time updated on the put?
        assertTrue(first2 < second2, "last use time should be changed when the DocumentStore.getDocument method is called");

        Document doc3 = store.getDocument(this.uri3);
        assertEquals(doc3.getKey(), this.uri3);
        long first3 = doc3.getLastUseTime();
        doc3 = store.getDocument(this.uri3);
        long second3 = doc3.getLastUseTime();
        //was last use time updated on the put?
        assertTrue(first3 < second3, "last use time should be changed when the DocumentStore.getDocument method is called");

        Document doc4 = store.getDocument(this.uri4);
        assertEquals(doc4.getKey(), this.uri4);
        long first4 = doc4.getLastUseTime();
        doc4 = store.getDocument(this.uri4);
        long second4 = doc4.getLastUseTime();
        //was last use time updated on the put?
        assertTrue(first4 < second4, "last use time should be changed when the DocumentStore.getDocument method is called");
        
        Set<URI> uris = store.deleteAll("text");
        assertEquals(4, uris.size());
    }

    @Test
    public void docStoreMultiSearch() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        List<Document> docs = store.search("TexT");

        assertEquals(4, docs.size());
        long doc1LUT = docs.get(0).getLastUseTime();
        long time = java.lang.System.nanoTime();
        assertTrue( doc1LUT < time);
        long doc2LUT = docs.get(1).getLastUseTime();
        long doc3LUT = docs.get(2).getLastUseTime();
        long doc4LUT = docs.get(3).getLastUseTime();
        assertEquals(doc1LUT, doc2LUT);
        assertEquals(doc2LUT, doc3LUT);
        assertEquals(doc3LUT, doc4LUT);
        
        Set<URI> uris = store.deleteAll("text");
        assertEquals(4, uris.size());
    }


    @Test
    public void docStoreMultiPrefixSearch() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        List<Document> docs = store.searchByPrefix("T");

        assertEquals(4, docs.size());
        long doc1LUT = docs.get(0).getLastUseTime();
        long time = java.lang.System.nanoTime();
        assertTrue( doc1LUT < time);
        long doc2LUT = docs.get(1).getLastUseTime();
        long doc3LUT = docs.get(2).getLastUseTime();
        long doc4LUT = docs.get(3).getLastUseTime();
        assertEquals(doc1LUT, doc2LUT);
        assertEquals(doc2LUT, doc3LUT);
        assertEquals(doc3LUT, doc4LUT);
        
        Set<URI> uris = store.deleteAll("text");
        assertEquals(4, uris.size());
    }

    @Test
    public void returnDocBackToDiskAfterSingleSearch() throws IOException{
        File baseDir = new File(System.getProperty("user.dir"));
        DocumentStore store = new DocumentStoreImpl(baseDir);
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        store.setMaxDocumentCount(0);

        Document doc1 = store.getDocument(this.uri1);
        assertEquals(doc1.getKey(), this.uri1);
        long first = doc1.getLastUseTime();
        doc1 = store.getDocument(this.uri1);
        long second = doc1.getLastUseTime();
        //was last use time updated on the put?
        assertTrue(first < second, "last use time should be changed when the DocumentStore.getDocument method is called");

        
        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(file.exists());
        
        Set<URI> uris = store.deleteAll("text");
        assertEquals(4, uris.size());
        assertTrue(!file.exists());
    }

    @Test
    public void returnDocBackToDiskAfterMultiSearch() throws IOException{
        File baseDir = new File(System.getProperty("user.dir"));
        DocumentStore store = new DocumentStoreImpl(baseDir);
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        store.setMaxDocumentCount(0);
        List<Document> docs = store.search("text");

        assertEquals(4, docs.size());
        long doc1LUT = docs.get(0).getLastUseTime();
        long time = java.lang.System.nanoTime();
        assertTrue( doc1LUT < time);
        long doc2LUT = docs.get(1).getLastUseTime();
        long doc3LUT = docs.get(2).getLastUseTime();
        long doc4LUT = docs.get(3).getLastUseTime();
        assertEquals(doc1LUT, doc2LUT);
        assertEquals(doc2LUT, doc3LUT);
        assertEquals(doc3LUT, doc4LUT);

        
        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(file4.exists());


        Set<URI> uris = store.deleteAll("text");
        assertEquals(4, uris.size());
        assertTrue(!file.exists());
        assertTrue(!file2.exists());
        assertTrue(!file3.exists());
        assertTrue(!file4.exists());
    }

    @Test
    public void returnDocBackToDiskAfterMultiPrefixSearch() throws IOException{
        File baseDir = new File(System.getProperty("user.dir"));
        DocumentStore store = new DocumentStoreImpl(baseDir);
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        store.setMaxDocumentCount(0);
        List<Document> docs = store.searchByPrefix("t");

        assertEquals(4, docs.size());
        long doc1LUT = docs.get(0).getLastUseTime();
        long time = java.lang.System.nanoTime();
        assertTrue( doc1LUT < time);
        long doc2LUT = docs.get(1).getLastUseTime();
        long doc3LUT = docs.get(2).getLastUseTime();
        long doc4LUT = docs.get(3).getLastUseTime();
        assertEquals(doc1LUT, doc2LUT);
        assertEquals(doc2LUT, doc3LUT);
        assertEquals(doc3LUT, doc4LUT);

        
        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(file4.exists());


        Set<URI> uris = store.deleteAll("text");
        assertEquals(4, uris.size());

        assertTrue(!file.exists());
        assertTrue(!file2.exists());
        assertTrue(!file3.exists());
        assertTrue(!file4.exists());
    }

}
