package edu.yu.cs.com1320.project.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value>
{
    private final int ALPHABET_SIZE = 36; // letters and numbers
    private Node<Value> root; // root of trie

    class Node<Value>
    {
        Set<Value> values;
        Node<Value>[] links;

        Node()
        {
            this.values = new HashSet<>();
            this.links = new Node[ALPHABET_SIZE];
        }

    }

    public TrieImpl()
    {
        this.root = new Node<Value>();
    }

    @Override
    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val) {
        if( key == null)
        {
            throw new IllegalArgumentException("prefix or comparator is null");
        }
        if( !key.isEmpty() && val != null )
        {
            this.root = put(this.root, key, val, 0);
        }
    }

    /**
     *
     * @param x
     * @param key
     * @param val
     * @param d
     * @return
     */
    private Node<Value> put(Node<Value> x, String key, Value val, int d)
    {
        if( x == null)
        {
            x = new Node<>();
        }
        if( d == key.length())
        {
            x.values.add(val);
            return x;
        }
        char c = key.charAt(d);
        x.links[Character.getNumericValue(c)] = this.put(x.links[Character.getNumericValue(c)], key, val, d + 1);
        return x;
    }

    @Override
    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if( key == null || comparator == null ){
            throw new IllegalArgumentException("key or comparator is null");
        }
        List<Value> matches = new ArrayList<>();
        if( key.isEmpty() ){
            return matches;
        }

        Node<Value> x = this.get(this.root, key, 0);
        if( x == null || x.values == null || x.values.isEmpty() ){
            return matches;
        }
        matches.addAll(x.values);
        matches.sort(comparator);
        return matches;
    }

    private Node<Value> get(Node<Value> x, String key, int d){
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[Character.getNumericValue(c)], key, d + 1);
    }

    @Override
    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if( prefix == null || comparator == null )
        {
            throw new IllegalArgumentException("prefix or comparator is null");
        }
        Set<Value> matchesSet = new HashSet<>();
        if( prefix.isEmpty() )
        {
            return new ArrayList<>();
        }
        getForPrefix(get(this.root, prefix, 0), prefix, matchesSet);
        List<Value> matches = new ArrayList<>(matchesSet); 
        matches.sort(comparator);
        return matches;
    }

    private void getForPrefix(Node<Value> x, String prefix, Set<Value> matches)
    {
        if( x == null )
        {
            return;
        }
        if( x.values != null && !x.values.isEmpty() )
        {
            matches.addAll(x.values);
        }
        for( char c = 0; c < ALPHABET_SIZE; c++)
        {
            getForPrefix(x.links[c], prefix + getChar(c), matches);//prefix + c issue of c being the unicode value so "yo" becomes "yo\000" etc
        }
    }

    @Override
    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if( prefix == null)
        {
            throw new IllegalArgumentException();
        }
        Set<Value> deletedValues = new HashSet<>();
        if( prefix.isEmpty() )
        {
            return deletedValues;
        }
        //just call a get method for prefix since these are the ones to be deleted and now we will just get a list of them
        getForPrefix(get(this.root, prefix, 0), prefix, deletedValues);
        //then just delete subtrie
        Node<Value> x = get(this.root, prefix, 0);
        x = deleteSubtrieForPrefix(x, prefix);
        return deletedValues;
    }

    private Node<Value> deleteSubtrieForPrefix(Node<Value> x, String prefix)//improve to be similar to get and deleteAll
    {
        if( x == null )
        {
            return null;
        }
        if( x.values != null && !x.values.isEmpty() )
        {
            x.values.clear();
        }
        for( char c = 0; c < ALPHABET_SIZE; c++)
        {
            x.links[c] = deleteSubtrieForPrefix(x.links[c], prefix + getChar(c));
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <ALPHABET_SIZE; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    @Override
    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key) {
        if( key == null)
        {
            throw new IllegalArgumentException();
        }
        Set<Value> deletedValues = new HashSet<>();
        if( key.isEmpty() )
        {
            return deletedValues;
        }
        Node<Value> x = this.get(this.root, key, 0);
        if( x != null )//if its not null then we probably have values to add that will be deleted
        {
            deletedValues.addAll(x.values);
        }//if it was null this will return null anyways and we will return an empty set
        this.root = deleteAll(this.root, key, 0);

        return deletedValues;
    }

    private Node<Value> deleteAll(Node<Value> x, String key, int d)//need to modify for deleting collection
    {
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()){
            x.values.clear();;
        }
        //continue down the trie to the target node
        else{
            char c = key.charAt(d);
            x.links[Character.getNumericValue(c)] = this.deleteAll(x.links[Character.getNumericValue(c)], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.values != null){
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <ALPHABET_SIZE; c++){
            if (x.links[c] != null){
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    @Override
    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val) {
        if( key == null || val == null )
        {
            throw new IllegalArgumentException();
        }
        if( key.isEmpty() )
        {
            return null;
        }
        Value valToDelete = null;
        Node<Value> x = this.get(this.root, key, 0);
        if( (x != null) && x.values.contains(val) )
        {
            for( Value v : x.values)
            {
                if( v.equals(val) )
                {
                    valToDelete = v;
                }
            }
            this.root = delete(this.root, key, 0, val);
        }
        return valToDelete;
    }
    
    private Node<Value> delete(Node<Value> x, String key, int d, Value val)//need to improve for dealing with returning a specific value but also going through the trie
    {
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()){
            if( x.values.contains(val) )
            {
                x.values.remove(val);
            }
        }else//continue down the trie to the target node
        {
            char c = key.charAt(d);
            x.links[Character.getNumericValue(c)] = this.delete(x.links[Character.getNumericValue(c)], key, d + 1, val);
        }
        //this node has values – do nothing, return the node
        if (x.values != null && !x.values.isEmpty()){
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <ALPHABET_SIZE; c++){
            if (x.links[c] != null){
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    /**
     * Used for certain conversions to get alphanumeric characters
     * @param c
     * @return
     */
    private char getChar(char c)
    {
        if( c <= 9 && c >=0 )
        {
            return (char)(c + 48);
        }else if( c >= 10 )
        {
            return (char)(c + 87);
        }
        return c;
    }
}
