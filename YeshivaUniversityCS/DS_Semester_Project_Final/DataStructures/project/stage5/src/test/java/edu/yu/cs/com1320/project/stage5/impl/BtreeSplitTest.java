package edu.yu.cs.com1320.project.stage5.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.DocumentStore;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class BtreeSplitTest {
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

    //variables to hold possible values for doc4
    private URI uri5;
    private String txt5;

    //variables to hold possible values for doc4
    private URI uri6;
    private String txt6;

    //variables to hold possible values for doc4
    private URI uri7;
    private String txt7;

    //variables to hold possible values for doc4
    private URI uri8;
    private String txt8;

    //variables to hold possible values for doc4
    private URI uri9;
    private String txt9;

    //variables to hold possible values for doc4
    private URI uri10;
    private String txt10;

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

        //init possible values for doc4
        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.txt5 = "This is the text of doc5";

        //init possible values for doc4
        this.uri6 = new URI("http://edu.yu.cs/com1320/project/doc6");
        this.txt6 = "This is the text of doc6";

        //init possible values for doc4
        this.uri7 = new URI("http://edu.yu.cs/com1320/project/doc7");
        this.txt7 = "This is the text of doc7";

        //init possible values for doc4
        this.uri8 = new URI("http://edu.yu.cs/com1320/project/doc8");
        this.txt8 = "This is the text of doc8";

        //init possible values for doc4
        this.uri9 = new URI("http://edu.yu.cs/com1320/project/doc9");
        this.txt9 = "This is the text of doc9";

        //init possible values for doc4
        this.uri10 = new URI("http://edu.yu.cs/com1320/project/doc10");
        this.txt10 = "This is the text of doc10";

        this.bytes1 = this.txt1.getBytes().length;
        this.bytes2 = this.txt2.getBytes().length;
        this.bytes3 = this.txt3.getBytes().length;
        this.bytes4 = this.txt4.getBytes().length;
    }
    //passed this test so I think my Btree is fine in addition to other tests
    @Test
    public void add10Docs() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(20);
        store.setMaxDocumentBytes(1000);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()),this.uri5, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt6.getBytes()),this.uri6, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt7.getBytes()),this.uri7, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt8.getBytes()),this.uri8, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt9.getBytes()),this.uri9, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt10.getBytes()),this.uri10, DocumentStore.DocumentFormat.TXT);

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
        String shavedURI5 = this.uri5.getRawSchemeSpecificPart().substring(2);
        String filePath5 = baseDir + File.separator + shavedURI5 + ".json";
        File file5 = new File(filePath5);
        assertTrue(!file5.exists());
        String shavedURI6 = this.uri6.getRawSchemeSpecificPart().substring(2);
        String filePath6 = baseDir + File.separator + shavedURI6 + ".json";
        File file6 = new File(filePath6);
        assertTrue(!file6.exists());
        String shavedURI7 = this.uri7.getRawSchemeSpecificPart().substring(2);
        String filePath7 = baseDir + File.separator + shavedURI7 + ".json";
        File file7 = new File(filePath7);
        assertTrue(!file7.exists());
        String shavedURI8 = this.uri8.getRawSchemeSpecificPart().substring(2);
        String filePath8 = baseDir + File.separator + shavedURI8 + ".json";
        File file8 = new File(filePath8);
        assertTrue(!file8.exists());
        String shavedURI9 = this.uri9.getRawSchemeSpecificPart().substring(2);
        String filePath9 = baseDir + File.separator + shavedURI9 + ".json";
        File file9 = new File(filePath9);
        assertTrue(!file9.exists());
        String shavedURI10 = this.uri10.getRawSchemeSpecificPart().substring(2);
        String filePath10 = baseDir + File.separator + shavedURI10 + ".json";
        File file10 = new File(filePath10);
        assertTrue(!file10.exists());

        store.setMaxDocumentBytes(0);
        assertTrue(file.exists());
        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertTrue(file4.exists());
        assertTrue(file5.exists());
        assertTrue(file6.exists());
        assertTrue(file7.exists());
        assertTrue(file8.exists());
        assertTrue(file9.exists());
        assertTrue(file10.exists());

        store.setMaxDocumentBytes(1000);
        store.search("text");
        assertTrue(!file.exists());
        assertTrue(!file2.exists());
        assertTrue(!file3.exists());
        assertTrue(!file4.exists());
        assertTrue(!file5.exists());
        assertTrue(!file6.exists());
        assertTrue(!file7.exists());
        assertTrue(!file8.exists());
        assertTrue(!file9.exists());
        assertTrue(!file10.exists());

        store.setMaxDocumentCount(0);
        assertTrue(file.exists());
        assertTrue(file2.exists());
        assertTrue(file3.exists());
        assertTrue(file4.exists());
        assertTrue(file5.exists());
        assertTrue(file6.exists());
        assertTrue(file7.exists());
        assertTrue(file8.exists());
        assertTrue(file9.exists());
        assertTrue(file10.exists());

        store.setMaxDocumentCount(10);
        store.search("text");
        assertTrue(!file.exists());
        assertTrue(!file2.exists());
        assertTrue(!file3.exists());
        assertTrue(!file4.exists());
        assertTrue(!file5.exists());
        assertTrue(!file6.exists());
        assertTrue(!file7.exists());
        assertTrue(!file8.exists());
        assertTrue(!file9.exists());
        assertTrue(!file10.exists());

        Set<URI> urisDeleted = store.deleteAll("text");
        assertEquals(10, urisDeleted.size());
        assertTrue(!file.exists());
        assertTrue(!file2.exists());
        assertTrue(!file3.exists());
        assertTrue(!file4.exists());
        assertTrue(!file5.exists());
        assertTrue(!file6.exists());
        assertTrue(!file7.exists());
        assertTrue(!file8.exists());
        assertTrue(!file9.exists());
        assertTrue(!file10.exists());
    }
}
