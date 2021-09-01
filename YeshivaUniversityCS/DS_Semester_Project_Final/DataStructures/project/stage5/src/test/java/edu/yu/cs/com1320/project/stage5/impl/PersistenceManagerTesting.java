package edu.yu.cs.com1320.project.stage5.impl;

//import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

public class PersistenceManagerTesting {

    @Test
    public void de_serializationTest() throws IllegalArgumentException, URISyntaxException, IOException {
        File baseDir = new File("C:\\Users\\yyb20\\OneDrive\\Desktop\\Temp");
        PersistenceManager<URI, Document> pm = new DocumentPersistenceManager(baseDir);
        URI uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        Document txtDoc = new DocumentImpl(uri1, " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
        //init possible values for doc3
        URI uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        String txt3 = "the text of doc3, this is";

        //init possible values for doc4
        URI uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        String txt4 = "This is the text of doc4";
        Document byteDoc1 = new DocumentImpl(uri3, txt3.getBytes());
        Document byteDoc2 = new DocumentImpl(uri4, txt4.getBytes());

        pm.serialize(uri1, txtDoc);
        pm.serialize(uri3, byteDoc1);
        pm.serialize(uri4, byteDoc2);

        Document text = pm.deserialize(uri1);
        Document byte1 = pm.deserialize(uri3);
        Document byte2 = pm.deserialize(uri4);

        System.out.println();
        System.out.println("URI: " + text.getKey());
        System.out.println("Content: " + text.getDocumentTxt());
        System.out.println("Map: " + text.getWordMap().toString());

        System.out.println();
        System.out.println("URI: " + byte1.getKey());
        System.out.println("Content: " + byte1.getDocumentBinaryData() + "   " + new String(byte1.getDocumentBinaryData()) );
        System.out.println("Map: " + byte1.getWordMap().toString());

        System.out.println();
        System.out.println("URI: " + byte2.getKey());
        System.out.println("Content: " + byte2.getDocumentBinaryData() + "   " + new String(byte2.getDocumentBinaryData()) );
        System.out.println("Map: " + byte2.getWordMap().toString() );

        assertEquals(uri1, text.getKey());
        assertEquals(txtDoc.getDocumentTxt(), text.getDocumentTxt());

        assertEquals(uri3, byte1.getKey());
        assertEquals(new String(byteDoc1.getDocumentBinaryData()), new String(byte1.getDocumentBinaryData()));

        assertEquals(uri4, byte2.getKey());
        assertEquals(new String(byteDoc2.getDocumentBinaryData()), new String(byte2.getDocumentBinaryData()));
    }

    // @Test
    // public void delete() throws IOException, URISyntaxException{
    //     File baseDir = new File("C:\\Users\\yyb20\\OneDrive\\Desktop\\Temp");
    //     PersistenceManager<URI, Document> pm = new DocumentPersistenceManager(baseDir);
    //     URI uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    //     Document txtDoc = new DocumentImpl(uri1, " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
    //     //init possible values for doc3
    //     URI uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
    //     String txt3 = "the text of doc3, this is";

    //     //init possible values for doc4
    //     URI uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
    //     String txt4 = "This is the text of doc4";
    //     Document byteDoc1 = new DocumentImpl(uri3, txt3.getBytes());
    //     Document byteDoc2 = new DocumentImpl(uri4, txt4.getBytes());

    //     pm.serialize(uri1, txtDoc);
    //     pm.serialize(uri3, byteDoc1);
    //     pm.serialize(uri4, byteDoc2);

    //     boolean d1 = pm.delete(uri1);
    //     assertTrue(d1);
    //     boolean d2 = pm.delete(uri3);
    //     assertTrue(d2);
    //     boolean d3 = pm.delete(uri4);
    //     assertTrue(d3);
    // }

    // @Test
    // public void serialize() throws IOException, URISyntaxException{
    //     File baseDir = new File("C:\\Users\\yyb20\\OneDrive\\Desktop\\Temp");
    //     PersistenceManager<URI, Document> pm = new DocumentPersistenceManager(baseDir);
    //     URI uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    //     Document txtDoc = new DocumentImpl(uri1, " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
    //     //init possible values for doc3
    //     URI uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
    //     String txt3 = "the text of doc3, this is";

    //     //init possible values for doc4
    //     URI uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
    //     String txt4 = "This is the text of doc4";
    //     Document byteDoc1 = new DocumentImpl(uri3, txt3.getBytes());
    //     Document byteDoc2 = new DocumentImpl(uri4, txt4.getBytes());

    //     pm.serialize(uri1, txtDoc);
    //     pm.serialize(uri3, byteDoc1);
    //     pm.serialize(uri4, byteDoc2);
    //     assertTrue(true);
    // }
    
    // @Test
    // public void deserialize() throws IOException, URISyntaxException{
    //     File baseDir = new File("C:\\Users\\yyb20\\OneDrive\\Desktop\\Temp");
    //     PersistenceManager<URI, Document> pm = new DocumentPersistenceManager(baseDir);
    //     URI uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    //     Document txtDoc = new DocumentImpl(uri1, " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
    //     //init possible values for doc3
    //     URI uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
    //     String txt3 = "the text of doc3, this is";

    //     //init possible values for doc4
    //     URI uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
    //     String txt4 = "This is the text of doc4";
    //     Document byteDoc1 = new DocumentImpl(uri3, txt3.getBytes());
    //     Document byteDoc2 = new DocumentImpl(uri4, txt4.getBytes());

    //     Document text = pm.deserialize(uri1);
    //     Document byte1 = pm.deserialize(uri3);
    //     Document byte2 = pm.deserialize(uri4);

    //     System.out.println();
    //     System.out.println("URI: " + text.getKey());
    //     System.out.println("Content: " + text.getDocumentTxt());
    //     System.out.println("Map: " + text.getWordMap().toString());

    //     System.out.println();
    //     System.out.println("URI: " + byte1.getKey());
    //     System.out.println("Content: " + byte1.getDocumentBinaryData() + "   " + new String(byte1.getDocumentBinaryData()) );
    //     System.out.println("Map: " + byte1.getWordMap().toString());

    //     System.out.println();
    //     System.out.println("URI: " + byte2.getKey());
    //     System.out.println("Content: " + byte2.getDocumentBinaryData() + "   " + new String(byte2.getDocumentBinaryData()) );
    //     System.out.println("Map: " + byte2.getWordMap().toString() );

    //     assertEquals(uri1, text.getKey());
    //     assertEquals(txtDoc.getDocumentTxt(), text.getDocumentTxt());

    //     assertEquals(uri3, byte1.getKey());
    //     assertEquals(new String(byteDoc1.getDocumentBinaryData()), new String(byte1.getDocumentBinaryData()));

    //     assertEquals(uri4, byte2.getKey());
    //     assertEquals(new String(byteDoc2.getDocumentBinaryData()), new String(byte2.getDocumentBinaryData()));
    // }

}
