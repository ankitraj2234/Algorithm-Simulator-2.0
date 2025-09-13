package com.simulator;

import java.util.HashMap;
import java.util.Map;

public class LinkedListCodeRepository {
    private final Map<String, CodeExample> codeExamples;

    public LinkedListCodeRepository() {
        codeExamples = new HashMap<>();
        initializeCodeExamples();
    }

    private void initializeCodeExamples() {
        // =============== BASIC OPERATIONS ===============
        codeExamples.put("Create LinkedList", new CodeExample(
                "Create LinkedList",
                "Basic LinkedList class structure with Node definition",
                "O(1)",
                """
                // LinkedList class with Node structure
                public class LinkedList {
                    private Node head;
                    private int size;
                    
                    // Inner Node class
                    private static class Node {
                        int data;
                        Node next;
                        
                        Node(int data) {
                            this.data = data;
                            this.next = null;
                        }
                    }
                    
                    // Constructor
                    public LinkedList() {
                        this.head = null;
                        this.size = 0;
                    }
                    
                    // Check if list is empty
                    public boolean isEmpty() {
                        return head == null;
                    }
                    
                    // Get size of list
                    public int size() {
                        return size;
                    }
                }
                """
        ));

        codeExamples.put("Insert at Beginning", new CodeExample(
                "Insert at Beginning",
                "Insert a new node at the beginning of the LinkedList",
                "O(1)",
                """
                // Insert element at the beginning
                public void insertAtBeginning(int data) {
                    // Create new node
                    Node newNode = new Node(data);
                    // Point new node to current head
                    newNode.next = head;
                    // Update head to new node
                    head = newNode;
                    // Increment size
                    size++;
                    System.out.println("Inserted " + data + " at beginning");
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtBeginning(10);
                list.insertAtBeginning(20);
                list.insertAtBeginning(30);
                // Result: 30 -> 20 -> 10 -> null
                """
        ));

        codeExamples.put("Insert at End", new CodeExample(
                "Insert at End",
                "Insert a new node at the end of the LinkedList",
                "O(n)",
                """
                // Insert element at the end
                public void insertAtEnd(int data) {
                    Node newNode = new Node(data);
                    
                    // If list is empty, make new node the head
                    if (head == null) {
                        head = newNode;
                    } else {
                        // Traverse to the last node
                        Node current = head;
                        while (current.next != null) {
                            current = current.next;
                        }
                        // Link last node to new node
                        current.next = newNode;
                    }
                    size++;
                    System.out.println("Inserted " + data + " at end");
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(20);
                list.insertAtEnd(30);
                // Result: 10 -> 20 -> 30 -> null
                """
        ));

        codeExamples.put("Insert at Position", new CodeExample(
                "Insert at Position",
                "Insert a new node at a specific position in the LinkedList",
                "O(n)",
                """
                // Insert element at specific position
                public void insertAtPosition(int position, int data) {
                    // Validate position
                    if (position < 0 || position > size) {
                        throw new IndexOutOfBoundsException("Invalid position: " + position);
                    }
                    
                    // If inserting at beginning
                    if (position == 0) {
                        insertAtBeginning(data);
                        return;
                    }
                    
                    Node newNode = new Node(data);
                    Node current = head;
                    
                    // Traverse to position-1
                    for (int i = 0; i < position - 1; i++) {
                        current = current.next;
                    }
                    
                    // Insert new node
                    newNode.next = current.next;
                    current.next = newNode;
                    size++;
                    System.out.println("Inserted " + data + " at position " + position);
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(30);
                list.insertAtPosition(1, 20);
                // Result: 10 -> 20 -> 30 -> null
                """
        ));

        codeExamples.put("Search Element", new CodeExample(
                "Search Element",
                "Search for an element in the LinkedList and return its position",
                "O(n)",
                """
                // Search for element and return position
                public int search(int data) {
                    Node current = head;
                    int position = 0;
                    
                    // Traverse the list
                    while (current != null) {
                        if (current.data == data) {
                            System.out.println("Found " + data + " at position " + position);
                            return position;
                        }
                        current = current.next;
                        position++;
                    }
                    
                    System.out.println(data + " not found in the list");
                    return -1; // Element not found
                }
                
                // Search with boolean return
                public boolean contains(int data) {
                    return search(data) != -1;
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(20);
                list.insertAtEnd(30);
                int position = list.search(20); // Returns 1
                boolean exists = list.contains(25); // Returns false
                """
        ));

        codeExamples.put("Delete Element", new CodeExample(
                "Delete Element",
                "Delete the first occurrence of an element from the LinkedList",
                "O(n)",
                """
                // Delete first occurrence of element
                public boolean delete(int data) {
                    // Empty list
                    if (head == null) {
                        return false;
                    }
                    
                    // If head node contains the data
                    if (head.data == data) {
                        head = head.next;
                        size--;
                        System.out.println("Deleted " + data + " from beginning");
                        return true;
                    }
                    
                    Node current = head;
                    // Find the node to delete
                    while (current.next != null && current.next.data != data) {
                        current = current.next;
                    }
                    
                    // If element found
                    if (current.next != null) {
                        current.next = current.next.next;
                        size--;
                        System.out.println("Deleted " + data);
                        return true;
                    }
                    
                    System.out.println(data + " not found for deletion");
                    return false; // Element not found
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(20);
                list.insertAtEnd(30);
                boolean deleted = list.delete(20); // Returns true
                // Result: 10 -> 30 -> null
                """
        ));

        codeExamples.put("Delete at Position", new CodeExample(
                "Delete at Position",
                "Delete element at a specific position from the LinkedList",
                "O(n)",
                """
                // Delete element at specific position
                public boolean deleteAtPosition(int position) {
                    // Validate position
                    if (position < 0 || position >= size || head == null) {
                        return false;
                    }
                    
                    // If deleting head
                    if (position == 0) {
                        int data = head.data;
                        head = head.next;
                        size--;
                        System.out.println("Deleted " + data + " at position " + position);
                        return true;
                    }
                    
                    Node current = head;
                    // Traverse to position-1
                    for (int i = 0; i < position - 1; i++) {
                        current = current.next;
                    }
                    
                    int data = current.next.data;
                    current.next = current.next.next;
                    size--;
                    System.out.println("Deleted " + data + " at position " + position);
                    return true;
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(20);
                list.insertAtEnd(30);
                boolean deleted = list.deleteAtPosition(1); // Deletes 20
                // Result: 10 -> 30 -> null
                """
        ));

        codeExamples.put("Display List", new CodeExample(
                "Display List",
                "Display all elements in the LinkedList",
                "O(n)",
                """
                // Display all elements
                public void display() {
                    if (head == null) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    Node current = head;
                    System.out.print("LinkedList: ");
                    while (current != null) {
                        System.out.print(current.data);
                        if (current.next != null) {
                            System.out.print(" -> ");
                        }
                        current = current.next;
                    }
                    System.out.println(" -> null");
                }
                
                // Get as array
                public int[] toArray() {
                    int[] array = new int[size];
                    Node current = head;
                    int index = 0;
                    while (current != null) {
                        array[index++] = current.data;
                        current = current.next;
                    }
                    return array;
                }
                
                // toString method
                @Override
                public String toString() {
                    if (head == null) {
                        return "[]";
                    }
                    
                    StringBuilder sb = new StringBuilder("[");
                    Node current = head;
                    while (current != null) {
                        sb.append(current.data);
                        if (current.next != null) {
                            sb.append(", ");
                        }
                        current = current.next;
                    }
                    sb.append("]");
                    return sb.toString();
                }
                """
        ));

        // =============== SINGLY LINKEDLIST ===============
        codeExamples.put("Node Class", new CodeExample(
                "Singly LinkedList Node",
                "Node class implementation for Singly LinkedList",
                "O(1)",
                """
                // Node class for Singly LinkedList
                public class SinglyNode {
                    int data; // Data stored in the node
                    SinglyNode next; // Reference to next node
                    
                    // Constructor
                    public SinglyNode(int data) {
                        this.data = data;
                        this.next = null;
                    }
                    
                    // Constructor with next node
                    public SinglyNode(int data, SinglyNode next) {
                        this.data = data;
                        this.next = next;
                    }
                    
                    // Getter methods
                    public int getData() {
                        return data;
                    }
                    
                    public SinglyNode getNext() {
                        return next;
                    }
                    
                    // Setter methods
                    public void setData(int data) {
                        this.data = data;
                    }
                    
                    public void setNext(SinglyNode next) {
                        this.next = next;
                    }
                    
                    @Override
                    public String toString() {
                        return "Node{" + data + "}";
                    }
                }
                """
        ));

        codeExamples.put("Complete Implementation", new CodeExample(
                "Complete Singly LinkedList",
                "Full implementation of Singly LinkedList with all operations",
                "O(1) to O(n)",
                """
                // Complete Singly LinkedList Implementation
                public class SinglyLinkedList {
                    private SinglyNode head;
                    private SinglyNode tail;
                    private int size;
                    
                    // Constructor
                    public SinglyLinkedList() {
                        this.head = null;
                        this.tail = null;
                        this.size = 0;
                    }
                    
                    // Add element at beginning
                    public void addFirst(int data) {
                        SinglyNode newNode = new SinglyNode(data);
                        if (isEmpty()) {
                            head = tail = newNode;
                        } else {
                            newNode.next = head;
                            head = newNode;
                        }
                        size++;
                    }
                    
                    // Add element at end
                    public void addLast(int data) {
                        SinglyNode newNode = new SinglyNode(data);
                        if (isEmpty()) {
                            head = tail = newNode;
                        } else {
                            tail.next = newNode;
                            tail = newNode;
                        }
                        size++;
                    }
                    
                    // Remove first element
                    public int removeFirst() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        int data = head.data;
                        head = head.next;
                        if (head == null) {
                            tail = null;
                        }
                        size--;
                        return data;
                    }
                    
                    // Remove last element
                    public int removeLast() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        if (size == 1) {
                            return removeFirst();
                        }
                        
                        // Find second last node
                        SinglyNode current = head;
                        while (current.next != tail) {
                            current = current.next;
                        }
                        
                        int data = tail.data;
                        tail = current;
                        tail.next = null;
                        size--;
                        return data;
                    }
                    
                    // Utility methods
                    public boolean isEmpty() {
                        return head == null;
                    }
                    
                    public int size() {
                        return size;
                    }
                    
                    public int getFirst() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        return head.data;
                    }
                    
                    public int getLast() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        return tail.data;
                    }
                }
                """
        ));

        // ✅ NEW: Traversal Methods
        codeExamples.put("Traversal Methods", new CodeExample(
                "Singly LinkedList Traversal",
                "Various traversal methods for Singly LinkedList including iterative and recursive approaches",
                "O(n)",
                """
                // Forward traversal methods for Singly LinkedList
                
                // Iterative traversal
                public void traverse() {
                    if (head == null) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    SinglyNode current = head;
                    System.out.print("List: ");
                    while (current != null) {
                        System.out.print(current.data + " ");
                        current = current.next;
                    }
                    System.out.println();
                }
                
                // Recursive traversal
                public void traverseRecursive() {
                    System.out.print("List (Recursive): ");
                    traverseRecursiveHelper(head);
                    System.out.println();
                }
                
                private void traverseRecursiveHelper(SinglyNode node) {
                    if (node != null) {
                        System.out.print(node.data + " ");
                        traverseRecursiveHelper(node.next);
                    }
                }
                
                // Reverse traversal using recursion
                public void traverseReverse() {
                    System.out.print("List (Reverse): ");
                    traverseReverseHelper(head);
                    System.out.println();
                }
                
                private void traverseReverseHelper(SinglyNode node) {
                    if (node != null) {
                        traverseReverseHelper(node.next);
                        System.out.print(node.data + " ");
                    }
                }
                
                // Get element at specific position
                public int get(int index) {
                    if (index < 0 || index >= size) {
                        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
                    }
                    
                    SinglyNode current = head;
                    for (int i = 0; i < index; i++) {
                        current = current.next;
                    }
                    return current.data;
                }
                
                // Usage Example:
                SinglyLinkedList list = new SinglyLinkedList();
                list.addLast(10);
                list.addLast(20);
                list.addLast(30);
                list.traverse();         // Output: List: 10 20 30
                list.traverseRecursive(); // Output: List (Recursive): 10 20 30
                list.traverseReverse();   // Output: List (Reverse): 30 20 10
                """
        ));

        // ✅ NEW: Size & Empty Check
        codeExamples.put("Size & Empty Check", new CodeExample(
                "Size and Empty Check Operations",
                "Various utility methods to check size, emptiness, and validate LinkedList state",
                "O(1) to O(n)",
                """
                // Size and empty check operations for LinkedList
                
                // Check if list is empty - O(1)
                public boolean isEmpty() {
                    return head == null;
                }
                
                // Get current size (if maintained) - O(1)
                public int size() {
                    return size;
                }
                
                // Calculate size by counting - O(n)
                public int calculateSize() {
                    int count = 0;
                    SinglyNode current = head;
                    while (current != null) {
                        count++;
                        current = current.next;
                    }
                    return count;
                }
                
                // Check if list has only one element
                public boolean hasSingleElement() {
                    return head != null && head.next == null;
                }
                
                // Check if list has at least n elements
                public boolean hasAtLeastNElements(int n) {
                    if (n <= 0) return true;
                    
                    int count = 0;
                    SinglyNode current = head;
                    while (current != null && count < n) {
                        count++;
                        current = current.next;
                    }
                    return count >= n;
                }
                
                // Get length without using size variable
                public int length() {
                    int length = 0;
                    SinglyNode current = head;
                    while (current != null) {
                        length++;
                        current = current.next;
                    }
                    return length;
                }
                
                // Validate list integrity
                public boolean isValid() {
                    if (head == null) {
                        return size == 0;
                    }
                    
                    int actualSize = calculateSize();
                    return actualSize == size;
                }
                
                // Compare sizes of two lists
                public static int compareSizes(SinglyLinkedList list1, SinglyLinkedList list2) {
                    int size1 = list1.size();
                    int size2 = list2.size();
                    
                    if (size1 < size2) return -1;
                    if (size1 > size2) return 1;
                    return 0;
                }
                
                // Usage Example:
                SinglyLinkedList list = new SinglyLinkedList();
                System.out.println("Is empty: " + list.isEmpty());        // true
                System.out.println("Size: " + list.size());               // 0
                
                list.addLast(10);
                list.addLast(20);
                
                System.out.println("Is empty: " + list.isEmpty());        // false
                System.out.println("Size: " + list.size());               // 2
                System.out.println("Has single element: " + list.hasSingleElement()); // false
                System.out.println("Has at least 3 elements: " + list.hasAtLeastNElements(3)); // false
                """
        ));

        // =============== DOUBLY LINKEDLIST ===============
        codeExamples.put("Node Class", new CodeExample(
                "Doubly LinkedList Node",
                "Node class implementation for Doubly LinkedList with previous pointer",
                "O(1)",
                """
                // Node class for Doubly LinkedList
                public class DoublyNode {
                    int data; // Data stored in the node
                    DoublyNode next; // Reference to next node
                    DoublyNode prev; // Reference to previous node
                    
                    // Constructor
                    public DoublyNode(int data) {
                        this.data = data;
                        this.next = null;
                        this.prev = null;
                    }
                    
                    // Constructor with next and previous nodes
                    public DoublyNode(int data, DoublyNode prev, DoublyNode next) {
                        this.data = data;
                        this.prev = prev;
                        this.next = next;
                    }
                    
                    // Getter methods
                    public int getData() {
                        return data;
                    }
                    
                    public DoublyNode getNext() {
                        return next;
                    }
                    
                    public DoublyNode getPrev() {
                        return prev;
                    }
                    
                    // Setter methods
                    public void setData(int data) {
                        this.data = data;
                    }
                    
                    public void setNext(DoublyNode next) {
                        this.next = next;
                    }
                    
                    public void setPrev(DoublyNode prev) {
                        this.prev = prev;
                    }
                    
                    @Override
                    public String toString() {
                        return "DoublyNode{" + data + "}";
                    }
                }
                """
        ));

        // ✅ NEW: Doubly LinkedList Complete Implementation
        codeExamples.put("Complete Implementation", new CodeExample(
                "Complete Doubly LinkedList",
                "Full implementation of Doubly LinkedList with all operations and bidirectional pointers",
                "O(1) to O(n)",
                """
                // Complete Doubly LinkedList Implementation
                public class DoublyLinkedList {
                    private DoublyNode head;
                    private DoublyNode tail;
                    private int size;
                    
                    // Constructor
                    public DoublyLinkedList() {
                        this.head = null;
                        this.tail = null;
                        this.size = 0;
                    }
                    
                    // Check if list is empty
                    public boolean isEmpty() {
                        return head == null;
                    }
                    
                    // Get size of list
                    public int size() {
                        return size;
                    }
                    
                    // Add element at beginning - O(1)
                    public void addFirst(int data) {
                        DoublyNode newNode = new DoublyNode(data);
                        if (isEmpty()) {
                            head = tail = newNode;
                        } else {
                            newNode.next = head;
                            head.prev = newNode;
                            head = newNode;
                        }
                        size++;
                    }
                    
                    // Add element at end - O(1)
                    public void addLast(int data) {
                        DoublyNode newNode = new DoublyNode(data);
                        if (isEmpty()) {
                            head = tail = newNode;
                        } else {
                            tail.next = newNode;
                            newNode.prev = tail;
                            tail = newNode;
                        }
                        size++;
                    }
                    
                    // Remove first element - O(1)
                    public int removeFirst() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        int data = head.data;
                        head = head.next;
                        if (head != null) {
                            head.prev = null;
                        } else {
                            tail = null; // List becomes empty
                        }
                        size--;
                        return data;
                    }
                    
                    // Remove last element - O(1)
                    public int removeLast() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        int data = tail.data;
                        tail = tail.prev;
                        if (tail != null) {
                            tail.next = null;
                        } else {
                            head = null; // List becomes empty
                        }
                        size--;
                        return data;
                    }
                    
                    // Insert at specific position - O(n)
                    public void insertAtPosition(int position, int data) {
                        if (position < 0 || position > size) {
                            throw new IndexOutOfBoundsException("Invalid position: " + position);
                        }
                        
                        if (position == 0) {
                            addFirst(data);
                            return;
                        }
                        if (position == size) {
                            addLast(data);
                            return;
                        }
                        
                        DoublyNode newNode = new DoublyNode(data);
                        DoublyNode current = head;
                        
                        // Traverse to position
                        for (int i = 0; i < position; i++) {
                            current = current.next;
                        }
                        
                        // Insert new node before current
                        newNode.next = current;
                        newNode.prev = current.prev;
                        current.prev.next = newNode;
                        current.prev = newNode;
                        size++;
                    }
                    
                    // Delete at specific position - O(n)
                    public boolean deleteAtPosition(int position) {
                        if (position < 0 || position >= size || isEmpty()) {
                            return false;
                        }
                        
                        if (position == 0) {
                            removeFirst();
                            return true;
                        }
                        if (position == size - 1) {
                            removeLast();
                            return true;
                        }
                        
                        DoublyNode current = head;
                        // Traverse to position
                        for (int i = 0; i < position; i++) {
                            current = current.next;
                        }
                        
                        // Remove current node
                        current.prev.next = current.next;
                        current.next.prev = current.prev;
                        size--;
                        return true;
                    }
                    
                    // Search for element - O(n)
                    public int search(int data) {
                        DoublyNode current = head;
                        int position = 0;
                        
                        while (current != null) {
                            if (current.data == data) {
                                return position;
                            }
                            current = current.next;
                            position++;
                        }
                        return -1; // Not found
                    }
                    
                    // Get first element
                    public int getFirst() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        return head.data;
                    }
                    
                    // Get last element
                    public int getLast() {
                        if (isEmpty()) {
                            throw new RuntimeException("List is empty");
                        }
                        return tail.data;
                    }
                    
                    // Usage Example:
                    DoublyLinkedList dll = new DoublyLinkedList();
                    dll.addLast(10);
                    dll.addLast(20);
                    dll.addFirst(5);
                    dll.insertAtPosition(2, 15);
                    // Result: 5 <-> 10 <-> 15 <-> 20
                }
                """
        ));

        // ✅ NEW: Bidirectional Traversal
        codeExamples.put("Bidirectional Traversal", new CodeExample(
                "Doubly LinkedList Bidirectional Traversal",
                "Forward and backward traversal methods for Doubly LinkedList",
                "O(n)",
                """
                // Bidirectional traversal methods for Doubly LinkedList
                
                // Forward traversal (head to tail)
                public void traverseForward() {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    System.out.print("Forward: ");
                    DoublyNode current = head;
                    while (current != null) {
                        System.out.print(current.data + " ");
                        current = current.next;
                    }
                    System.out.println();
                }
                
                // Backward traversal (tail to head)
                public void traverseBackward() {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    System.out.print("Backward: ");
                    DoublyNode current = tail;
                    while (current != null) {
                        System.out.print(current.data + " ");
                        current = current.prev;
                    }
                    System.out.println();
                }
                
                // Recursive forward traversal
                public void traverseForwardRecursive() {
                    System.out.print("Forward (Recursive): ");
                    traverseForwardHelper(head);
                    System.out.println();
                }
                
                private void traverseForwardHelper(DoublyNode node) {
                    if (node != null) {
                        System.out.print(node.data + " ");
                        traverseForwardHelper(node.next);
                    }
                }
                
                // Recursive backward traversal
                public void traverseBackwardRecursive() {
                    System.out.print("Backward (Recursive): ");
                    traverseBackwardHelper(tail);
                    System.out.println();
                }
                
                private void traverseBackwardHelper(DoublyNode node) {
                    if (node != null) {
                        System.out.print(node.data + " ");
                        traverseBackwardHelper(node.prev);
                    }
                }
                
                // Traverse from specific position in both directions
                public void traverseFromPosition(int position) {
                    if (position < 0 || position >= size) {
                        System.out.println("Invalid position");
                        return;
                    }
                    
                    // Find the node at position
                    DoublyNode current = head;
                    for (int i = 0; i < position; i++) {
                        current = current.next;
                    }
                    
                    System.out.println("Traversing from position " + position + " (value: " + current.data + ")");
                    
                    // Traverse forward from position
                    System.out.print("Forward from position: ");
                    DoublyNode temp = current;
                    while (temp != null) {
                        System.out.print(temp.data + " ");
                        temp = temp.next;
                    }
                    System.out.println();
                    
                    // Traverse backward from position
                    System.out.print("Backward from position: ");
                    temp = current;
                    while (temp != null) {
                        System.out.print(temp.data + " ");
                        temp = temp.prev;
                    }
                    System.out.println();
                }
                
                // Display list with links
                public void displayWithLinks() {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    System.out.print("null <- ");
                    DoublyNode current = head;
                    while (current != null) {
                        System.out.print(current.data);
                        if (current.next != null) {
                            System.out.print(" <-> ");
                        } else {
                            System.out.print(" -> null");
                        }
                        current = current.next;
                    }
                    System.out.println();
                }
                
                // Usage Example:
                DoublyLinkedList dll = new DoublyLinkedList();
                dll.addLast(10);
                dll.addLast(20);
                dll.addLast(30);
                dll.addLast(40);
                
                dll.traverseForward();      // Forward: 10 20 30 40
                dll.traverseBackward();     // Backward: 40 30 20 10
                dll.displayWithLinks();     // null <- 10 <-> 20 <-> 30 <-> 40 -> null
                dll.traverseFromPosition(2); // Traverse from position 2 (value: 30)
                """
        ));

        // ✅ NEW: Doubly LinkedList Insert Operations
        codeExamples.put("Insert Operations", new CodeExample(
                "Doubly LinkedList Insert Operations",
                "Comprehensive insert operations for Doubly LinkedList with proper pointer management",
                "O(1) to O(n)",
                """
                // Advanced insert operations for Doubly LinkedList
                
                // Insert after a specific node
                public void insertAfter(DoublyNode node, int data) {
                    if (node == null) {
                        throw new IllegalArgumentException("Node cannot be null");
                    }
                    
                    DoublyNode newNode = new DoublyNode(data);
                    newNode.next = node.next;
                    newNode.prev = node;
                    
                    if (node.next != null) {
                        node.next.prev = newNode;
                    } else {
                        // node is the tail
                        tail = newNode;
                    }
                    node.next = newNode;
                    size++;
                }
                
                // Insert before a specific node
                public void insertBefore(DoublyNode node, int data) {
                    if (node == null) {
                        throw new IllegalArgumentException("Node cannot be null");
                    }
                    
                    DoublyNode newNode = new DoublyNode(data);
                    newNode.next = node;
                    newNode.prev = node.prev;
                    
                    if (node.prev != null) {
                        node.prev.next = newNode;
                    } else {
                        // node is the head
                        head = newNode;
                    }
                    node.prev = newNode;
                    size++;
                }
                
                // Insert at multiple positions
                public void insertMultiple(int[] positions, int[] values) {
                    if (positions.length != values.length) {
                        throw new IllegalArgumentException("Positions and values arrays must have same length");
                    }
                    
                    // Sort positions in descending order to avoid index shifting
                    for (int i = 0; i < positions.length - 1; i++) {
                        for (int j = i + 1; j < positions.length; j++) {
                            if (positions[i] < positions[j]) {
                                // Swap positions
                                int tempPos = positions[i];
                                positions[i] = positions[j];
                                positions[j] = tempPos;
                                
                                // Swap values
                                int tempVal = values[i];
                                values[i] = values[j];
                                values[j] = tempVal;
                            }
                        }
                    }
                    
                    // Insert in descending order of positions
                    for (int i = 0; i < positions.length; i++) {
                        insertAtPosition(positions[i], values[i]);
                    }
                }
                
                // Insert sorted (maintains sorted order)
                public void insertSorted(int data) {
                    if (isEmpty() || data <= head.data) {
                        addFirst(data);
                        return;
                    }
                    
                    if (data >= tail.data) {
                        addLast(data);
                        return;
                    }
                    
                    DoublyNode current = head;
                    while (current != null && current.data < data) {
                        current = current.next;
                    }
                    
                    insertBefore(current, data);
                }
                
                // Bulk insert at end
                public void insertAll(int[] values) {
                    for (int value : values) {
                        addLast(value);
                    }
                }
                
                // Bulk insert at beginning (reverse order)
                public void insertAllAtBeginning(int[] values) {
                    for (int i = values.length - 1; i >= 0; i--) {
                        addFirst(values[i]);
                    }
                }
                
                // Insert with condition
                public boolean insertIf(int data, java.util.function.Predicate<Integer> condition) {
                    if (condition.test(data)) {
                        addLast(data);
                        return true;
                    }
                    return false;
                }
                
                // Insert at middle
                public void insertAtMiddle(int data) {
                    int middlePos = size / 2;
                    insertAtPosition(middlePos, data);
                }
                
                // Usage Example:
                DoublyLinkedList dll = new DoublyLinkedList();
                
                // Basic inserts
                dll.addLast(10);
                dll.addLast(30);
                dll.addLast(50);
                
                // Insert after node with value 10
                DoublyNode nodeWith10 = dll.findNode(10);
                dll.insertAfter(nodeWith10, 20);
                
                // Insert sorted values
                dll.insertSorted(25);
                dll.insertSorted(5);
                
                // Bulk insert
                int[] values = {60, 70, 80};
                dll.insertAll(values);
                
                dll.traverseForward(); // Output: 5 10 20 25 30 50 60 70 80
                """
        ));

        // ✅ NEW: Doubly LinkedList Delete Operations
        codeExamples.put("Delete Operations", new CodeExample(
                "Doubly LinkedList Delete Operations",
                "Comprehensive delete operations for Doubly LinkedList with proper pointer management",
                "O(1) to O(n)",
                """
                // Advanced delete operations for Doubly LinkedList
                
                // Delete a specific node
                public boolean deleteNode(DoublyNode node) {
                    if (node == null) return false;
                    
                    // Update previous node's next pointer
                    if (node.prev != null) {
                        node.prev.next = node.next;
                    } else {
                        // node is head
                        head = node.next;
                    }
                    
                    // Update next node's previous pointer
                    if (node.next != null) {
                        node.next.prev = node.prev;
                    } else {
                        // node is tail
                        tail = node.prev;
                    }
                    
                    size--;
                    return true;
                }
                
                // Delete first occurrence of value
                public boolean delete(int data) {
                    DoublyNode current = head;
                    
                    while (current != null) {
                        if (current.data == data) {
                            return deleteNode(current);
                        }
                        current = current.next;
                    }
                    return false; // Value not found
                }
                
                // Delete all occurrences of value
                public int deleteAll(int data) {
                    int count = 0;
                    DoublyNode current = head;
                    
                    while (current != null) {
                        DoublyNode next = current.next;
                        if (current.data == data) {
                            deleteNode(current);
                            count++;
                        }
                        current = next;
                    }
                    return count;
                }
                
                // Delete nodes in range [start, end]
                public int deleteRange(int start, int end) {
                    if (start < 0 || end >= size || start > end) {
                        throw new IndexOutOfBoundsException("Invalid range");
                    }
                    
                    int count = 0;
                    for (int i = end; i >= start; i--) {
                        deleteAtPosition(i);
                        count++;
                    }
                    return count;
                }
                
                // Delete nodes with condition
                public int deleteIf(java.util.function.Predicate<Integer> condition) {
                    int count = 0;
                    DoublyNode current = head;
                    
                    while (current != null) {
                        DoublyNode next = current.next;
                        if (condition.test(current.data)) {
                            deleteNode(current);
                            count++;
                        }
                        current = next;
                    }
                    return count;
                }
                
                // Delete every nth node
                public int deleteEveryNth(int n) {
                    if (n <= 0) return 0;
                    
                    int count = 0;
                    DoublyNode current = head;
                    int position = 1;
                    
                    while (current != null) {
                        DoublyNode next = current.next;
                        if (position % n == 0) {
                            deleteNode(current);
                            count++;
                        }
                        current = next;
                        position++;
                    }
                    return count;
                }
                
                // Delete middle element(s)
                public int deleteMiddle() {
                    if (isEmpty()) return 0;
                    
                    int middlePos = size / 2;
                    deleteAtPosition(middlePos);
                    
                    // If even number of elements, delete both middle elements
                    if (size % 2 == 1 && size > 0) {
                        deleteAtPosition(middlePos - 1);
                        return 2;
                    }
                    return 1;
                }
                
                // Delete duplicates (keep first occurrence)
                public int deleteDuplicates() {
                    if (isEmpty()) return 0;
                    
                    int count = 0;
                    DoublyNode current = head;
                    
                    while (current != null) {
                        DoublyNode runner = current.next;
                        while (runner != null) {
                            DoublyNode nextRunner = runner.next;
                            if (runner.data == current.data) {
                                deleteNode(runner);
                                count++;
                            }
                            runner = nextRunner;
                        }
                        current = current.next;
                    }
                    return count;
                }
                
                // Clear all elements
                public void clear() {
                    DoublyNode current = head;
                    while (current != null) {
                        DoublyNode next = current.next;
                        current.prev = null;
                        current.next = null;
                        current = next;
                    }
                    head = tail = null;
                    size = 0;
                }
                
                // Delete nodes greater than value
                public int deleteGreaterThan(int value) {
                    return deleteIf(x -> x > value);
                }
                
                // Delete nodes less than value
                public int deleteLessThan(int value) {
                    return deleteIf(x -> x < value);
                }
                
                // Usage Example:
                DoublyLinkedList dll = new DoublyLinkedList();
                int[] values = {5, 10, 15, 10, 20, 25, 10, 30};
                dll.insertAll(values);
                
                // Delete all occurrences of 10
                int deleted = dll.deleteAll(10); // Returns 3
                
                // Delete range [1, 3]
                dll.deleteRange(1, 3);
                
                // Delete elements greater than 20
                dll.deleteGreaterThan(20);
                
                // Delete duplicates
                dll.deleteDuplicates();
                
                dll.traverseForward(); // Show remaining elements
                """
        ));

        // =============== CIRCULAR LINKEDLIST ===============

        // ✅ NEW: Circular LinkedList Node Class
        codeExamples.put("Node Class", new CodeExample(
                "Circular LinkedList Node",
                "Node class implementation for Circular LinkedList",
                "O(1)",
                """
                // Node class for Circular LinkedList
                public class CircularNode {
                    int data; // Data stored in the node
                    CircularNode next; // Reference to next node
                    
                    // Constructor
                    public CircularNode(int data) {
                        this.data = data;
                        this.next = null;
                    }
                    
                    // Constructor with next node
                    public CircularNode(int data, CircularNode next) {
                        this.data = data;
                        this.next = next;
                    }
                    
                    // Getter methods
                    public int getData() {
                        return data;
                    }
                    
                    public CircularNode getNext() {
                        return next;
                    }
                    
                    // Setter methods
                    public void setData(int data) {
                        this.data = data;
                    }
                    
                    public void setNext(CircularNode next) {
                        this.next = next;
                    }
                    
                    @Override
                    public String toString() {
                        return "CircularNode{" + data + "}";
                    }
                }
                
                // Alternative: Using generic Node class for Circular LinkedList
                public class Node {
                    int data;
                    Node next;
                    
                    public Node(int data) {
                        this.data = data;
                        this.next = null;
                    }
                }
                """
        ));

        // ✅ NEW: Circular LinkedList Complete Implementation
        codeExamples.put("Complete Implementation", new CodeExample(
                "Complete Circular LinkedList",
                "Full implementation of Circular LinkedList with all operations",
                "O(1) to O(n)",
                """
                // Complete Circular LinkedList Implementation
                public class CircularLinkedList {
                    private Node tail; // Points to last node (next points to first)
                    private int size;
                    
                    // Constructor
                    public CircularLinkedList() {
                        this.tail = null;
                        this.size = 0;
                    }
                    
                    // Check if list is empty
                    public boolean isEmpty() {
                        return tail == null;
                    }
                    
                    // Get size of list
                    public int size() {
                        return size;
                    }
                    
                    // Get head node
                    private Node getHead() {
                        return isEmpty() ? null : tail.next;
                    }
                    
                    // Insert at beginning - O(1)
                    public void insertAtBeginning(int data) {
                        Node newNode = new Node(data);
                        
                        if (isEmpty()) {
                            tail = newNode;
                            newNode.next = newNode; // Points to itself
                        } else {
                            newNode.next = tail.next; // New node points to current head
                            tail.next = newNode;      // Tail points to new node (new head)
                        }
                        size++;
                    }
                    
                    // Insert at end - O(1)
                    public void insertAtEnd(int data) {
                        Node newNode = new Node(data);
                        
                        if (isEmpty()) {
                            tail = newNode;
                            newNode.next = newNode; // Points to itself
                        } else {
                            newNode.next = tail.next; // New node points to head
                            tail.next = newNode;      // Current tail points to new node
                            tail = newNode;           // Update tail to new node
                        }
                        size++;
                    }
                    
                    // Insert at specific position - O(n)
                    public void insertAtPosition(int position, int data) {
                        if (position < 0 || position > size) {
                            throw new IndexOutOfBoundsException("Invalid position: " + position);
                        }
                        
                        if (position == 0) {
                            insertAtBeginning(data);
                            return;
                        }
                        
                        if (position == size) {
                            insertAtEnd(data);
                            return;
                        }
                        
                        Node newNode = new Node(data);
                        Node current = getHead();
                        
                        // Traverse to position - 1
                        for (int i = 0; i < position - 1; i++) {
                            current = current.next;
                        }
                        
                        newNode.next = current.next;
                        current.next = newNode;
                        size++;
                    }
                    
                    // Delete first element - O(1)
                    public boolean deleteFirst() {
                        if (isEmpty()) return false;
                        
                        if (size == 1) {
                            tail = null;
                        } else {
                            Node head = tail.next;
                            tail.next = head.next; // Tail now points to second node
                        }
                        size--;
                        return true;
                    }
                    
                    // Delete last element - O(n) because we need to find second-to-last
                    public boolean deleteLast() {
                        if (isEmpty()) return false;
                        
                        if (size == 1) {
                            tail = null;
                        } else {
                            Node current = getHead();
                            // Find second-to-last node
                            while (current.next != tail) {
                                current = current.next;
                            }
                            current.next = tail.next; // Skip tail node
                            tail = current;           // Update tail
                        }
                        size--;
                        return true;
                    }
                    
                    // Delete specific value - O(n)
                    public boolean delete(int data) {
                        if (isEmpty()) return false;
                        
                        Node head = getHead();
                        
                        // If head contains the data
                        if (head.data == data) {
                            return deleteFirst();
                        }
                        
                        Node current = head;
                        // Search for the node to delete
                        while (current.next != head && current.next.data != data) {
                            current = current.next;
                        }
                        
                        // If found
                        if (current.next.data == data) {
                            Node nodeToDelete = current.next;
                            current.next = nodeToDelete.next;
                            
                            // If deleting tail, update tail pointer
                            if (nodeToDelete == tail) {
                                tail = current;
                            }
                            size--;
                            return true;
                        }
                        
                        return false; // Not found
                    }
                    
                    // Search for element - O(n)
                    public int search(int data) {
                        if (isEmpty()) return -1;
                        
                        Node current = getHead();
                        int position = 0;
                        
                        do {
                            if (current.data == data) {
                                return position;
                            }
                            current = current.next;
                            position++;
                        } while (current != getHead());
                        
                        return -1; // Not found
                    }
                    
                    // Get element at position - O(n)
                    public int get(int position) {
                        if (position < 0 || position >= size) {
                            throw new IndexOutOfBoundsException("Invalid position: " + position);
                        }
                        
                        Node current = getHead();
                        for (int i = 0; i < position; i++) {
                            current = current.next;
                        }
                        return current.data;
                    }
                    
                    // Clear all elements
                    public void clear() {
                        if (!isEmpty()) {
                            // Break the circular reference
                            Node head = getHead();
                            tail.next = null;
                            tail = null;
                        }
                        size = 0;
                    }
                    
                    // Usage Example:
                    CircularLinkedList cll = new CircularLinkedList();
                    cll.insertAtEnd(10);
                    cll.insertAtEnd(20);
                    cll.insertAtBeginning(5);
                    cll.insertAtPosition(2, 15);
                    // Result: 5 -> 10 -> 15 -> 20 -> (back to 5)
                }
                """
        ));

        // ✅ NEW: Circular LinkedList Traversal
        codeExamples.put("Circular Traversal", new CodeExample(
                "Circular LinkedList Traversal",
                "Various traversal methods for Circular LinkedList including infinite loop handling",
                "O(n)",
                """
                // Circular traversal methods for Circular LinkedList
                
                // Basic circular traversal (one complete round)
                public void traverse() {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    Node current = getHead();
                    System.out.print("Circular List: ");
                    
                    do {
                        System.out.print(current.data + " ");
                        current = current.next;
                    } while (current != getHead());
                    
                    System.out.println("(back to " + getHead().data + ")");
                }
                
                // Traverse n complete rounds
                public void traverseRounds(int rounds) {
                    if (isEmpty() || rounds <= 0) {
                        System.out.println("List is empty or invalid rounds");
                        return;
                    }
                    
                    Node current = getHead();
                    System.out.print("Traversing " + rounds + " rounds: ");
                    
                    for (int round = 0; round < rounds; round++) {
                        do {
                            System.out.print(current.data + " ");
                            current = current.next;
                        } while (current != getHead());
                        System.out.print("| "); // Round separator
                    }
                    System.out.println();
                }
                
                // Traverse with limit (prevent infinite loops in debugging)
                public void traverseWithLimit(int maxNodes) {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    Node current = getHead();
                    int count = 0;
                    System.out.print("Limited traversal: ");
                    
                    do {
                        System.out.print(current.data + " ");
                        current = current.next;
                        count++;
                    } while (current != getHead() && count < maxNodes);
                    
                    if (count >= maxNodes) {
                        System.out.print("... (limit reached)");
                    }
                    System.out.println();
                }
                
                // Recursive traversal (one round)
                public void traverseRecursive() {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    System.out.print("Recursive traversal: ");
                    traverseRecursiveHelper(getHead(), getHead(), true);
                    System.out.println();
                }
                
                private void traverseRecursiveHelper(Node current, Node head, boolean isFirst) {
                    if (!isFirst && current == head) {
                        return; // Completed one round
                    }
                    
                    System.out.print(current.data + " ");
                    traverseRecursiveHelper(current.next, head, false);
                }
                
                // Find and display path between two values
                public void findPath(int start, int end) {
                    if (isEmpty()) {
                        System.out.println("List is empty");
                        return;
                    }
                    
                    // Find start position
                    int startPos = search(start);
                    if (startPos == -1) {
                        System.out.println("Start value " + start + " not found");
                        return;
                    }
                    
                    Node current = getHead();
                    // Move to start position
                    for (int i = 0; i < startPos; i++) {
                        current = current.next;
                    }
                    
                    System.out.print("Path from " + start + " to " + end + ": ");
                    int steps = 0;
                    Node startNode = current;
                    
                    do {
                        System.out.print(current.data + " ");
                        if (current.data == end) {
                            System.out.println("(Found in " + steps + " steps)");
                            return;
                        }
                        current = current.next;
                        steps++;
                    } while (current != startNode && steps < size * 2); // Prevent infinite loop
                    
                    System.out.println("(Not found)");
                }
                
                // Display with arrows showing circular nature
                public void displayCircular() {
                    if (isEmpty()) {
                        System.out.println("Empty circular list");
                        return;
                    }
                    
                    Node current = getHead();
                    System.out.print("Circular: ");
                    
                    do {
                        System.out.print(current.data);
                        current = current.next;
                        if (current != getHead()) {
                            System.out.print(" -> ");
                        } else {
                            System.out.print(" -> [" + current.data + "] (circular)");
                        }
                    } while (current != getHead());
                    
                    System.out.println();
                }
                
                // Count occurrences in one complete round
                public int countOccurrences(int data) {
                    if (isEmpty()) return 0;
                    
                    int count = 0;
                    Node current = getHead();
                    
                    do {
                        if (current.data == data) {
                            count++;
                        }
                        current = current.next;
                    } while (current != getHead());
                    
                    return count;
                }
                
                // Usage Example:
                CircularLinkedList cll = new CircularLinkedList();
                cll.insertAtEnd(10);
                cll.insertAtEnd(20);
                cll.insertAtEnd(30);
                cll.insertAtEnd(20);
                
                cll.traverse();                    // One complete round
                cll.traverseRounds(2);            // Two complete rounds
                cll.traverseWithLimit(10);        // Traverse with limit
                cll.displayCircular();            // Show circular nature
                cll.findPath(20, 30);             // Find path between values
                int count = cll.countOccurrences(20); // Count 20s in list
                """
        ));

        // ✅ NEW: Circular LinkedList Insert Operations
        codeExamples.put("Insert Operations", new CodeExample(
                "Circular LinkedList Insert Operations",
                "Advanced insert operations for Circular LinkedList maintaining circular property",
                "O(1) to O(n)",
                """
                // Advanced insert operations for Circular LinkedList
                
                // Insert after specific node
                public void insertAfter(Node targetNode, int data) {
                    if (targetNode == null) {
                        throw new IllegalArgumentException("Target node cannot be null");
                    }
                    
                    Node newNode = new Node(data);
                    newNode.next = targetNode.next;
                    targetNode.next = newNode;
                    
                    // Update tail if we inserted after current tail
                    if (targetNode == tail) {
                        tail = newNode;
                    }
                    size++;
                }
                
                // Insert after first occurrence of value
                public boolean insertAfterValue(int targetValue, int newData) {
                    if (isEmpty()) return false;
                    
                    Node current = getHead();
                    do {
                        if (current.data == targetValue) {
                            insertAfter(current, newData);
                            return true;
                        }
                        current = current.next;
                    } while (current != getHead());
                    
                    return false; // Target value not found
                }
                
                // Insert before specific node (requires traversal to find previous)
                public void insertBefore(Node targetNode, int data) {
                    if (targetNode == null) {
                        throw new IllegalArgumentException("Target node cannot be null");
                    }
                    
                    Node newNode = new Node(data);
                    
                    // Special case: inserting before head
                    if (targetNode == getHead()) {
                        insertAtBeginning(data);
                        return;
                    }
                    
                    // Find the node before target
                    Node current = getHead();
                    while (current.next != targetNode) {
                        current = current.next;
                    }
                    
                    newNode.next = targetNode;
                    current.next = newNode;
                    size++;
                }
                
                // Insert in sorted order (assumes list is sorted)
                public void insertSorted(int data) {
                    if (isEmpty() || data <= getHead().data) {
                        insertAtBeginning(data);
                        return;
                    }
                    
                    Node current = getHead();
                    // Find position to insert
                    while (current.next != getHead() && current.next.data < data) {
                        current = current.next;
                    }
                    
                    // If we reached the end and data is largest
                    if (current.next == getHead() && current.data < data) {
                        insertAtEnd(data);
                    } else {
                        insertAfter(current, data);
                    }
                }
                
                // Insert multiple values at once
                public void insertAll(int[] values) {
                    for (int value : values) {
                        insertAtEnd(value);
                    }
                }
                
                // Insert array at specific position
                public void insertArrayAt(int position, int[] values) {
                    if (position < 0 || position > size) {
                        throw new IndexOutOfBoundsException("Invalid position: " + position);
                    }
                    
                    for (int i = 0; i < values.length; i++) {
                        insertAtPosition(position + i, values[i]);
                    }
                }
                
                // Insert alternately (every nth position)
                public void insertAlternate(int[] values, int step) {
                    if (step <= 0) return;
                    
                    int position = 0;
                    for (int value : values) {
                        if (position <= size) {
                            insertAtPosition(position, value);
                            position += step + 1; // +1 because size increased
                        } else {
                            insertAtEnd(value);
                        }
                    }
                }
                
                // Insert with condition
                public boolean insertIf(int data, java.util.function.Predicate<Integer> condition) {
                    if (condition.test(data)) {
                        insertAtEnd(data);
                        return true;
                    }
                    return false;
                }
                
                // Split and insert (insert at middle of each pair)
                public void splitInsert(int data) {
                    if (size < 2) {
                        insertAtEnd(data);
                        return;
                    }
                    
                    int insertPositions = size / 2;
                    for (int i = 0; i < insertPositions; i++) {
                        insertAtPosition((i + 1) * 2 - 1 + i, data);
                    }
                }
                
                // Create node at specific data value (helper for insertAfter operations)
                public Node findNode(int data) {
                    if (isEmpty()) return null;
                    
                    Node current = getHead();
                    do {
                        if (current.data == data) {
                            return current;
                        }
                        current = current.next;
                    } while (current != getHead());
                    
                    return null; // Not found
                }
                
                // Usage Example:
                CircularLinkedList cll = new CircularLinkedList();
                cll.insertAtEnd(10);
                cll.insertAtEnd(30);
                cll.insertAtEnd(50);
                
                // Insert after value
                cll.insertAfterValue(10, 20); // Insert 20 after 10
                
                // Insert sorted values
                cll.insertSorted(25);
                cll.insertSorted(5);
                
                // Insert multiple values
                int[] values = {60, 70};
                cll.insertAll(values);
                
                // Insert array at specific position
                int[] moreValues = {35, 40};
                cll.insertArrayAt(5, moreValues);
                
                cll.traverse(); // Display final list
                """
        ));

        // ✅ NEW: Circular LinkedList Delete Operations
        codeExamples.put("Delete Operations", new CodeExample(
                "Circular LinkedList Delete Operations",
                "Advanced delete operations for Circular LinkedList maintaining circular property",
                "O(1) to O(n)",
                """
                // Advanced delete operations for Circular LinkedList
                
                // Delete node by reference
                public boolean deleteNode(Node nodeToDelete) {
                    if (isEmpty() || nodeToDelete == null) return false;
                    
                    Node head = getHead();
                    
                    // If only one node
                    if (size == 1 && head == nodeToDelete) {
                        tail = null;
                        size--;
                        return true;
                    }
                    
                    // If deleting head
                    if (nodeToDelete == head) {
                        return deleteFirst();
                    }
                    
                    // Find previous node
                    Node current = head;
                    while (current.next != nodeToDelete && current.next != head) {
                        current = current.next;
                    }
                    
                    // If node found
                    if (current.next == nodeToDelete) {
                        current.next = nodeToDelete.next;
                        
                        // Update tail if deleting tail
                        if (nodeToDelete == tail) {
                            tail = current;
                        }
                        size--;
                        return true;
                    }
                    
                    return false; // Node not found
                }
                
                // Delete all occurrences of a value
                public int deleteAll(int data) {
                    if (isEmpty()) return 0;
                    
                    int count = 0;
                    Node current = getHead();
                    Node startNode = current;
                    
                    do {
                        Node next = current.next;
                        if (current.data == data) {
                            if (deleteNode(current)) {
                                count++;
                                // Restart from head if we deleted nodes
                                current = isEmpty() ? null : getHead();
                                startNode = current;
                                continue;
                            }
                        }
                        current = next;
                    } while (current != null && current != startNode);
                    
                    return count;
                }
                
                // Delete every nth node
                public int deleteEveryNth(int n) {
                    if (isEmpty() || n <= 0) return 0;
                    
                    int count = 0;
                    Node current = getHead();
                    int position = 1;
                    
                    while (size > 0 && count < size) {
                        Node next = current.next;
                        if (position % n == 0) {
                            deleteNode(current);
                            count++;
                        }
                        current = next;
                        position++;
                        
                        // Prevent infinite loop
                        if (current == getHead()) {
                            position = 1;
                        }
                    }
                    
                    return count;
                }
                
                // Delete nodes in range [start, end]
                public int deleteRange(int start, int end) {
                    if (start < 0 || end >= size || start > end) {
                        throw new IndexOutOfBoundsException("Invalid range");
                    }
                    
                    int count = 0;
                    // Delete from end to start to avoid index shifting
                    for (int i = end; i >= start; i--) {
                        if (deleteAtPosition(i)) {
                            count++;
                        }
                    }
                    return count;
                }
                
                // Delete at position
                public boolean deleteAtPosition(int position) {
                    if (position < 0 || position >= size) {
                        return false;
                    }
                    
                    if (position == 0) {
                        return deleteFirst();
                    }
                    
                    Node current = getHead();
                    for (int i = 0; i < position; i++) {
                        current = current.next;
                    }
                    
                    return deleteNode(current);
                }
                
                // Delete nodes greater than value
                public int deleteGreaterThan(int value) {
                    return deleteIf(data -> data > value);
                }
                
                // Delete nodes less than value
                public int deleteLessThan(int value) {
                    return deleteIf(data -> data < value);
                }
                
                // Delete with condition
                public int deleteIf(java.util.function.Predicate<Integer> condition) {
                    if (isEmpty()) return 0;
                    
                    int count = 0;
                    Node current = getHead();
                    Node startNode = current;
                    boolean firstIteration = true;
                    
                    do {
                        Node next = current.next;
                        if (condition.test(current.data)) {
                            if (deleteNode(current)) {
                                count++;
                                // If list becomes empty, break
                                if (isEmpty()) break;
                                
                                // Reset to head after deletion
                                current = getHead();
                                startNode = current;
                                firstIteration = true;
                                continue;
                            }
                        }
                        current = next;
                        firstIteration = false;
                    } while (current != startNode || firstIteration);
                    
                    return count;
                }
                
                // Delete duplicates (keep first occurrence)
                public int deleteDuplicates() {
                    if (isEmpty()) return 0;
                    
                    int count = 0;
                    Node current = getHead();
                    Node outerStart = current;
                    
                    do {
                        Node runner = current.next;
                        Node runnerStart = runner;
                        
                        while (runner != outerStart) {
                            Node nextRunner = runner.next;
                            if (runner.data == current.data) {
                                deleteNode(runner);
                                count++;
                            }
                            runner = nextRunner;
                            
                            // If we've gone full circle, break
                            if (runner == runnerStart) break;
                        }
                        current = current.next;
                    } while (current != outerStart);
                    
                    return count;
                }
                
                // Delete middle element(s)
                public int deleteMiddle() {
                    if (isEmpty()) return 0;
                    
                    if (size == 1) {
                        clear();
                        return 1;
                    }
                    
                    int middlePos = size / 2;
                    int count = deleteAtPosition(middlePos) ? 1 : 0;
                    
                    // For even number of elements, delete both middle elements
                    if (size % 2 == 0 && size > 0) {
                        if (deleteAtPosition(middlePos - 1)) {
                            count++;
                        }
                    }
                    
                    return count;
                }
                
                // Josephus problem - delete every kth person
                public Node josephus(int k) {
                    if (isEmpty() || k <= 0) return null;
                    
                    Node current = getHead();
                    
                    // Continue until only one node remains
                    while (size > 1) {
                        // Move k-1 steps
                        for (int i = 1; i < k; i++) {
                            current = current.next;
                        }
                        
                        // Delete current node and move to next
                        Node next = current.next;
                        deleteNode(current);
                        current = next;
                    }
                    
                    return getHead(); // Return survivor
                }
                
                // Usage Example:
                CircularLinkedList cll = new CircularLinkedList();
                int[] values = {10, 20, 30, 20, 40, 50, 20};
                cll.insertAll(values);
                
                // Delete all occurrences of 20
                int deleted = cll.deleteAll(20); // Returns 3
                
                // Delete every 2nd node
                cll.deleteEveryNth(2);
                
                // Delete range [1, 2]
                cll.deleteRange(1, 2);
                
                // Delete elements greater than 35
                cll.deleteGreaterThan(35);
                
                cll.traverse(); // Show remaining elements
                """
        ));

        // =============== ADVANCED OPERATIONS ===============
        codeExamples.put("Reverse LinkedList", new CodeExample(
                "Reverse LinkedList",
                "Reverse a LinkedList iteratively and recursively",
                "O(n)",
                """
                // Iterative approach to reverse LinkedList
                public void reverseIterative() {
                    if (head == null || head.next == null) {
                        return;
                    }
                    
                    Node prev = null;
                    Node current = head;
                    Node next = null;
                    
                    while (current != null) {
                        next = current.next; // Store next
                        current.next = prev; // Reverse the link
                        prev = current;      // Move prev forward
                        current = next;      // Move current forward
                    }
                    
                    head = prev; // Update head to new first node
                }
                
                // Recursive approach to reverse LinkedList
                public void reverseRecursive() {
                    head = reverseRecursiveHelper(head);
                }
                
                private Node reverseRecursiveHelper(Node node) {
                    // Base case
                    if (node == null || node.next == null) {
                        return node;
                    }
                    
                    // Recursively reverse the rest of the list
                    Node newHead = reverseRecursiveHelper(node.next);
                    
                    // Reverse the current link
                    node.next.next = node;
                    node.next = null;
                    
                    return newHead;
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(20);
                list.insertAtEnd(30);
                // Before: 10 -> 20 -> 30 -> null
                list.reverseIterative();
                // After: 30 -> 20 -> 10 -> null
                """
        ));

        codeExamples.put("Merge Two Lists", new CodeExample(
                "Merge Two Sorted Lists",
                "Merge two sorted LinkedLists into one sorted LinkedList",
                "O(m + n)",
                """
                // Merge two sorted LinkedLists
                public static LinkedList mergeSortedLists(LinkedList list1, LinkedList list2) {
                    LinkedList mergedList = new LinkedList();
                    Node ptr1 = list1.head;
                    Node ptr2 = list2.head;
                    
                    // Compare and merge
                    while (ptr1 != null && ptr2 != null) {
                        if (ptr1.data <= ptr2.data) {
                            mergedList.insertAtEnd(ptr1.data);
                            ptr1 = ptr1.next;
                        } else {
                            mergedList.insertAtEnd(ptr2.data);
                            ptr2 = ptr2.next;
                        }
                    }
                    
                    // Add remaining elements
                    while (ptr1 != null) {
                        mergedList.insertAtEnd(ptr1.data);
                        ptr1 = ptr1.next;
                    }
                    
                    while (ptr2 != null) {
                        mergedList.insertAtEnd(ptr2.data);
                        ptr2 = ptr2.next;
                    }
                    
                    return mergedList;
                }
                
                // In-place merge (modifies original lists)
                public static Node mergeSortedNodes(Node l1, Node l2) {
                    Node dummy = new Node(0);
                    Node current = dummy;
                    
                    while (l1 != null && l2 != null) {
                        if (l1.data <= l2.data) {
                            current.next = l1;
                            l1 = l1.next;
                        } else {
                            current.next = l2;
                            l2 = l2.next;
                        }
                        current = current.next;
                    }
                    
                    // Attach remaining nodes
                    current.next = (l1 != null) ? l1 : l2;
                    return dummy.next;
                }
                
                // Usage Example:
                LinkedList list1 = new LinkedList();
                list1.insertAtEnd(1);
                list1.insertAtEnd(3);
                list1.insertAtEnd(5);
                
                LinkedList list2 = new LinkedList();
                list2.insertAtEnd(2);
                list2.insertAtEnd(4);
                list2.insertAtEnd(6);
                
                LinkedList merged = mergeSortedLists(list1, list2);
                // Result: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> null
                """
        ));

        codeExamples.put("Find Middle Element", new CodeExample(
                "Find Middle Element",
                "Find the middle element of LinkedList using slow-fast pointer technique",
                "O(n)",
                """
                // Find middle element using two pointers
                public int findMiddle() {
                    if (head == null) {
                        throw new RuntimeException("List is empty");
                    }
                    
                    Node slow = head;
                    Node fast = head;
                    
                    // Move slow by 1 and fast by 2
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                    }
                    
                    return slow.data;
                }
                
                // Find middle node (returns the node)
                public Node findMiddleNode() {
                    if (head == null) {
                        return null;
                    }
                    
                    Node slow = head;
                    Node fast = head;
                    
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                    }
                    
                    return slow;
                }
                
                // Find all middle elements (for even sized lists)
                public int[] findAllMiddleElements() {
                    if (head == null) {
                        return new int[0];
                    }
                    
                    Node slow = head;
                    Node fast = head;
                    Node prevSlow = null;
                    
                    while (fast != null && fast.next != null) {
                        prevSlow = slow;
                        slow = slow.next;
                        fast = fast.next.next;
                    }
                    
                    // If list has even number of elements
                    if (fast == null && prevSlow != null) {
                        return new int[]{prevSlow.data, slow.data};
                    } else {
                        return new int[]{slow.data};
                    }
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(10);
                list.insertAtEnd(20);
                list.insertAtEnd(30);
                list.insertAtEnd(40);
                list.insertAtEnd(50);
                int middle = list.findMiddle(); // Returns 30
                """
        ));

        codeExamples.put("Detect Cycle", new CodeExample(
                "Detect Cycle in LinkedList",
                "Detect if there's a cycle in LinkedList using Floyd's algorithm",
                "O(n)",
                """
                // Detect cycle using Floyd's Cycle Detection Algorithm
                public boolean hasCycle() {
                    if (head == null || head.next == null) {
                        return false;
                    }
                    
                    Node slow = head;
                    Node fast = head;
                    
                    // Move slow by 1 and fast by 2
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                        
                        // If they meet, there's a cycle
                        if (slow == fast) {
                            return true;
                        }
                    }
                    
                    return false;
                }
                
                // Find the start of the cycle
                public Node findCycleStart() {
                    if (head == null || !hasCycle()) {
                        return null;
                    }
                    
                    Node slow = head;
                    Node fast = head;
                    
                    // Find meeting point
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                        if (slow == fast) {
                            break;
                        }
                    }
                    
                    // Move slow to head and keep fast at meeting point
                    slow = head;
                    while (slow != fast) {
                        slow = slow.next;
                        fast = fast.next;
                    }
                    
                    return slow; // Start of cycle
                }
                
                // Get cycle length
                public int getCycleLength() {
                    if (!hasCycle()) {
                        return 0;
                    }
                    
                    Node slow = head;
                    Node fast = head;
                    
                    // Find meeting point
                    while (fast != null && fast.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                        if (slow == fast) {
                            break;
                        }
                    }
                    
                    // Count nodes in cycle
                    int length = 1;
                    Node current = slow.next;
                    while (current != slow) {
                        current = current.next;
                        length++;
                    }
                    
                    return length;
                }
                
                // Remove cycle if exists
                public void removeCycle() {
                    if (!hasCycle()) {
                        return;
                    }
                    
                    Node cycleStart = findCycleStart();
                    Node current = cycleStart;
                    
                    // Find the node just before cycle start
                    while (current.next != cycleStart) {
                        current = current.next;
                    }
                    
                    // Break the cycle
                    current.next = null;
                }
                """
        ));

        // ✅ NEW: Remove Duplicates
        codeExamples.put("Remove Duplicates", new CodeExample(
                "Remove Duplicates from LinkedList",
                "Remove duplicate elements from LinkedList using different approaches",
                "O(n) to O(n²)",
                """
                // Remove duplicates from unsorted LinkedList - O(n²) time, O(1) space
                public void removeDuplicates() {
                    if (head == null) return;
                    
                    Node current = head;
                    
                    while (current != null) {
                        Node runner = current;
                        
                        // Remove all duplicates of current.data
                        while (runner.next != null) {
                            if (runner.next.data == current.data) {
                                runner.next = runner.next.next;
                                size--;
                            } else {
                                runner = runner.next;
                            }
                        }
                        current = current.next;
                    }
                }
                
                // Remove duplicates from sorted LinkedList - O(n) time, O(1) space
                public void removeDuplicatesSorted() {
                    if (head == null) return;
                    
                    Node current = head;
                    
                    while (current.next != null) {
                        if (current.data == current.next.data) {
                            current.next = current.next.next;
                            size--;
                        } else {
                            current = current.next;
                        }
                    }
                }
                
                // Remove duplicates using HashSet - O(n) time, O(n) space
                public void removeDuplicatesWithSet() {
                    if (head == null) return;
                    
                    java.util.Set<Integer> seen = new java.util.HashSet<>();
                    Node current = head;
                    Node prev = null;
                    
                    while (current != null) {
                        if (seen.contains(current.data)) {
                            // Remove duplicate
                            prev.next = current.next;
                            size--;
                        } else {
                            seen.add(current.data);
                            prev = current;
                        }
                        current = current.next;
                    }
                }
                
                // Remove all duplicates (keep no occurrence) - for sorted list
                public void removeAllDuplicates() {
                    if (head == null) return;
                    
                    Node dummy = new Node(0);
                    dummy.next = head;
                    Node prev = dummy;
                    Node current = head;
                    
                    while (current != null) {
                        // Check if current has duplicates
                        if (current.next != null && current.data == current.next.data) {
                            int duplicateValue = current.data;
                            
                            // Skip all nodes with duplicate value
                            while (current != null && current.data == duplicateValue) {
                                current = current.next;
                                size--;
                            }
                            prev.next = current;
                        } else {
                            prev = current;
                            current = current.next;
                        }
                    }
                    head = dummy.next;
                }
                
                // Count duplicate removals
                public int removeDuplicatesCount() {
                    if (head == null) return 0;
                    
                    int removedCount = 0;
                    java.util.Set<Integer> seen = new java.util.HashSet<>();
                    Node current = head;
                    Node prev = null;
                    
                    while (current != null) {
                        if (seen.contains(current.data)) {
                            prev.next = current.next;
                            removedCount++;
                            size--;
                        } else {
                            seen.add(current.data);
                            prev = current;
                        }
                        current = current.next;
                    }
                    
                    return removedCount;
                }
                
                // Remove duplicates keeping last occurrence
                public void removeDuplicatesKeepLast() {
                    if (head == null) return;
                    
                    java.util.Map<Integer, Node> lastOccurrence = new java.util.HashMap<>();
                    java.util.Set<Integer> duplicates = new java.util.HashSet<>();
                    
                    Node current = head;
                    while (current != null) {
                        if (lastOccurrence.containsKey(current.data)) {
                            duplicates.add(current.data);
                        }
                        lastOccurrence.put(current.data, current);
                        current = current.next;
                    }
                    
                    // Remove all but last occurrence of duplicates
                    Node dummy = new Node(0);
                    dummy.next = head;
                    Node prev = dummy;
                    current = head;
                    
                    while (current != null) {
                        if (duplicates.contains(current.data) && 
                            current != lastOccurrence.get(current.data)) {
                            prev.next = current.next;
                            size--;
                        } else {
                            prev = current;
                        }
                        current = current.next;
                    }
                    head = dummy.next;
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                list.insertAtEnd(1);
                list.insertAtEnd(2);
                list.insertAtEnd(3);
                list.insertAtEnd(2);
                list.insertAtEnd(4);
                list.insertAtEnd(1);
                // Before: 1 -> 2 -> 3 -> 2 -> 4 -> 1 -> null
                
                list.removeDuplicates();
                // After: 1 -> 2 -> 3 -> 4 -> null
                """
        ));

        // ✅ NEW: Clone List
        codeExamples.put("Clone List", new CodeExample(
                "Clone LinkedList",
                "Create a deep copy of LinkedList including complex cloning scenarios",
                "O(n)",
                """
                // Simple clone of LinkedList
                public LinkedList clone() {
                    LinkedList clonedList = new LinkedList();
                    Node current = head;
                    
                    while (current != null) {
                        clonedList.insertAtEnd(current.data);
                        current = current.next;
                    }
                    
                    return clonedList;
                }
                
                // Clone using recursion
                public LinkedList cloneRecursive() {
                    LinkedList clonedList = new LinkedList();
                    clonedList.head = cloneRecursiveHelper(head);
                    clonedList.size = this.size;
                    return clonedList;
                }
                
                private Node cloneRecursiveHelper(Node node) {
                    if (node == null) {
                        return null;
                    }
                    
                    Node newNode = new Node(node.data);
                    newNode.next = cloneRecursiveHelper(node.next);
                    return newNode;
                }
                
                // Clone with random pointers (complex cloning)
                public static class NodeWithRandom {
                    int data;
                    NodeWithRandom next;
                    NodeWithRandom random; // Points to any node in the list
                    
                    public NodeWithRandom(int data) {
                        this.data = data;
                        this.next = null;
                        this.random = null;
                    }
                }
                
                public static class LinkedListWithRandom {
                    private NodeWithRandom head;
                    
                    // Clone list with random pointers - O(n) time, O(n) space
                    public LinkedListWithRandom cloneWithRandom() {
                        if (head == null) return new LinkedListWithRandom();
                        
                        java.util.Map<NodeWithRandom, NodeWithRandom> nodeMap = new java.util.HashMap<>();
                        
                        // First pass: create all nodes
                        NodeWithRandom current = head;
                        while (current != null) {
                            nodeMap.put(current, new NodeWithRandom(current.data));
                            current = current.next;
                        }
                        
                        // Second pass: set next and random pointers
                        current = head;
                        while (current != null) {
                            NodeWithRandom clonedNode = nodeMap.get(current);
                            clonedNode.next = nodeMap.get(current.next);
                            clonedNode.random = nodeMap.get(current.random);
                            current = current.next;
                        }
                        
                        LinkedListWithRandom clonedList = new LinkedListWithRandom();
                        clonedList.head = nodeMap.get(head);
                        return clonedList;
                    }
                    
                    // Clone with random pointers - O(n) time, O(1) space
                    public LinkedListWithRandom cloneWithRandomOptimized() {
                        if (head == null) return new LinkedListWithRandom();
                        
                        // Step 1: Create cloned nodes and insert after original nodes
                        NodeWithRandom current = head;
                        while (current != null) {
                            NodeWithRandom cloned = new NodeWithRandom(current.data);
                            cloned.next = current.next;
                            current.next = cloned;
                            current = cloned.next;
                        }
                        
                        // Step 2: Set random pointers for cloned nodes
                        current = head;
                        while (current != null) {
                            if (current.random != null) {
                                current.next.random = current.random.next;
                            }
                            current = current.next.next;
                        }
                        
                        // Step 3: Separate original and cloned lists
                        LinkedListWithRandom clonedList = new LinkedListWithRandom();
                        NodeWithRandom originalCurrent = head;
                        NodeWithRandom clonedCurrent = head.next;
                        clonedList.head = clonedCurrent;
                        
                        while (originalCurrent != null) {
                            originalCurrent.next = clonedCurrent.next;
                            originalCurrent = originalCurrent.next;
                            
                            if (originalCurrent != null) {
                                clonedCurrent.next = originalCurrent.next;
                                clonedCurrent = clonedCurrent.next;
                            }
                        }
                        
                        return clonedList;
                    }
                }
                
                // Deep clone with custom data objects
                public static class LinkedListWithObjects {
                    private NodeWithObject head;
                    
                    private static class NodeWithObject {
                        CustomObject data;
                        NodeWithObject next;
                        
                        public NodeWithObject(CustomObject data) {
                            this.data = data.clone(); // Assume CustomObject implements Cloneable
                            this.next = null;
                        }
                    }
                    
                    // Clone preserving object references vs deep cloning objects
                    public LinkedListWithObjects deepClone() {
                        LinkedListWithObjects clonedList = new LinkedListWithObjects();
                        NodeWithObject current = head;
                        
                        while (current != null) {
                            clonedList.insertObject(current.data.clone());
                            current = current.next;
                        }
                        
                        return clonedList;
                    }
                    
                    private void insertObject(CustomObject obj) {
                        NodeWithObject newNode = new NodeWithObject(obj);
                        if (head == null) {
                            head = newNode;
                        } else {
                            NodeWithObject current = head;
                            while (current.next != null) {
                                current = current.next;
                            }
                            current.next = newNode;
                        }
                    }
                }
                
                // Clone and reverse
                public LinkedList cloneAndReverse() {
                    LinkedList cloned = clone();
                    cloned.reverseIterative();
                    return cloned;
                }
                
                // Partial clone (first n elements)
                public LinkedList cloneFirst(int n) {
                    LinkedList clonedList = new LinkedList();
                    Node current = head;
                    int count = 0;
                    
                    while (current != null && count < n) {
                        clonedList.insertAtEnd(current.data);
                        current = current.next;
                        count++;
                    }
                    
                    return clonedList;
                }
                
                // Clone with transformation
                public LinkedList cloneWithTransform(java.util.function.Function<Integer, Integer> transformer) {
                    LinkedList clonedList = new LinkedList();
                    Node current = head;
                    
                    while (current != null) {
                        clonedList.insertAtEnd(transformer.apply(current.data));
                        current = current.next;
                    }
                    
                    return clonedList;
                }
                
                // Usage Example:
                LinkedList original = new LinkedList();
                original.insertAtEnd(10);
                original.insertAtEnd(20);
                original.insertAtEnd(30);
                
                // Simple clone
                LinkedList cloned = original.clone();
                
                // Clone and reverse
                LinkedList clonedReversed = original.cloneAndReverse();
                
                // Clone first 2 elements
                LinkedList partial = original.cloneFirst(2);
                
                // Clone with transformation (multiply by 2)
                LinkedList transformed = original.cloneWithTransform(x -> x * 2);
                """
        ));

        // ✅ NEW: Sort LinkedList
        codeExamples.put("Sort LinkedList", new CodeExample(
                "Sort LinkedList",
                "Sort LinkedList using various algorithms including Merge Sort, optimized for linked structures",
                "O(n log n)",
                """
                // Sort LinkedList using Merge Sort (most efficient for linked lists)
                public void sort() {
                    head = mergeSort(head);
                }
                
                private Node mergeSort(Node h) {
                    // Base case
                    if (h == null || h.next == null) {
                        return h;
                    }
                    
                    // Get middle of the list
                    Node middle = getMiddle(h);
                    Node nextOfMiddle = middle.next;
                    
                    // Split the list into two halves
                    middle.next = null;
                    
                    // Apply mergeSort on left list
                    Node left = mergeSort(h);
                    
                    // Apply mergeSort on right list
                    Node right = mergeSort(nextOfMiddle);
                    
                    // Merge the left and right lists
                    Node sortedList = sortedMerge(left, right);
                    return sortedList;
                }
                
                private Node sortedMerge(Node a, Node b) {
                    Node result = null;
                    
                    // Base cases
                    if (a == null) return b;
                    if (b == null) return a;
                    
                    // Recursively merge
                    if (a.data <= b.data) {
                        result = a;
                        result.next = sortedMerge(a.next, b);
                    } else {
                        result = b;
                        result.next = sortedMerge(a, b.next);
                    }
                    
                    return result;
                }
                
                private Node getMiddle(Node head) {
                    if (head == null) return head;
                    
                    Node slow = head;
                    Node fast = head.next;
                    
                    // Move fast pointer two steps and slow pointer one step
                    while (fast != null) {
                        fast = fast.next;
                        if (fast != null) {
                            slow = slow.next;
                            fast = fast.next;
                        }
                    }
                    
                    return slow;
                }
                
                // Bubble sort for LinkedList - O(n²) but simple
                public void bubbleSort() {
                    if (head == null || head.next == null) return;
                    
                    boolean swapped;
                    do {
                        swapped = false;
                        Node current = head;
                        
                        while (current.next != null) {
                            if (current.data > current.next.data) {
                                // Swap data
                                int temp = current.data;
                                current.data = current.next.data;
                                current.next.data = temp;
                                swapped = true;
                            }
                            current = current.next;
                        }
                    } while (swapped);
                }
                
                // Selection sort for LinkedList - O(n²)
                public void selectionSort() {
                    Node current = head;
                    
                    while (current != null) {
                        Node min = current;
                        Node r = current.next;
                        
                        // Find minimum element in remaining list
                        while (r != null) {
                            if (r.data < min.data) {
                                min = r;
                            }
                            r = r.next;
                        }
                        
                        // Swap current with minimum
                        int temp = current.data;
                        current.data = min.data;
                        min.data = temp;
                        
                        current = current.next;
                    }
                }
                
                // Insertion sort for LinkedList - O(n²) but efficient for small lists
                public void insertionSort() {
                    if (head == null || head.next == null) return;
                    
                    Node sorted = null;
                    Node current = head;
                    
                    while (current != null) {
                        Node next = current.next;
                        sorted = sortedInsert(sorted, current);
                        current = next;
                    }
                    
                    head = sorted;
                }
                
                private Node sortedInsert(Node sorted, Node newNode) {
                    // If sorted list is empty or new node should be first
                    if (sorted == null || sorted.data >= newNode.data) {
                        newNode.next = sorted;
                        sorted = newNode;
                    } else {
                        Node current = sorted;
                        
                        // Find the correct position to insert
                        while (current.next != null && current.next.data < newNode.data) {
                            current = current.next;
                        }
                        
                        newNode.next = current.next;
                        current.next = newNode;
                    }
                    
                    return sorted;
                }
                
                // Quick sort for LinkedList - O(n log n) average case
                public void quickSort() {
                    head = quickSortRecursive(head, getTail(head));
                }
                
                private Node quickSortRecursive(Node start, Node end) {
                    if (start == null || start == end || start == end.next) {
                        return start;
                    }
                    
                    // Partition the list and get pivot
                    Node[] partitioned = partition(start, end);
                    Node pivotPrev = partitioned[0];
                    Node pivot = partitioned[1];
                    
                    // If pivot is the smallest element, no need to recur for left part
                    if (pivotPrev != null) {
                        pivotPrev.next = null;
                        start = quickSortRecursive(start, pivotPrev);
                        
                        // Connect sorted left part with pivot
                        Node temp = start;
                        while (temp.next != null) {
                            temp = temp.next;
                        }
                        temp.next = pivot;
                    }
                    
                    // Recur for right part
                    pivot.next = quickSortRecursive(pivot.next, end);
                    
                    return start;
                }
                
                private Node[] partition(Node start, Node end) {
                    Node pivot = end;
                    Node pivotPrev = null;
                    Node current = start;
                    Node tail = pivot;
                    
                    while (current != pivot) {
                        if (current.data < pivot.data) {
                            if (pivotPrev == null) {
                                pivotPrev = current;
                            }
                            current = current.next;
                        } else {
                            // Move current to end
                            if (pivotPrev != null) {
                                pivotPrev.next = current.next;
                            }
                            Node temp = current.next;
                            current.next = null;
                            tail.next = current;
                            tail = current;
                            current = temp;
                        }
                    }
                    
                    return new Node[]{pivotPrev, pivot};
                }
                
                private Node getTail(Node node) {
                    while (node != null && node.next != null) {
                        node = node.next;
                    }
                    return node;
                }
                
                // Sort in descending order
                public void sortDescending() {
                    sort(); // First sort in ascending order
                    reverseIterative(); // Then reverse
                }
                
                // Custom sort with comparator
                public void sort(java.util.Comparator<Integer> comparator) {
                    if (head == null || head.next == null) return;
                    
                    // Convert to array, sort, and rebuild
                    int[] values = toArray();
                    java.util.Arrays.sort(values, 0, values.length, comparator);
                    
                    // Rebuild list
                    clear();
                    for (int value : values) {
                        insertAtEnd(value);
                    }
                }
                
                // Check if list is sorted
                public boolean isSorted() {
                    if (head == null || head.next == null) return true;
                    
                    Node current = head;
                    while (current.next != null) {
                        if (current.data > current.next.data) {
                            return false;
                        }
                        current = current.next;
                    }
                    return true;
                }
                
                // Usage Example:
                LinkedList list = new LinkedList();
                int[] values = {64, 34, 25, 12, 22, 11, 90};
                
                for (int value : values) {
                    list.insertAtEnd(value);
                }
                
                // Before: 64 -> 34 -> 25 -> 12 -> 22 -> 11 -> 90 -> null
                list.sort(); // Using merge sort
                // After: 11 -> 12 -> 22 -> 25 -> 34 -> 64 -> 90 -> null
                
                // Sort in descending order
                list.sortDescending();
                // Result: 90 -> 64 -> 34 -> 25 -> 22 -> 12 -> 11 -> null
                """
        ));
    }

    public CodeExample getCodeExample(String name) {
        return codeExamples.get(name);
    }

    public String getWelcomeMessage() {
        return """
        /*
         * ================================================================
         * LinkedList Code Repository - Welcome
         * ================================================================
         
         * This comprehensive collection includes:
         
         * 📚 Basic Operations:
         * • Create, Insert, Delete, Search operations
         * • Position-based operations
         * • Display and utility methods
         
         * ➡️ Singly LinkedList:
         * • Complete implementation with Node class
         * • Forward traversal methods
         * • All basic operations optimized
         
         * ↔️ Doubly LinkedList:
         * • Bidirectional navigation
         * • Enhanced insert/delete operations
         * • Forward and backward traversal
         
         * 🔄 Circular LinkedList:
         * • Circular navigation patterns
         * • Specialized insert/delete operations
         * • Infinite loop handling
         
         * ⚡ Advanced Operations:
         * • List reversal (iterative & recursive)
         * • Merge sorted lists
         * • Cycle detection (Floyd's algorithm)
         * • Find middle element
         * • Remove duplicates
         * • Clone operations
         
         * 💡 Features:
         * • Copy any code snippet to clipboard
         * • Time & space complexity analysis
         * • Usage examples and explanations
         * • Production-ready implementations
         
         * Select any category from the left panel to begin exploring!
         
         * Happy Coding! 🚀
         */
        """;
    }

    // Inner class to represent a code example
    public static class CodeExample {
        private final String title;
        private final String description;
        private final String complexity;
        private final String code;

        public CodeExample(String title, String description, String complexity, String code) {
            this.title = title;
            this.description = description;
            this.complexity = complexity;
            this.code = code;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getComplexity() { return complexity; }
        public String getCode() { return code; }
    }
}
