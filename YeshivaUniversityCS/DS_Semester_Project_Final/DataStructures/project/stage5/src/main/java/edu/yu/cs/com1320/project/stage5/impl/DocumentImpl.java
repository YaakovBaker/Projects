package edu.yu.cs.com1320.project.stage5.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;


public class DocumentImpl implements Document{
    private URI uri;
    private String txt;
    private byte[] binaryData;
    private Map<String, Integer> wordCounts = new HashMap<>();
    private long lastUseTime;

    public DocumentImpl(URI uri, String text, boolean fromPM){
        if ( text == null || text.isEmpty() || uri == null || uri.toString().isEmpty() ){
            throw new IllegalArgumentException("either argument is null or empty/blank");
        }
        if( fromPM ){
            this.uri = uri;
            this.txt = text;
            this.binaryData = null;
            this.lastUseTime = 0;
        }
    }
    public DocumentImpl(URI uri, String txt) throws IllegalArgumentException{
        if ( txt == null || txt.isEmpty() || uri == null || uri.toString().isEmpty() ){
            throw new IllegalArgumentException("either argument is null or empty/blank");
        }
        this.uri = uri;
        this.txt = txt;
        this.binaryData = null;
        this.lastUseTime = 0;
        loadWordCountMap();
    }

    public DocumentImpl(URI uri, byte[] binaryData) throws IllegalArgumentException{
        if ( binaryData == null || binaryData.length == 0 || uri == null || uri.toString().isEmpty() ) {
            throw new IllegalArgumentException("either argument is null or empty/blank");
        }
        this.uri = uri;
        this.binaryData = binaryData;
        this.txt = null;
        this.lastUseTime = 0;
    }

    @Override
    /**
     * @return content of text document
     */
    public String getDocumentTxt() {
        return this.txt;
    }

    @Override
    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey() {
        return this.uri;
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
        int result = this.uri.hashCode();
        result = 31 * result + (this.txt != null ? this.txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    public int wordCount(String word){
        String caseInsensitiveWord = modifyTxt(word);
        if( this.binaryData != null || !this.wordCounts.containsKey(caseInsensitiveWord) ){
            return 0;
        }
        return this.wordCounts.get(caseInsensitiveWord); 
    }

    @Override
    /**
     * This must return a copy of the wordcount map so it can be serialized
     */
    public Map<String, Integer> getWordMap(){
        if( isByte() ){
            return new HashMap<>();
        }
        return Map.copyOf(this.wordCounts);
    }

    @Override
    /**
     * This must set the wordcount map during deserialization
     * @param wordMap
     */
    public void setWordMap(Map<String, Integer> wordMap) {
        this.wordCounts = wordMap;
    }

    @Override
    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords(){
        if( isByte() ){
            return new HashSet<>();
        }
        return this.wordCounts.keySet();
    }

    @Override
    public int compareTo(Document o) {
        if( this.getLastUseTime() > o.getLastUseTime() ){
            return 1;
        }else if( this.getLastUseTime() < o.getLastUseTime() ){
            return -1;
        }else{
            return 0;
        }
    }

    @Override
    public long getLastUseTime(){
        return this.lastUseTime;
    }

    /**
     * Every time a document is used, its last use time should be updated to the relative JVM time, 
     * as measured in nanoseconds (see java.lang.System.nanoTime().) 
     * A Document is considered to be "used" whenever it is accessed as a result of a call to any part of DocumentStore's public API. 
     * In other words, if it is "put", or returned in any form as the result of any "get" or "search" request, 
     * or an action on it is undone via any call to either of the DocumentStore.undo methods.
     */
    @Override
    public void setLastUseTime(long timeInNanoseconds){
        this.lastUseTime = timeInNanoseconds;
    }

    /**
     * Method for loading the map with the word counts for the words
     */
    private void loadWordCountMap() {
        String modifiedTxt = modifyTxt(this.txt);
        String[] stringIteration = modifiedTxt.split("\\s");
        for( String word: stringIteration){
            if( this.wordCounts.containsKey(word) ){
                this.wordCounts.put(word, this.wordCounts.get(word) + 1);
            }else{
                this.wordCounts.put(word, 1);
            }
        }
    }

    /**
     * 
     * @param text to modify to be suitable for our purposes
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
     * 
     * @return true if byte doc, false if not
     */
    private boolean isByte(){
        return this.binaryData != null;
    }
}

