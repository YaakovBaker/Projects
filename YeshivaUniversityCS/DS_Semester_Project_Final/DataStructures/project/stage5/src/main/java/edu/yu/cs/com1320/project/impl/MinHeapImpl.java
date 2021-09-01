package edu.yu.cs.com1320.project.impl;

import java.util.Arrays;
import java.util.NoSuchElementException;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> 
{
    private String notFound = "Element is not in heap";
    public MinHeapImpl(){
        super.elements = (E[]) new Comparable[16];
    }
    
    @Override
    public void reHeapify(E element){
        if( isEmpty() ){
            throw new NoSuchElementException(notFound);
        }
        int elIndex = getArrayIndex(element);
        if( hasParent(elIndex) ){
            if( isGreater(elIndex, parent(elIndex)) ){
                downHeap(elIndex);
            }else{
                upHeap(elIndex);
            }
        }else{
            if(hasLeftChild(elIndex) || hasRightChild(elIndex)){
                upHeap(elIndex);
            }else{
                downHeap(elIndex);
            }
        }
    }

    @Override
    protected int getArrayIndex(E element){
        if( isEmpty() ){
            throw new NoSuchElementException(notFound);
        }
        for( int index = 1; index <= super.count; index++ ){
            if( super.elements[index] == null ){
                throw new NoSuchElementException(notFound);
            }
            if( element.equals(super.elements[index]) ){
                return index;
            }
        }
        throw new NoSuchElementException(notFound);
    }

    @Override
    protected void doubleArraySize() {
        super.elements = Arrays.copyOf(super.elements, super.elements.length * 2 );
    }

    private int parent(int index){
        return index / 2;
    }

    private boolean hasParent(int index){
        return index > 1;
    }

    private int leftChild(int index){
        return index * 2;
    }

    private int rightChild(int index){
        return index * 2 + 1;
    }

    private boolean hasLeftChild(int index){
        return leftChild(index) <= super.count;
    }

    private boolean hasRightChild(int index){
        return rightChild(index) <= super.count;
    }
}
