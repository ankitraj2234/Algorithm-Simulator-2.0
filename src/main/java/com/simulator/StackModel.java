package com.simulator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class StackModel {

    private List<Integer> stack;
    private int maxSize;

    public StackModel() {
        this(20); // Default max size
    }

    public StackModel(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new ArrayList<>();
    }

    public void push(int value) {
        if (stack.size() >= maxSize) {
            throw new IllegalStateException("Stack overflow: Maximum capacity reached");
        }
        stack.add(value);
        System.out.println("Pushed: " + value + " | Stack size: " + stack.size());
    }

    public int pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        int value = stack.remove(stack.size() - 1);
        System.out.println("Popped: " + value + " | Stack size: " + stack.size());
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return stack.get(stack.size() - 1);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    public void clear() {
        stack.clear();
        System.out.println("Stack cleared");
    }

    public boolean isFull() {
        return stack.size() >= maxSize;
    }

    public List<Integer> getElements() {
        return new ArrayList<>(stack);
    }

    public int getCapacity() {
        return maxSize;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Stack: []";
        }

        StringBuilder sb = new StringBuilder("Stack (top to bottom): [");
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
            if (i > 0) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
