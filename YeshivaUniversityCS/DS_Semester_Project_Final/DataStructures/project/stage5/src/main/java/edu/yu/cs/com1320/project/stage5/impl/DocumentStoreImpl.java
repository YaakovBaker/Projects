package edu.yu.cs.com1320.project.stage5.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

public class DocumentStoreImpl implements DocumentStore{

    class ImpastaDoc implements Comparable<ImpastaDoc>{
        private URI uri;
        private boolean onDisk;

        ImpastaDoc(URI uri){
            this.uri = uri;
            this.onDisk = false;
        }
        URI getURI(){
            return this.uri;
        }
        boolean isOnDisk(){
            return this.onDisk;
        }
        void setInMem(){
            this.onDisk = false;
        }
        void setOnDisk(){
            this.onDisk = true;
        }
        @Override
        public int compareTo(ImpastaDoc o) {
            Document doc = storageSystem.get(uri);
            Document otherDoc = storageSystem.get(o.getURI());
            if( doc.getLastUseTime() > otherDoc.getLastUseTime() ){
                return 1;
            }else if( doc.getLastUseTime() < otherDoc.getLastUseTime() ){
                return -1;
            }else{
                return 0;
            }
        }

        @Override
        public boolean equals(Object other){
            if (other == null){
                return false;
            }
            return this.hashCode() == other.hashCode();
        }
         
        @Override
        public int hashCode(){
            Document doc = storageSystem.get(uri);
            int result = this.uri.hashCode();
            result = 31 * result + (doc.getDocumentTxt() != null ? doc.getDocumentTxt().hashCode() : 0);
            result = 31 * result + Arrays.hashCode(doc.getDocumentBinaryData());
            return result;
        }
    } 
    
    private BTree<URI, Document> storageSystem;
    private Stack<Undoable> commandStack;
    private Trie<URI> wordSearcher;
    private MinHeap<ImpastaDoc> usageTimeMinHeap;
    private Map<URI,ImpastaDoc> refrencing;
    
    //I'll set the limits to zero and do logic tests based on that. if 0 then that means nothing can happen yet until the thing is set
    private int documentCountLimit;
    private int documentByteLimit;
    private int documentCount;
    private int documentBytes;

    DocumentStoreImpl(){
        this(new File(System.getProperty("user.dir")));
    }

    DocumentStoreImpl(File baseDir){
        this.storageSystem = new BTreeImpl<>();
        this.storageSystem.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        this.commandStack = new StackImpl<>();
        this.usageTimeMinHeap = new MinHeapImpl<>();
        this.wordSearcher = new TrieImpl<>();
        this.refrencing = new HashMap<>();

        documentCountLimit = Integer.MAX_VALUE;
        documentByteLimit = Integer.MAX_VALUE;
        documentCount = 0;
        documentBytes = 0;
    }
    
    /**
    * The Limit Methods
    */
    @Override
    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    public void setMaxDocumentCount(int limit) {//may need to change up some logic but this is the general gist
        if( limit < 0 ){
            throw new IllegalArgumentException("Can't have a negative limit");
        }
        this.documentCountLimit = limit;
        while( this.documentCount > this.documentCountLimit ){
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){//if not null its a txt
                //move it to disk
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
            }else{//its binary
                //move it to disk
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
            }
        }
    }

    @Override
    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit) {//may need to change up some logic but this is the general gist
        if( limit < 0 ){
            throw new IllegalArgumentException("Can't have a negative limit");
        }
        this.documentByteLimit = limit;
        while( this.documentBytes > this.documentByteLimit ){
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){//if not null its a txt
                //move it to disk
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
            }else{//its binary
                //move it to disk
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
            }
        }
    }


    /*
    * The Search Methods
    */
    @Override
    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri) {
        if( uri == null ){
            throw new IllegalArgumentException("Uri was null"); 
        }
        Document doc = null;
        boolean inRefrence = this.refrencing.containsKey(uri);
        if( (this.documentByteLimit == 0 || this.documentCountLimit == 0) && (inRefrence) ){
            ImpastaDoc ref = this.refrencing.get(uri);
            if(ref != null && ref.isOnDisk()){//if its on disk deserialize it then save it, serialize it and return
                doc = this.storageSystem.get(uri);
                doc.setLastUseTime(java.lang.System.nanoTime());
                try {
                    this.storageSystem.moveToDisk(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if( inRefrence ){//bringing this doc into the store exceeds limits put others to the disk or it is already in memory
            return getDoc(uri);
        }
        return doc;
    }

    private Document getDoc(URI uri){
        Document doc = null;
        ImpastaDoc ref = this.refrencing.get(uri);
        if(ref != null && ref.isOnDisk()){
            doc = this.storageSystem.get(uri);//get it and deserialize
            doc.setLastUseTime(java.lang.System.nanoTime());
            ref.setInMem();
            //check our limits if we need to move others to disk
            if( doc.getDocumentTxt() != null ){
                limitChecker(doc.getDocumentTxt().getBytes().length);
            }else{
                limitChecker(doc.getDocumentBinaryData().length);
            }
            this.usageTimeMinHeap.insert(ref);
        }else{//in memory
            doc = this.storageSystem.get(uri);
            doc.setLastUseTime(java.lang.System.nanoTime());
            this.usageTimeMinHeap.reHeapify(ref);
        }
        return doc;
    }

    private void limitChecker(int byteLength){
        //if bringing this doc back into memory exceeds limits then we move others to disk
        while( (this.documentBytes + byteLength > this.documentByteLimit) || (this.documentCount + 1 > this.documentCountLimit) ){
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
            }else{
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
            }
        }
    }

    @Override
    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword) {
        if( keyword == null ){
            throw new IllegalArgumentException("Passed in a null value for a searc");
        }
        String key = modifyTxt(keyword);
        List<URI> uris = this.wordSearcher.getAllSorted(key, (URI uri1, URI uri2) -> this.storageSystem.get(uri2).wordCount(keyword) -  this.storageSystem.get(uri1).wordCount(keyword));
        List<Document> docs = new ArrayList<>();
        long time = java.lang.System.nanoTime();
        for( URI uri : uris){
            Document doc = this.storageSystem.get(uri);
            docs.add(doc);
            doc.setLastUseTime(time);
        }
        if( (this.documentByteLimit == 0 || this.documentCountLimit == 0) ){
            for( URI uri : uris ){
                try {
                    this.storageSystem.moveToDisk(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{//limits are not at zero, lets add all to memory then decide what to put back to disk after
            memManagementSearch(docs);
        }
        return docs;
    }

    /**
     * managing the memory for the search
     * @param docs
     */
    private void memManagementSearch(List<Document> docs){
        for( Document doc : docs ){
            URI uri = doc.getKey();
            ImpastaDoc ref = this.refrencing.get(uri);
            if( ref != null && ref.isOnDisk() ){
                ref.setInMem();
                this.usageTimeMinHeap.insert(ref);
                raiseCount(doc.getDocumentTxt().getBytes().length);
            }else{
                this.usageTimeMinHeap.reHeapify(ref);
            }
        }
        //now after adding or reheapifying all docs, we now have to check our limits and move docs to disk based on that
        limitChecker();
    }

    

    @Override
    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix) {
        if( keywordPrefix == null ){
            throw new IllegalArgumentException("Passed in a null value for a prefix search");
        }
        String keyPre = modifyTxt(keywordPrefix);
        List<URI> uris = this.wordSearcher.getAllWithPrefixSorted(keyPre, (URI uri1, URI uri2) -> prefixCount(keyPre, this.storageSystem.get(uri2)) - prefixCount(keyPre, this.storageSystem.get(uri1)));
        List<Document> docs = new ArrayList<>();
        long time = java.lang.System.nanoTime();
        for( URI uri : uris ){
            Document doc = this.storageSystem.get(uri);
            docs.add(doc);
            doc.setLastUseTime(time);
        }
        if( (this.documentByteLimit == 0 || this.documentCountLimit == 0) ){
            for( URI uri : uris ){
                try {
                    this.storageSystem.moveToDisk(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            memManagementSearch(docs);
        }
        return docs;
    }

    /*
    * Prefix counter method for searchByPrefix
    */
    private int prefixCount(String prefix, Document doc){
        int count = 0;
        int prefixlength = prefix.length();
        String modifiedTxt = modifyTxt(doc.getDocumentTxt());
        String[] stringIteration = modifiedTxt.split("\\s");
        for( String word : stringIteration){
            if(word.length() < prefixlength){
                continue;
            }
            String pre = word.substring(0, prefixlength);
            if( pre.equals(prefix) ){
                count++;
            }
        }
        return count;
    }


    /*
    * The put Documents and related methods
    */
    @Override
    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. 
     * If there is a previous doc, return the hashCode of the previous doc. 
     * If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc 
     * or 0 if there is no doc to delete.
     */
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if( uri == null || format == null ){
            throw new IllegalArgumentException();
        }
        if(input == null ){
            Document returnedDoc = this.storageSystem.get(uri);
            if( deleteDocument(uri) ){
                return returnedDoc.hashCode();
            }
        }else{
            byte[] byteArray = input.readAllBytes();
            if( format == DocumentFormat.TXT ){
                return putTxtDoc(uri, byteArray);
            }else if( format == DocumentFormat.BINARY){
                return putBinaryDoc(uri, byteArray);
            }
        }
        return 0;
    }
    
    /*
    *   private method for putting a txtDocument in the DocStore
    */
    private int putTxtDoc(URI uri, byte[] byteArray){
        //create the text document
        Document txtDocument = new DocumentImpl(uri, new String(byteArray));
        //check our limits regarding adding this document
        Set<URI> movedToDisk = limitChecker(txtDocument, uri);
        if( movedToDisk == null ){//if null then put it and have it go straight to memory because we have no space in memory
            return zeroLimitPutTxT(uri, txtDocument, new HashSet<>());
        }else if( movedToDisk.isEmpty() ){//empty meaning a regular put
            return regularPutTxT(uri, txtDocument, movedToDisk);
        }else if ( !movedToDisk.isEmpty() ){//if it conatins elements then we got to add them to undo logic
            return memoryPutTxt(uri, txtDocument, movedToDisk);
        }
        return 0;
    }

    private Set<URI> limitChecker(Document txtDocument, URI uri){
        int byteLength = txtDocument.getDocumentTxt().getBytes().length;
        if(this.documentByteLimit == 0 || this.documentCountLimit == 0){
            return null;//signifies to the put method that this doc must go straight to disk
        }
        if( this.refrencing.containsKey(uri) && !this.refrencing.get(uri).isOnDisk()){//if this doc is already in mem and this is obviously an overwrite
            //need to check limits of replacing the document
            return limitChecker2(txtDocument, uri);
        }
        Set<URI> movedToDisk = new HashSet<>();
        while( (this.documentBytes + byteLength > this.documentByteLimit) || (this.documentCount + 1 > this.documentCountLimit) ){
            //remove the least recently used document in heap to make space for the new one.
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }else if( toDiskDoc.getDocumentTxt() == null){
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }
        }
        return movedToDisk;//returning here means proceed with a regular put if empty otherwise we have things for disk movement
    }

    private Set<URI> limitChecker2(Document txtDocument, URI uri){
        Set<URI> movedToDisk = new HashSet<>();
        //checks if the docCount is greater then the docCount limit already-which it wont be
        //needs to check if removing the previous docs bytes and adding this ones 
        Document previous = this.storageSystem.get(uri);
        //remove the old docs bytes from store and add this docs then check it we are over the byte limits
        int preBCount = previous.getDocumentTxt().getBytes().length;
        int currentBCount = txtDocument.getDocumentTxt().getBytes().length;
        this.documentBytes = this.documentBytes - preBCount + currentBCount;
        while( (this.documentBytes > this.documentByteLimit) || (this.documentCount > this.documentCountLimit) ){
            //remove the least recently used document in heap to make space for the new one.
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }else if( toDiskDoc.getDocumentTxt() == null){
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }
        }
        //before continuing lets reset the document bytes of the store based on this doc and what will be overriden as this will conflict with the count later
        this.documentBytes = this.documentBytes + preBCount - currentBCount;
        return movedToDisk;
    }

    /**
     * put logic where this document must go straight to disk
     * @param uri
     * @param txtDocument
     * @param movedToDisk
     * @return
     */
    private int zeroLimitPutTxT(URI uri, Document txtDocument, Set<URI> movedToDisk){
        Document returnedDoc = this.storageSystem.put(uri, txtDocument);
        if( returnedDoc != null ){//if what was returned is not null we should delete it from the other data structures
            megaDeleteTxT(returnedDoc);
        }
        txtDocument.setLastUseTime(java.lang.System.nanoTime());
        megaInsertionTxT(txtDocument);//load this document into the other data structures
        if( returnedDoc != null ){ 
            overwriteUndoTxT(uri, returnedDoc, txtDocument, movedToDisk);
            //move it to disk
            moveTxTToDisk(uri, txtDocument);
            return returnedDoc.hashCode();
        }
        //here means returnedDoc was null meaning it was a new first entry. undo logic is to remove txtDocument from the data structures
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            megaDeleteTxT(txtDocument);
            this.storageSystem.put(uri, null);
            return true; }) );
        //move it to disk
        moveTxTToDisk(uri, txtDocument);
        return 0;
    }

    private void moveTxTToDisk(URI uri, Document txtDocument){
        txtDocument.setLastUseTime(1);
        ImpastaDoc ref = this.usageTimeMinHeap.remove();
        ref.setOnDisk();
        try {
            this.storageSystem.moveToDisk(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lowerCount(txtDocument.getDocumentTxt().getBytes().length);
    }

    /**
     * regular put logic
     * @param uri
     * @param txtDocument
     * @param movedToDisk
     * @return
     */
    private int regularPutTxT(URI uri, Document txtDocument, Set<URI> movedToDisk){
        Document returnedDoc = this.storageSystem.put(uri, txtDocument);
        if( returnedDoc != null ){//if what was returned is not null we should delete it from the other data structures
            megaDeleteTxT(returnedDoc);
        }
        txtDocument.setLastUseTime(java.lang.System.nanoTime());
        megaInsertionTxT(txtDocument);//load this document into the other data structures
        // when returned doc doesn't equal null that means this was an overrite and the undo logic must be for an overrite
        if( returnedDoc != null ){ 
            overwriteUndoTxT(uri, returnedDoc, txtDocument, movedToDisk);
            return returnedDoc.hashCode();
        }
        //here means returnedDoc was null meaning it was a new first entry. undo logic is to remove txtDocument from the data structures
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            megaDeleteTxT(txtDocument);
            this.storageSystem.put(uri, null);
            return true; }) );
        return 0;

    }

    /**
     * put logic for when things got moved to disk because of this put and we need to bring back to memory in undo
     * @param uri
     * @param txtDocument
     * @param movedToDisk
     * @return
     */
    private int memoryPutTxt(URI uri, Document txtDocument, Set<URI> movedToDisk){
        Document returnedDoc = this.storageSystem.put(uri, txtDocument);
        if( returnedDoc != null ){//if what was returned is not null we should delete it from the other data structures
            megaDeleteTxT(returnedDoc);
        }
        txtDocument.setLastUseTime(java.lang.System.nanoTime());
        megaInsertionTxT(txtDocument);//load this document into the other data structures
        // when returned doc doesn't equal null that means this was an overrite and the undo logic must be for an overrite
        if( returnedDoc != null ){
            overwriteUndoTxT(uri, returnedDoc, txtDocument, movedToDisk);
            return returnedDoc.hashCode();
        }
        //here means returnedDoc was null meaning it was a new first entry. undo logic is to remove txtDocument from the data structures
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            megaDeleteTxT(txtDocument);
            this.storageSystem.put(uri, null);
            long time = java.lang.System.nanoTime();
            for( URI key: movedToDisk ){
                ImpastaDoc ref = this.refrencing.get(key);
                if( ref != null && ref.isOnDisk() ){//if this document is still on disk deserilize it
                    Document doc = this.storageSystem.get(key);
                    doc.setLastUseTime(time);
                    ref.setInMem();
                    this.usageTimeMinHeap.insert(ref);
                    raiseCount(doc.getDocumentTxt().getBytes().length);
                }
            }
            return true; }) );
        return 0;
    }

    /**
     * the overwrite undo logic in a method
     * may need to add limit checker logic
     * @param uri
     * @param returnedDoc
     * @param txtDocument
     * @return
     */
    private void overwriteUndoTxT(URI uri, Document returnedDoc, Document txtDocument, Set<URI> movedToDisk){
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            this.storageSystem.put(uri, returnedDoc);//return the old doc
            megaDeleteTxT(txtDocument);
            long time = java.lang.System.nanoTime();
            returnedDoc.setLastUseTime(time);
            megaInsertionTxT(returnedDoc);
            if( !movedToDisk.isEmpty() ){
                for( URI key: movedToDisk){
                    ImpastaDoc ref = this.refrencing.get(key);
                    if( ref != null && ref.isOnDisk() ){//if this document is still on disk deserilize it
                        Document doc = this.storageSystem.get(key);
                        doc.setLastUseTime(time);
                        ref.setInMem();
                        this.usageTimeMinHeap.insert(ref);
                        this.usageTimeMinHeap.reHeapify(ref);
                        raiseCount(doc.getDocumentTxt().getBytes().length);
                    }
                }
            }
            limitChecker();
            return true; }) );
    }
    
    /**
     * an Insertion for lambda and other useful places for loading a Trie, and inserting into a heap and raising count
     * @param doc
     */
    private void megaInsertionTxT(Document doc){
        loadTrie(doc);
        //need to make the impasta doc and place it in the minheap and a refrencing map
        URI key = doc.getKey();
        ImpastaDoc impostor = new ImpastaDoc(key);
        impostor.setInMem();
        this.refrencing.put(key, impostor);
        this.usageTimeMinHeap.insert(impostor);
        raiseCount(doc.getDocumentTxt().getBytes().length);
    }

    /*
     *   private method for loading document into Trie
     */
    private void loadTrie(Document txtDocument){
        Set<String> words = txtDocument.getWords();
        URI key = txtDocument.getKey();
        for( String word : words){//trie now stores the uri with those words
            this.wordSearcher.put(word, key);
        }
    }

    /**
     * Beginning of Put For Byte Doc
     */

    /*
     * private method for putting a binaryDocument in the DocStore
     */
    private int putBinaryDoc(URI uri, byte[] byteArray){
        //create the document
        Document byteDocument = new DocumentImpl(uri, byteArray);
        //check our limits regarding adding this document, if true then do the regular put
        Set<URI> movedToDisk = limitCheckerB(byteDocument, uri);
        if( movedToDisk == null ){//if null then put it and have it go straight to disk because we have no space in memory
            return zeroLimitPutBinary(uri, byteDocument, new HashSet<>());
        }else if( movedToDisk.isEmpty() ){//empty meaning a regular put
            return regularPutByte(uri, byteDocument, movedToDisk);
        }else if ( !movedToDisk.isEmpty() ){//if it conatins elements then we got to add them to undo logic
            return memoryPutByte(uri, byteDocument, movedToDisk);
        }
        return 0;
    }

    private Set<URI> limitCheckerB(Document byteDocument, URI uri){
        int byteLength = byteDocument.getDocumentBinaryData().length;
        if(this.documentByteLimit == 0 || this.documentCountLimit == 0){
            return null;//signifies to the put method that this doc must go straight to disk
        }
        if( this.refrencing.containsKey(uri) && !this.refrencing.get(uri).isOnDisk()){//if this doc is already in mem and this is obviously an overwrite
            //need to check limits of replacing the document
            return limitCheckerB2(byteDocument, uri);
        }
        Set<URI> movedToDisk = new HashSet<>();
        while( (this.documentBytes + byteLength > this.documentByteLimit) || (this.documentCount + 1 > this.documentCountLimit) ){
            //remove the least recently used document in heap to make space for the new one.
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }else if( toDiskDoc.getDocumentTxt() == null){
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }
        }
        return movedToDisk;//returning here means proceed with a regular put if empty otherwise we have things for disk movement
    }

    private Set<URI> limitCheckerB2(Document byteDocument, URI uri){
        Set<URI> movedToDisk = new HashSet<>();
        //checks if the docCount is greater then the docCount limit already-which it wont be
        //needs to check if removing the previous docs bytes and adding this ones 
        Document previous = this.storageSystem.get(uri);
        //remove the old docs bytes from store and add this docs then check it we are over the byte limits
        int preBCount = previous.getDocumentBinaryData().length;
        int currentBCount = byteDocument.getDocumentBinaryData().length;
        this.documentBytes = this.documentBytes - preBCount + currentBCount;
        while( (this.documentBytes > this.documentByteLimit) || (this.documentCount > this.documentCountLimit) ){
            //remove the least recently used document in heap to make space for the new one.
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }else if( toDiskDoc.getDocumentTxt() == null){
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
                movedToDisk.add(toDiskURI);
            }
        }
        //before continuing lets reset the document bytes of the store based on this doc and what will be overriden as this will conflict with the count later
        this.documentBytes = this.documentBytes + preBCount - currentBCount;
        return movedToDisk;
    }



    /**
     * put logic where this document must go straight to disk
     * @param uri
     * @param byteDocument
     * @param movedToDisk
     * @return
     */
    private int zeroLimitPutBinary(URI uri, Document byteDocument, Set<URI> movedToDisk){
        Document returnedDoc = this.storageSystem.put(uri, byteDocument);
        if( returnedDoc != null ){
            megaDeleteByte(returnedDoc);
        }
        byteDocument.setLastUseTime(java.lang.System.nanoTime());
        megaInsertionByte(byteDocument);
        //when returned doc doesn't equal null that means this was an overrite and the undo logic must be for an overrite
        if( returnedDoc != null ){
            overwriteUndoByte(uri, returnedDoc, byteDocument, movedToDisk);
            //move it to disk
            moveBToDisk(uri, byteDocument);
            return returnedDoc.hashCode();
        }
        //here means returnedDoc was null meaning it was a new first entry. undo logic is to remove it from the hashTable
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            megaDeleteByte(byteDocument);
            this.storageSystem.put(uri, null);
            return true; } ) );
        //move it to disk
        moveBToDisk(uri, byteDocument);
        return 0;
    }

    private void moveBToDisk(URI uri, Document byteDocument){
        byteDocument.setLastUseTime(1);
        ImpastaDoc ref = this.usageTimeMinHeap.remove();
        ref.setOnDisk();
        try {
            this.storageSystem.moveToDisk(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lowerCount(byteDocument.getDocumentBinaryData().length);
    }

    /**
     * put logic for a regular put
     * @param uri
     * @param byteDocument
     * @param movedToDisk
     * @return
     */
    private int regularPutByte(URI uri, Document byteDocument, Set<URI> movedToDisk){
        Document returnedDoc = this.storageSystem.put(uri, byteDocument);
        if( returnedDoc != null ){
            megaDeleteByte(returnedDoc);
        }
        byteDocument.setLastUseTime(java.lang.System.nanoTime());
        megaInsertionByte(byteDocument);
        if( returnedDoc != null ){
            overwriteUndoByte(uri, returnedDoc, byteDocument, movedToDisk);
            return returnedDoc.hashCode();
        }
        //here means returnedDoc was null meaning it was a new first entry. undo logic is to remove it from the hashTable
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            megaDeleteByte(byteDocument);
            this.storageSystem.put(uri, null);
            return true; } ) );
        return 0;
    }

    /**
     * put logic for when things were moved to disk because of this put and we need undo logic to bring them into memory
     * @param uri
     * @param txtDocument
     * @param movedToDisk
     * @return
     */
    private int memoryPutByte(URI uri, Document byteDocument, Set<URI> movedToDisk){
        Document returnedDoc = this.storageSystem.put(uri, byteDocument);
        if( returnedDoc != null ){
            megaDeleteByte(returnedDoc);
        }
        byteDocument.setLastUseTime(java.lang.System.nanoTime());
        megaInsertionByte(byteDocument);
        if( returnedDoc != null ){
            overwriteUndoByte(uri, returnedDoc, byteDocument, movedToDisk);
            return returnedDoc.hashCode();
        }
        //here means returnedDoc was null meaning it was a new first entry. undo logic is to remove it from the hashTable
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            megaDeleteByte(byteDocument);
            this.storageSystem.put(uri, null);
            long time = java.lang.System.nanoTime();
            for( URI key: movedToDisk){
                ImpastaDoc ref = this.refrencing.get(key);
                if( ref != null && ref.isOnDisk() ){//if this document is still on disk deserilize it
                    Document doc = this.storageSystem.get(key);
                    doc.setLastUseTime(time);
                    ref.setInMem();
                    this.usageTimeMinHeap.insert(ref);
                    raiseCount(doc.getDocumentBinaryData().length);
                }
            }
            return true; } ) );
        return 0;
    }

    /**
     * overwrite undo logic for Byte documents
     * @param uri
     * @param returnedDoc
     * @param byteDocument
     * @param movedToDisk
     */
    private void overwriteUndoByte(URI uri, Document returnedDoc, Document byteDocument, Set<URI> movedToDisk){
        this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
            this.storageSystem.put(uri, returnedDoc);
            megaDeleteByte(byteDocument);
            long time = java.lang.System.nanoTime();
            returnedDoc.setLastUseTime(time);
            megaInsertionByte(returnedDoc);
            if( !movedToDisk.isEmpty() ){
                for( URI key: movedToDisk){
                    ImpastaDoc ref = this.refrencing.get(key);
                    if( ref != null && ref.isOnDisk() ){//if this document is still on disk deserilize it
                        Document doc = this.storageSystem.get(key);
                        doc.setLastUseTime(time);
                        ref.setInMem();
                        this.usageTimeMinHeap.insert(ref);
                        this.usageTimeMinHeap.reHeapify(ref);
                        raiseCount(doc.getDocumentBinaryData().length);
                    }
                }
            }
            limitChecker();
            return true; } ) );
    }


    /**
     * an Insertion for lambda and other useful places for inserting into a heap, refrencing map, and raising count
     * @param doc
     */
    private void megaInsertionByte(Document doc){
        //need to make the impasta doc and place it in the minheap and a refrencing map
        URI key = doc.getKey();
        ImpastaDoc impostor = new ImpastaDoc(key);
        this.refrencing.put(key, impostor);
        this.usageTimeMinHeap.insert(impostor);
        raiseCount(doc.getDocumentBinaryData().length);
    }


    /*
    * The Delete methods
    */
    @Override
    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean deleteDocument(URI uri) {
        if( uri == null ){
            throw new IllegalArgumentException(); 
        }
        Document returnedDoc = this.storageSystem.get(uri);
        if( returnedDoc != null ){
            if( returnedDoc.getDocumentTxt() != null ){
                megaDeleteTxT(returnedDoc);
                this.storageSystem.put(uri, null);
                this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
                    this.storageSystem.put(uri, returnedDoc);
                    returnedDoc.setLastUseTime(java.lang.System.nanoTime());
                    megaInsertionTxT(returnedDoc);
                    limitChecker();
                    return true; }) );
            }else{
                megaDeleteByte(returnedDoc);
                this.storageSystem.put(uri, null);
                this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> {
                    this.storageSystem.put(uri, returnedDoc);
                    returnedDoc.setLastUseTime(java.lang.System.nanoTime());
                    megaInsertionByte(returnedDoc);
                    limitChecker();
                    return true; }) );
            }
            return true;
        }else{
            this.commandStack.push(new GenericCommand<URI>(uri, undoUri -> true) );
            return false;
        }
    }

    @Override
    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAll(String keyword) {
        if( keyword == null ){
            throw new IllegalArgumentException("Passed in a null value for a word deleteAll");
        }
        Set<URI> returnedUris = this.wordSearcher.deleteAll(keyword);
        CommandSet<URI> comSet = new CommandSet<>();
        long time = java.lang.System.nanoTime();
        for( URI uri : returnedUris ){
            Document deletedDoc = this.storageSystem.get(uri);
            megaDeleteTxT(deletedDoc);
            this.storageSystem.put(uri, null);
            comSet.addCommand(new GenericCommand<>(uri, undoUri -> {
                this.storageSystem.put(uri, deletedDoc);
                deletedDoc.setLastUseTime(time);
                megaInsertionTxT(deletedDoc);
                limitChecker();
                return true;
            }) );
        }
        this.commandStack.push(comSet);
        return returnedUris;
    }

    @Override
    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        if( keywordPrefix == null ){
            throw new IllegalArgumentException("Passed in a null value prefix deleteAll");
        }
        Set<URI> returnedUris = this.wordSearcher.deleteAllWithPrefix(keywordPrefix);
        CommandSet<URI> comSet = new CommandSet<>();
        long time = java.lang.System.nanoTime();
        for( URI uri : returnedUris ){
            Document deletedDoc = this.storageSystem.get(uri);
            megaDeleteTxT(deletedDoc);
            this.storageSystem.put(uri, null);
            comSet.addCommand(new GenericCommand<>(uri, undoUri -> {
                this.storageSystem.put(uri, deletedDoc);
                deletedDoc.setLastUseTime(time);
                megaInsertionTxT(deletedDoc);
                limitChecker();
                return true;
            }) );
        }
        this.commandStack.push(comSet);
        return returnedUris;
    }

    /**
     * a Delete for lambda and other useful places for deleting from Trie, and Heap and then lowering count
     * @param doc
     */
    private void megaDeleteTxT(Document doc){
        URI key = doc.getKey();
        deleteDocumentForTrie(doc);
        ImpastaDoc ref = this.refrencing.get(key);
        if( ref != null && ref.isOnDisk() ){
            ref.setInMem();
        }else{
            heapDelete(doc, ref);
            lowerCount(doc.getDocumentTxt().getBytes().length);
        }
        //delete ref from refrencing because we deleting this doc from store. it will be remade when undo happens
        this.refrencing.remove(key);
    }

    /**
     * Deleting a specific document from the heap. The way to do this is to get it to the least used one by "cheating the system" do this by setting time to a low number
     * then by calling MinHeap.remove that one will be the one removed
     * @param doc to be deleted from the minHeap
     */
    private void heapDelete(Document doc, ImpastaDoc ref){
        doc.setLastUseTime(1);
        this.usageTimeMinHeap.reHeapify(ref);
        this.usageTimeMinHeap.remove();
    }

    /*
    *   private method for deleting a document in Trie through a lambda function - change to a regular as this is used in many places
    */
    private void deleteDocumentForTrie(Document doc){
        URI key = doc.getKey();
        for( String word : doc.getWords()){
            this.wordSearcher.delete(word, key);
        }
    }

    private void megaDeleteByte(Document doc){
        URI key = doc.getKey();
        ImpastaDoc ref = this.refrencing.get(key);
        if( ref != null && ref.isOnDisk() ){
            ref.setInMem();
        }else{
            heapDelete(doc, ref);
            lowerCount(doc.getDocumentBinaryData().length);
        }
        //delete ref from refrencing because we deleting this doc from store. it will be remade when undo happens
        this.refrencing.remove(key);
    }


    /*
    * The Undo methods and ones related
    */
    @Override
    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    public void undo() throws IllegalStateException {
        if( this.commandStack.size() == 0 ){
            throw new IllegalStateException("Command Stack is Empty");
        }
        Undoable cmndundo = this.commandStack.pop();
        cmndundo.undo();
    }

    @Override
    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException {
        if( uri == null ){
            throw new IllegalArgumentException("Uri is null"); 
        }
        if( this.commandStack.size() == 0 ){
            throw new IllegalStateException("Command Stack is Empty");
        }
        Stack<Undoable> tempCmndStack = new StackImpl<>();
        boolean foundUri = undo(tempCmndStack, false, uri);
        commandStackReload(tempCmndStack);
        if( !foundUri ){
            throw new IllegalStateException("404: Uri was not found");
        }
    }

    /**
     * 
     * @param tempCmndStack
     * @param foundUri
     * @param uri
     * @return
     */
    private boolean undo(Stack<Undoable> tempCmndStack, boolean foundUri, URI uri ){
        Undoable cmnd = null;
        int cmndSize = this.commandStack.size();
        for( int i = 0; i < cmndSize; i++ ){
            cmnd = this.commandStack.pop();
            if( cmnd instanceof GenericCommand<?> ){
                GenericCommand<URI> genCmnd = (GenericCommand<URI>)cmnd;
                if( genCmnd.getTarget().equals(uri) ){
                    foundUri = genCmnd.undo();
                    break;
                }
            }else if( cmnd instanceof CommandSet<?> ){
                CommandSet<URI> cmndSet = (CommandSet<URI>)cmnd;
                if( cmndSet.containsTarget(uri) ){
                    foundUri = cmndSet.undo(uri);
                    if( cmndSet.size() != 0 ){
                        tempCmndStack.push(cmndSet);
                    }
                    break;
                }
            }
            tempCmndStack.push(cmnd);
        }
        return foundUri;
    }

    /**
     * private method for reloading the Command Stack
     * @param tempCmndStack
     */
    private void commandStackReload(Stack<Undoable> tempCmndStack){
        int tempSize = tempCmndStack.size();
        for( int i = 0; i < tempSize; i++ ){
            this.commandStack.push(tempCmndStack.pop());
        }
    }


    /*
    * Utility and misc. methods
    */
    /**
     * The String modifer method for when necessary
     * @param text
     * @return
     */
    private String modifyTxt(String text){
        text = text.trim();
        text = text.replaceAll("[^a-zA-Z0-9\\s]", "");
        text = text.toLowerCase();
        text = text.replaceAll("\\s+", " ");
        return text;
    }

    

    /**
     * checking limits no args
     * A powerful limit checker that deal with after the fact
     */
    private void limitChecker(){
        while( (this.documentBytes > this.documentByteLimit) || (this.documentCount > this.documentCountLimit) ){
            ImpastaDoc ref = this.usageTimeMinHeap.remove();
            URI toDiskURI = ref.getURI();
            Document toDiskDoc = this.storageSystem.get(toDiskURI);
            if(toDiskDoc.getDocumentTxt() != null){
                moveTxtDocToDisk(toDiskURI, toDiskDoc, ref);
            }else{
                moveByteDocToDisk(toDiskURI, toDiskDoc, ref);
            }
        }
    }
    /**
     * private move to Disk method for text docs for many method use
     * @param toDiskURI
     * @param toDiskDoc
     * @param ref
     */
    private void moveTxtDocToDisk(URI toDiskURI, Document toDiskDoc, ImpastaDoc ref){
        try {
            this.storageSystem.moveToDisk(toDiskURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ref.setOnDisk();
        lowerCount(toDiskDoc.getDocumentTxt().getBytes().length);
    }

    /**
     * private move to Disk method for byte docs for many method use
     * @param toDiskURI
     * @param toDiskDoc
     * @param ref
     */
    private void moveByteDocToDisk(URI toDiskURI, Document toDiskDoc, ImpastaDoc ref){
        try {
            this.storageSystem.moveToDisk(toDiskURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ref.setOnDisk();
        lowerCount(toDiskDoc.getDocumentBinaryData().length);
    }

    /**
     * a method for lowering the counts in the store
     * @param deacreaseBy the amount of bytes to decrease by in the store's count
     */
    private void lowerCount(int deacreaseBy){
        this.documentCount--;
        this.documentBytes -= deacreaseBy;
    }

    /**
     * a method for raising the counts in the store
     * @param increaseBy the amount of bytes to increase by in the store's count
     */
    private void raiseCount(int increaseBy){
        this.documentCount++;
        this.documentBytes += increaseBy;
    }
}
