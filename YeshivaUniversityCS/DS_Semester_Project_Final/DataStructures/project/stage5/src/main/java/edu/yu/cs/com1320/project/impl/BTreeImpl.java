package edu.yu.cs.com1320.project.impl;

import java.io.IOException;
import java.util.Arrays;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    
    //max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private static final int MAX = 6;
    private Node root; //root of the B-tree
    private Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree

    //B-tree node data type
    static final class Node{
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[BTreeImpl.MAX]; // the array of children (Entry[])new Object
        private Node next;
        private Node previous;

        // create a node with k entries
        private Node(int k){
            this.entryCount = k;
        }

        private void setNext(Node next){
            this.next = next;
        }
        private Node getNext(){
            return this.next;
        }
        private void setPrevious(Node previous){
            this.previous = previous;
        }
        private Node getPrevious(){
            return this.previous;
        }

        private Entry[] getEntries(){
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }
 
    //internal nodes: only use key and child
    //external nodes: only use key and value
    static class Entry{
        private Comparable key;
        private Object val;
        private Node child;
        private boolean isOnDisk;

        public Entry(Comparable key, Object val, Node child){
        this.key = key;
        this.val = val;
        this.child = child;
        this.isOnDisk = false;
        }
        private Object getValue(){
            return this.val;
        }
        private Comparable getKey(){
            return this.key;
        }
        private boolean isOnDisk(){
            return this.isOnDisk;
        }
        private void onDisk(){
            this.isOnDisk = true;
        }
        private void notOnDisk(){
            this.isOnDisk = false;
        }
    }

    PersistenceManager<Key, Value> pm;

    public BTreeImpl(){
        this.root = new Node(0);
        this.leftMostExternalNode = this.root;
    }

    @Override
    public Value get(Key k) {
        if( k == null ){
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry entry = this.get(this.root, k, this.height);
        if( entry != null ){
            if( entry.isOnDisk() ){
                try {
                    entry.val = pm.deserialize(k);
                    entry.notOnDisk();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//in memory and other cases including what was just deserialized if that arises returns what is there in entry
            return (Value)entry.val;
        }
        return null;
    }

    private Entry get(Node currentNode, Key key, int height){
        Entry[] entries = currentNode.entries;
        //current node is external (i.e. height == 0)
        if( height == 0 ){
            for(int index = 0; index < currentNode.entryCount; index++){
                if( isEqual(key, entries[index].key )){
                    //found desired key. Return the desired Entry
                    return entries[index];
                }
            }
            //didn't find the key
            return null;
        }else{//current node is internal (height > 0)
            for(int index = 0; index < currentNode.entryCount; index++){
                //if {we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be in the subtree below the current entry},
                //then recurse into the current entry’s child
                if( index + 1 == currentNode.entryCount || less(key, entries[index + 1].getKey()) ){
                    return this.get(entries[index].child, key, height - 1);
                }
            }
            //didn't find the key
            return null;
        }
    }

    /**
     * for public put when the entry already exists
     * @param alreadyThere
     * @param k
     * @param v
     * @return
     */
    private Value entryExists(Entry alreadyThere, Key k, Value v){
        if( alreadyThere.isOnDisk() ){
            Value deserializedVal = null;
            try {
                deserializedVal = pm.deserialize(k);
            } catch (IOException e) {
                e.printStackTrace();
            }
            alreadyThere.val = deserializedVal;//then continue to the preceding logic
        }else{//its in memory
            if( alreadyThere.val == null ){
                alreadyThere.val = v;
                return null;
            }//else continue to the preceding logic
        }
        Value oldVal = (Value)alreadyThere.val;
        alreadyThere.val = v;
        alreadyThere.notOnDisk();
        return oldVal;
    }

    @Override
    /**
     * return the old Value or null if nothing there
     */
    public Value put(Key k, Value v) {
        if( k == null ){
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //if the key already exists in the b-tree, simply replace the value
        Entry alreadyThere = this.get(this.root, k, this.height);//calls private get
        if( alreadyThere != null ){
            return entryExists(alreadyThere, k, v);
        }
        //since alreadyThere does equal null we create a newNode and call private put
        Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if( newNode == null ){
            return null;
        }

        //split the root:
        //Create a new node to be the root.
        //Set the old root to be new root's first entry.
        //Set the node returned from the call to put to be new root's second entry
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        //a split at the root always increases the tree height by 1
        this.height++;
        return null;
    }

    /**
     *
     * @param currentNode
     * @param key
     * @param val
     * @param height
     * @return null if no new node was created (i.e. just added a new Entry into an existing node). If a new node was created due to the need to split, returns the new node
     */
    private Node put(Node currentNode, Key key, Value val, int height){
        //create a new entry with this key and val and no child nodes
        Entry newEntry = new Entry(key, val, null);
        //the index where this entry will go
        int entryIndex;
        //external node
        if( height == 0 ){
            //find index in currentNode’s entry[] to insert new entry
            //we look for key < entry.key since we want to leave entryIndex
            //pointing to the slot to insert the new entry, hence we want to find
            //the first entry in the current node that key is LESS THAN
            for(entryIndex = 0; entryIndex < currentNode.entryCount; entryIndex++){
                if( less(key, currentNode.entries[entryIndex].getKey()) ){
                    break;
                }
            }
        }else{// internal node
            //find index in node entry array to insert the new entry
            for(entryIndex = 0; entryIndex < currentNode.entryCount; entryIndex++){
                //if {we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be added to the subtree below the current entry},
                //then do a recursive call to put on the current entry’s child
                if( (entryIndex + 1 == currentNode.entryCount) || less(key, currentNode.entries[entryIndex + 1].getKey()) ){
                    //increment entryIndex (entryIndex++) after the call so that a new entry created by a split
                    //will be inserted in the next slot
                    Node newNode = this.put(currentNode.entries[entryIndex++].child, key, val, height - 1);
                    if( newNode == null ){
                        return null;
                    }
                    //if the call to put returned a node, it means I need to add a new entry to the current node
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        return entryShiftAndSplit(entryIndex, currentNode, newEntry, height);
    }

    /**
     * method to ease cognitive complexity of private put
     * @param entryIndex
     * @param currentNode
     * @param newEntry
     * @param height
     * @return
     */
    private Node entryShiftAndSplit(int entryIndex, Node currentNode, Entry newEntry, int height){
        //shift entries over one place to make room for new entry
        for(int i = currentNode.entryCount; i > entryIndex; i--){
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        //add new entry into slot entryIndex in the currentNode
        currentNode.entries[entryIndex] = newEntry;
        currentNode.entryCount++;
        if( currentNode.entryCount < BTreeImpl.MAX ){
            //no structural changes needed in the tree
            //so just return null
            return null;
        }else{
            //will have to create new entry in the parent due
            //to the split, so return the new node, which is
            //the node for which the new entry will be created
            return this.split(currentNode, height);
        }
    }

    /**
     * split node in half
     * @param currentNode
     * @return new node
     */
    private Node split(Node currentNode, int height){
        Node newNode = new Node(BTreeImpl.MAX / 2);
        //by changing currentNode.entryCount, we will treat any value
        //at index higher than the new currentNode.entryCount as if
        //it doesn't exist
        currentNode.entryCount = BTreeImpl.MAX / 2;
        //copy top half of h into t
        for(int j = 0; j < BTreeImpl.MAX / 2; j++){
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
        }
        //external node
        if( height == 0 ){
            newNode.setNext(currentNode.getNext());
            newNode.setPrevious(currentNode);
            currentNode.setNext(newNode);
        }
        return newNode;
    }


    @Override
    public void moveToDisk(Key k) throws Exception {
        Entry entry = this.get(this.root, k, this.height);
        if( entry != null && !entry.isOnDisk() ){
            Value val = (Value)entry.getValue();
            if( val != null ){
                entry.val = null;
                entry.onDisk();
                this.pm.serialize(k, val);
            }
        }
    }

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.pm = pm;
    }
    
    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) < 0;
    }

    private boolean isEqual(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) == 0;
    }
}
