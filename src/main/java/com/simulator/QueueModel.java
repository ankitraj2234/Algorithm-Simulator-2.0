package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Queue data structure implementation using ArrayList.
 * Supports FIFO (First-In-First-Out) operations.
 */
public class QueueModel {

    private List<Integer> queue;
    private int maxSize;

    public QueueModel() {
        this(20); // Default max size
    }

    public QueueModel(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new ArrayList<>();
    }

    /**
     * Add element to the rear of the queue.
     */
    public void enqueue(int value) {
        if (queue.size() >= maxSize) {
            throw new IllegalStateException("Queue overflow: Maximum capacity reached");
        }
        queue.add(value);
        System.out.println("Enqueued: " + value + " | Queue size: " + queue.size());
    }

    /**
     * Remove and return element from the front of the queue.
     */
    public int dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue underflow: Cannot dequeue from empty queue");
        }
        int value = queue.remove(0);
        System.out.println("Dequeued: " + value + " | Queue size: " + queue.size());
        return value;
    }

    /**
     * View the front element without removing it.
     */
    public int front() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty: No front element");
        }
        return queue.get(0);
    }

    /**
     * View the rear element without removing it.
     */
    public int rear() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty: No rear element");
        }
        return queue.get(queue.size() - 1);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
        System.out.println("Queue cleared");
    }

    public boolean isFull() {
        return queue.size() >= maxSize;
    }

    public List<Integer> getElements() {
        return new ArrayList<>(queue);
    }

    public int getCapacity() {
        return maxSize;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Queue: []";
        }

        StringBuilder sb = new StringBuilder("Queue (front to rear): [");
        for (int i = 0; i < queue.size(); i++) {
            sb.append(queue.get(i));
            if (i < queue.size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
