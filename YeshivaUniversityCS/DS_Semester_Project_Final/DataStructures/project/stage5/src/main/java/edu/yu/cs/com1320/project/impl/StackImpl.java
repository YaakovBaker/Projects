package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T>
{
    class StackEntry<T>
    {
        T data;
        StackEntry<T> next;

        StackEntry(T data, StackEntry<T> next)
        {
            this.data = data;
            this.next = next;
        }
        StackEntry(T data)
        {
            this(data, null);
        }
    }
    //amount of elements in the stack
    private int count;
    //pointer to top of linkedList
    private StackEntry<T> top;

    public StackImpl(){
        this.top = null;
        this.count = 0;
    }

    @Override
    /**
     * @param element object to add to the Stack
     */
    public void push(T element)
    {
        if( element == null )
        {
            throw new IllegalArgumentException(); 
        }
        StackEntry<T> newEntry = new StackEntry<>(element);
        newEntry.next = this.top;
        this.top = newEntry;
        this.count++;
    }

    @Override
    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    public T pop()
    {
        if( isEmpty() )
        {
            return null;
        }
        T value = this.top.data;
        this.top = this.top.next;
        this.count--;
        return value;
    }

    @Override
    /**
     *
     * @return the element at the top of the stack without removing it
     */
    public T peek()
    {
        if( isEmpty() )
        {
            return null;
        }
        return this.top.data;
    }

    @Override
    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size()
    {
        return this.count;
    }

    private boolean isEmpty()
    {
        return this.count == 0;
    }
}