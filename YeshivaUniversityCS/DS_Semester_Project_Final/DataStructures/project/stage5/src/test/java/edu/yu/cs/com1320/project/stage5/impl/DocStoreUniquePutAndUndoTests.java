package edu.yu.cs.com1320.project.stage5.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

public class DocStoreUniquePutAndUndoTests {
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
    private File baseDir;
 
    @BeforeEach
    public void init() throws Exception {
        baseDir = new File(System.getProperty("user.dir"));
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
    public void zeroLimitPutTxTTest() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(0);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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

        assertTrue(store.deleteDocument(this.uri1));
        assertTrue(store.deleteDocument(this.uri2));
        assertTrue(store.deleteDocument(this.uri3));
        assertTrue(store.deleteDocument(this.uri4));
    }

    @Test
    public void zeroLimitPutTxTTestThenUndo() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(0);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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

        store.undo();
        store.undo();
        store.undo();
        store.undo();

        assertNull(store.getDocument(this.uri1));
        assertNull(store.getDocument(this.uri2));
        assertNull(store.getDocument(this.uri3));
        assertNull(store.getDocument(this.uri4));
    }

    @Test
    public void zeroLimitPutTxTTestThenUndoOverwrite() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(0);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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

        one = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //should overwrite the uri1 that is on disk and replace it on disk because the store is at 0
        System.out.println("hashCode: " + one);
        assertTrue(file.exists());

        store.undo(this.uri2);
        assertTrue(!file2.exists());
        assertNull(store.getDocument(this.uri2));

        store.undo();//undo the overwrite, it should be moved to disk
        assertTrue(file.exists());

        store.undo();
        assertTrue(!file4.exists());
        assertNull(store.getDocument(this.uri4));

        store.undo();
        assertTrue(!file3.exists());
        assertNull(store.getDocument(this.uri3));

        store.undo();
        assertTrue(!file.exists());
        assertNull(store.getDocument(this.uri1));
    }

    @Test
    public void regularPutTxTTest() throws IOException{
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

        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(!file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(!file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        assertTrue(store.deleteDocument(this.uri1));
        assertTrue(store.deleteDocument(this.uri2));
        assertTrue(store.deleteDocument(this.uri3));
        assertTrue(store.deleteDocument(this.uri4));
    }

    @Test
    public void regularPutTxTTestThenUndo() throws IOException{
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

        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(!file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(!file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        store.undo();
        store.undo();
        store.undo();
        store.undo();

        assertNull(store.getDocument(this.uri1));
        assertNull(store.getDocument(this.uri2));
        assertNull(store.getDocument(this.uri3));
        assertNull(store.getDocument(this.uri4));
    }

    @Test
    public void regularPutTxTTestThenUndoOverwrite() throws IOException{
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

        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(!file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(!file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        one = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //should overwrite the uri1 and replace it in memory
        System.out.println("hashCode2: " + one);
        assertTrue(!file.exists());
        Document doc1 = store.getDocument(this.uri1);
        assertEquals(doc1.getDocumentTxt(), this.txt2);


        two = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        System.out.println("hashCode3: " + two);

        assertTrue(!file2.exists());

        Document doc2 = store.getDocument(this.uri2);
        assertEquals(doc2.getDocumentTxt(), this.txt1);

        store.undo();//undo the overwrite
        doc2 = store.getDocument(this.uri2);
        assertEquals(doc2.getDocumentTxt(), this.txt2);

        store.setMaxDocumentBytes(0);
        assertTrue(file.exists());
       
        assertTrue(file2.exists());

        assertTrue(file3.exists());

        assertTrue(file4.exists());

        store.undo();//undo the overwrite doc should still be on disk
        assertTrue(file.exists());
        doc1 = store.getDocument(this.uri1);
        assertEquals(doc1.getDocumentTxt(), this.txt1);
        assertTrue(file.exists());

        store.setMaxDocumentBytes(100);
        store.undo(this.uri2);
        assertNull(store.getDocument(this.uri2));
        assertTrue(!file2.exists());
        store.undo(this.uri3);
        assertNull(store.getDocument(this.uri3));
        assertTrue(!file3.exists());
        store.undo(this.uri1);
        assertNull(store.getDocument(this.uri1));
        assertTrue(!file.exists());
        store.undo(this.uri4);
        assertNull(store.getDocument(this.uri4));
        assertTrue(!file4.exists());
    }

    @Test
    public void memoryPutTxtTest() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        assertTrue(store.deleteDocument(this.uri1));
        assertTrue(store.deleteDocument(this.uri2));
        assertTrue(store.deleteDocument(this.uri3));
        assertTrue(store.deleteDocument(this.uri4));
    }

    @Test
    public void memoryPutTxtTestThenUndo() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        store.undo();
        assertNull(store.getDocument(this.uri4));
        assertNotNull(store.getDocument(this.uri2));
        assertTrue(!file2.exists());
        assertTrue(file.exists());

        store.undo();
        assertNull(store.getDocument(this.uri3));
        assertNotNull(store.getDocument(this.uri1));
        assertTrue(!file.exists());

        store.undo();
        assertNull(store.getDocument(this.uri2));
        store.undo();
        assertNull(store.getDocument(this.uri1));

        assertTrue(!file.exists());

        assertTrue(!file2.exists());

        assertTrue(!file3.exists());

        assertTrue(!file4.exists());
    }

    @Test
    public void memoryPutTxtTestThenUndoOverwrite1() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo1");
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo2");
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo3");
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo4");
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());


        //start with undoing doc4 and doc3 to make sure 1 and 2 are put back into memory
        store.undo();//deletes doc4 and puts back doc2 into the store
        assertTrue(!file2.exists());
        assertTrue(!file4.exists());
        assertNull(store.getDocument(this.uri4));
        assertNotNull(store.getDocument(this.uri2));
        assertTrue(!file3.exists());
        assertTrue(file.exists());

        store.undo();//delete doc3 and put back doc one
        assertTrue(!file.exists());
        assertTrue(!file3.exists());
        assertNull(store.getDocument(this.uri3));
        assertNotNull(store.getDocument(this.uri1));
        assertTrue(!file.exists());
        assertTrue(!file3.exists());
        assertTrue(!file2.exists());
        assertTrue(!file4.exists());

        store.undo();//delete doc2
        assertNull(store.getDocument(this.uri2));
        store.undo();//delete doc1
        assertNull(store.getDocument(this.uri1));
    }
    @Test
    public void memoryPutTxtTestThenUndoOverwrite2() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo1");
        int two = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo2");
        int three = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo3");
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        System.out.println("yo4");
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);
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
        assertTrue(!file3.exists());
        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        //lets overwrite doc4
        four = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        assertTrue(!file3.exists());
        assertTrue(!file4.exists());
        System.out.println("undo 4");
        store.undo();//undo the overwrite. both should be in memory still
        assertTrue(!file4.exists());
        assertTrue(!file3.exists());

        
        assertTrue(file.exists());
        one = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //should overwrite the uri1 and replace it in memory
        assertTrue(!file.exists());
        //doc 3 should be sent to disk
        assertTrue(file3.exists());
        System.out.println("here");
        //undo should bring both back (do1 and doc3) and push doc4 to disk BUT FOR SOME REASON DOC3 IS GETTING PUT TO DISK INSTEAD OF DOC4. RESLOVED
        store.undo();
        assertTrue(!file3.exists());
        assertTrue(file4.exists());

        store.undo(this.uri2);
        assertNull(store.getDocument(this.uri2));
        assertTrue(!file2.exists());
        store.undo(this.uri3);
        assertNull(store.getDocument(this.uri3));
        assertTrue(!file3.exists());
        store.undo(this.uri1);
        assertNull(store.getDocument(this.uri1));
        assertTrue(!file.exists());
        store.undo(this.uri4);
        assertNull(store.getDocument(this.uri4));
        assertTrue(!file4.exists());

        //Set<URI> urisdeleted = store.deleteAll("text");
    }

    @Test
    public void zeroLimitPutBinaryTest() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(0);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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

        assertTrue(store.deleteDocument(this.uri1));
        assertTrue(store.deleteDocument(this.uri2));
        assertTrue(store.deleteDocument(this.uri3));
        assertTrue(store.deleteDocument(this.uri4));
    }

    @Test
    public void zeroLimitPutBinaryTestThenUndo() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(0);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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

        store.undo();
        assertNull(store.getDocument(this.uri4));
        store.undo();
        assertNull(store.getDocument(this.uri3));
        store.undo();
        assertNull(store.getDocument(this.uri2));
        store.undo();
        assertNull(store.getDocument(this.uri1));
    }

    @Test
    public void zeroLimitPutBinaryTestThenUndoOverwrite() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(0);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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

        one = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        //should overwrite the uri1 that is on disk and replace it on disk because the store is at 0
        System.out.println("hashCodeB1: " + one);
        assertTrue(file.exists());

        store.undo(this.uri2);
        assertTrue(!file2.exists());
        assertNull(store.getDocument(this.uri2));

        store.undo();//undo the overwrite, it should be moved to disk
        assertTrue(file.exists());

        store.undo();
        assertTrue(!file4.exists());
        assertNull(store.getDocument(this.uri4));

        store.undo();
        assertTrue(!file3.exists());
        assertNull(store.getDocument(this.uri3));

        store.undo();
        assertTrue(!file.exists());
        assertNull(store.getDocument(this.uri1));
    }

    @Test
    public void regularPutByteTest() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(!file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(!file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        assertTrue(store.deleteDocument(this.uri1));
        assertTrue(store.deleteDocument(this.uri2));
        assertTrue(store.deleteDocument(this.uri3));
        assertTrue(store.deleteDocument(this.uri4));
    }

    @Test
    public void regularPutByteTestThenUndo() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(!file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(!file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        store.undo();
        assertNull(store.getDocument(this.uri4));
        store.undo();
        assertNull(store.getDocument(this.uri2));
        store.undo();
        assertNull(store.getDocument(this.uri3));   
        store.undo();
        assertNull(store.getDocument(this.uri1));
    }

    @Test
    public void regularPutByteTestThenUndoOverwrite() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(10);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

        String shavedURI = this.uri1.getRawSchemeSpecificPart().substring(2);
        String filePath = baseDir + File.separator + shavedURI + ".json";
        File file = new File(filePath);
        assertTrue(!file.exists());

        String shavedURI2 = this.uri2.getRawSchemeSpecificPart().substring(2);
        String filePath2 = baseDir + File.separator + shavedURI2 + ".json";
        File file2 = new File(filePath2);
        assertTrue(!file2.exists());

        String shavedURI3 = this.uri3.getRawSchemeSpecificPart().substring(2);
        String filePath3 = baseDir + File.separator + shavedURI3 + ".json";
        File file3 = new File(filePath3);
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        one = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        //should overwrite the uri1 and replace it in memory
        System.out.println("hashCode2: " + one);
        assertTrue(!file.exists());
        Document doc1 = store.getDocument(this.uri1);
        assertEquals(doc1.getDocumentBinaryData().length, this.txt2.getBytes().length);

        two = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        System.out.println("hashCode3: " + two);
        assertTrue(!file3.exists());

        Document doc3 = store.getDocument(this.uri3);
        assertEquals(doc3.getDocumentBinaryData().length, this.txt4.getBytes().length);

        store.undo();//undo the overwrite
        doc3 = store.getDocument(this.uri3);
        assertEquals(doc3.getDocumentBinaryData().length, this.txt3.getBytes().length);

        store.setMaxDocumentBytes(0);
        assertTrue(file.exists());
       
        assertTrue(file2.exists());

        assertTrue(file3.exists());

        assertTrue(file4.exists());

        store.undo();//undo the overwrite doc should still be on disk
        assertTrue(file.exists());
        doc1 = store.getDocument(this.uri1);
        assertEquals(doc1.getDocumentBinaryData().length, this.txt1.getBytes().length);
        assertTrue(file.exists());

        store.setMaxDocumentBytes(100);
        store.undo(this.uri2);
        assertNull(store.getDocument(this.uri2));
        assertTrue(!file2.exists());
        store.undo(this.uri3);
        assertNull(store.getDocument(this.uri3));
        assertTrue(!file3.exists());
        store.undo(this.uri1);
        assertNull(store.getDocument(this.uri1));
        assertTrue(!file.exists());
        store.undo(this.uri4);
        assertNull(store.getDocument(this.uri4));
        assertTrue(!file4.exists());
    }

    @Test
    public void memoryPutByteTest() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        assertTrue(store.deleteDocument(this.uri1));
        assertTrue(store.deleteDocument(this.uri2));
        assertTrue(store.deleteDocument(this.uri3));
        assertTrue(store.deleteDocument(this.uri4));
    }

    @Test
    public void memoryPutByteTestThenUndo() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.setMaxDocumentBytes(1000);
        int one = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        int three = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        int two = store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        int four = store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, one);
        assertEquals(0, two);
        assertEquals(0, three);
        assertEquals(0, four);

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
        assertTrue(!file3.exists());

        String shavedURI4 = this.uri4.getRawSchemeSpecificPart().substring(2);
        String filePath4 = baseDir + File.separator + shavedURI4 + ".json";
        File file4 = new File(filePath4);
        assertTrue(!file4.exists());

        store.undo();
        assertNull(store.getDocument(this.uri4));
        assertNotNull(store.getDocument(this.uri2));
        assertTrue(!file2.exists());
        assertTrue(file.exists());

        store.undo();
        assertNull(store.getDocument(this.uri3));
        assertNotNull(store.getDocument(this.uri1));
        assertTrue(!file.exists());

        store.undo();
        assertNull(store.getDocument(this.uri2));
        store.undo();
        assertNull(store.getDocument(this.uri1));

        assertTrue(!file.exists());

        assertTrue(!file2.exists());

        assertTrue(!file3.exists());

        assertTrue(!file4.exists());
    }
}
