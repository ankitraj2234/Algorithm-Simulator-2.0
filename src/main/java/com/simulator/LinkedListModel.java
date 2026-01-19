package com.simulator;

import java.util.ArrayList;
import java.util.List;

public class LinkedListModel {

    private Node head;
    private int size;

    public LinkedListModel() {
        this.head = null;
        this.size = 0;
    }

    // Node class for linked list
    public static class Node {
        public int data;
        public Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // Insert at beginning
    public void insertAtBeginning(int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
        size++;
        System.out.println("Inserted " + data + " at beginning. Size: " + size);
    }

    // Insert at end
    public void insertAtEnd(int data) {
        Node newNode = new Node(data);

        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
        System.out.println("Inserted " + data + " at end. Size: " + size);
    }

    // Insert at position
    public void insertAtPosition(int position, int data) {
        if (position < 0 || position > size) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }

        if (position == 0) {
            insertAtBeginning(data);
            return;
        }

        Node newNode = new Node(data);
        Node current = head;

        for (int i = 0; i < position - 1; i++) {
            current = current.next;
        }

        newNode.next = current.next;
        current.next = newNode;
        size++;
        System.out.println("Inserted " + data + " at position " + position + ". Size: " + size);
    }

    // Delete first occurrence
    public boolean delete(int data) {
        if (head == null) {
            return false;
        }

        if (head.data == data) {
            head = head.next;
            size--;
            System.out.println("Deleted " + data + ". Size: " + size);
            return true;
        }

        Node current = head;
        while (current.next != null && current.next.data != data) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
            size--;
            System.out.println("Deleted " + data + ". Size: " + size);
            return true;
        }

        return false;
    }

    // Delete at position
    public boolean deleteAtPosition(int position) {
        if (position < 0 || position >= size || head == null) {
            return false;
        }

        if (position == 0) {
            int data = head.data;
            head = head.next;
            size--;
            System.out.println("Deleted " + data + " at position " + position + ". Size: " + size);
            return true;
        }

        Node current = head;
        for (int i = 0; i < position - 1; i++) {
            current = current.next;
        }

        int data = current.next.data;
        current.next = current.next.next;
        size--;
        System.out.println("Deleted " + data + " at position " + position + ". Size: " + size);
        return true;
    }

    // Search for element
    public int search(int data) {
        Node current = head;
        int position = 0;

        while (current != null) {
            if (current.data == data) {
                System.out.println("Found " + data + " at position " + position);
                return position;
            }
            current = current.next;
            position++;
        }

        System.out.println(data + " not found in the list");
        return -1;
    }

    // Get all elements as list
    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        Node current = head;

        while (current != null) {
            list.add(current.data);
            current = current.next;
        }

        return list;
    }

    // Get all nodes (for visualization)
    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        Node current = head;

        while (current != null) {
            nodes.add(current);
            current = current.next;
        }

        return nodes;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        size = 0;
        System.out.println("LinkedList cleared");
    }

    @Override
    public String toString() {
        if (head == null) {
            return "LinkedList: []";
        }

        StringBuilder sb = new StringBuilder("LinkedList: [");
        Node current = head;

        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(" -> ");
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }
}
