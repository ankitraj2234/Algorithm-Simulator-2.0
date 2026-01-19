package com.simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository of Queue code examples with comprehensive implementations.
 */
public class QueueCodeRepository {
    private final Map<String, CodeExample> codeExamples;

    public QueueCodeRepository() {
        codeExamples = new HashMap<>();
        initializeCodeExamples();
    }

    private void initializeCodeExamples() {
        // =============== BASIC QUEUE OPERATIONS ===============
        codeExamples.put("Create Queue", new CodeExample(
                "Create Queue",
                "Basic Queue class structure with array/list implementation",
                "O(1)",
                """
                        // Queue implementation using ArrayList
                        import java.util.ArrayList;
                        import java.util.NoSuchElementException;

                        public class Queue<T> {
                            private ArrayList<T> queue;
                            private int maxSize;

                            // Constructor
                            public Queue() {
                                this(100); // Default max size
                            }

                            public Queue(int maxSize) {
                                this.maxSize = maxSize;
                                this.queue = new ArrayList<>();
                            }

                            // Check if queue is empty
                            public boolean isEmpty() {
                                return queue.isEmpty();
                            }

                            // Check if queue is full
                            public boolean isFull() {
                                return queue.size() >= maxSize;
                            }

                            // Get current size
                            public int size() {
                                return queue.size();
                            }

                            // Get capacity
                            public int getCapacity() {
                                return maxSize;
                            }

                            // Display queue contents
                            public void display() {
                                if (isEmpty()) {
                                    System.out.println("Queue is empty");
                                    return;
                                }
                                System.out.println("Queue contents (front to rear): " + queue);
                            }
                        }

                        // Usage Example:
                        Queue<Integer> queue = new Queue<>(10);
                        System.out.println("Empty: " + queue.isEmpty()); // true
                        System.out.println("Capacity: " + queue.getCapacity()); // 10
                        """));

        codeExamples.put("Enqueue Operation", new CodeExample(
                "Enqueue Operation",
                "Add element to the rear of the queue",
                "O(1)",
                """
                        // Enqueue element to queue (add to rear)
                        public void enqueue(T element) {
                            // Check for queue overflow
                            if (isFull()) {
                                throw new IllegalStateException("Queue Overflow: Cannot enqueue to full queue");
                            }

                            // Add element to rear of queue
                            queue.add(element);
                            System.out.println("Enqueued: " + element + " | Size: " + size());
                        }

                        // Bulk enqueue operation
                        public void enqueueAll(T[] elements) {
                            for (T element : elements) {
                                if (!isFull()) {
                                    enqueue(element);
                                } else {
                                    System.out.println("Queue full! Cannot enqueue: " + element);
                                    break;
                                }
                            }
                        }

                        // Safe enqueue (returns boolean instead of exception)
                        public boolean safeEnqueue(T element) {
                            if (isFull()) {
                                return false;
                            }
                            queue.add(element);
                            return true;
                        }

                        // Usage Example:
                        Queue<Integer> queue = new Queue<>(5);
                        queue.enqueue(10);
                        queue.enqueue(20);
                        queue.enqueue(30);
                        // Queue: [10, 20, 30] (10 is front, 30 is rear)

                        Integer[] values = {40, 50};
                        queue.enqueueAll(values);

                        boolean success = queue.safeEnqueue(60); // Returns false if full
                        """));

        codeExamples.put("Dequeue Operation", new CodeExample(
                "Dequeue Operation",
                "Remove and return element from the front of the queue",
                "O(1)",
                """
                        // Dequeue element from queue (remove from front)
                        public T dequeue() {
                            // Check for queue underflow
                            if (isEmpty()) {
                                throw new NoSuchElementException("Queue Underflow: Cannot dequeue from empty queue");
                            }

                            // Remove and return front element
                            T element = queue.remove(0);
                            System.out.println("Dequeued: " + element + " | Size: " + size());
                            return element;
                        }

                        // Safe dequeue (returns null instead of exception)
                        public T safeDequeue() {
                            if (isEmpty()) {
                                return null;
                            }
                            return queue.remove(0);
                        }

                        // Dequeue multiple elements
                        public List<T> dequeueMultiple(int count) {
                            List<T> result = new ArrayList<>();
                            int actualCount = Math.min(count, size());

                            for (int i = 0; i < actualCount; i++) {
                                result.add(dequeue());
                            }
                            return result;
                        }

                        // Usage Example:
                        Queue<Integer> queue = new Queue<>();
                        queue.enqueue(10);
                        queue.enqueue(20);
                        queue.enqueue(30);

                        int dequeuedElement = queue.dequeue(); // Returns 10
                        // Queue: [20, 30] (20 is now front)

                        Integer safeDequeued = queue.safeDequeue(); // Returns 20 or null
                        List<Integer> multiple = queue.dequeueMultiple(2); // Dequeue 2 elements
                        """));

        codeExamples.put("Front Operation", new CodeExample(
                "Front/Peek Operation",
                "View front element without removing it from queue",
                "O(1)",
                """
                        // Peek at front element without removing
                        public T front() {
                            // Check if queue is empty
                            if (isEmpty()) {
                                throw new NoSuchElementException("Queue is empty: No front element");
                            }

                            // Return front element without removing
                            T frontElement = queue.get(0);
                            System.out.println("Front element: " + frontElement);
                            return frontElement;
                        }

                        // Alternative: peek() method (same functionality)
                        public T peek() {
                            return front();
                        }

                        // Safe front (returns null instead of exception)
                        public T safeFront() {
                            if (isEmpty()) {
                                return null;
                            }
                            return queue.get(0);
                        }

                        // Usage Example:
                        Queue<Integer> queue = new Queue<>();
                        queue.enqueue(10);
                        queue.enqueue(20);
                        queue.enqueue(30);

                        int frontElement = queue.front(); // Returns 10, queue unchanged
                        // Queue: [10, 20, 30] (10 still at front)

                        Integer safeFront = queue.safeFront(); // Safe version
                        """));

        codeExamples.put("Rear Operation", new CodeExample(
                "Rear Operation",
                "View rear element without removing it from queue",
                "O(1)",
                """
                        // View rear element without removing
                        public T rear() {
                            // Check if queue is empty
                            if (isEmpty()) {
                                throw new NoSuchElementException("Queue is empty: No rear element");
                            }

                            // Return rear element without removing
                            T rearElement = queue.get(queue.size() - 1);
                            System.out.println("Rear element: " + rearElement);
                            return rearElement;
                        }

                        // Safe rear (returns null instead of exception)
                        public T safeRear() {
                            if (isEmpty()) {
                                return null;
                            }
                            return queue.get(queue.size() - 1);
                        }

                        // Usage Example:
                        Queue<Integer> queue = new Queue<>();
                        queue.enqueue(10);
                        queue.enqueue(20);
                        queue.enqueue(30);

                        int rearElement = queue.rear(); // Returns 30, queue unchanged
                        // Queue: [10, 20, 30] (30 still at rear)
                        """));

        // =============== QUEUE MANAGEMENT ===============
        codeExamples.put("Clear Queue", new CodeExample(
                "Clear Queue Operation",
                "Remove all elements from the queue",
                "O(1)",
                """
                        // Clear all elements from queue
                        public void clear() {
                            queue.clear();
                            System.out.println("Queue cleared. Size: " + size());
                        }

                        // Clear with confirmation
                        public boolean clearWithConfirmation() {
                            if (isEmpty()) {
                                System.out.println("Queue is already empty");
                                return false;
                            }

                            int oldSize = size();
                            clear();
                            System.out.println("Cleared " + oldSize + " elements from queue");
                            return true;
                        }

                        // Clear and return all elements
                        public List<T> clearAndReturn() {
                            List<T> allElements = new ArrayList<>(queue);
                            clear();
                            return allElements;
                        }

                        // Usage Example:
                        Queue<String> queue = new Queue<>();
                        queue.enqueue("A");
                        queue.enqueue("B");
                        queue.enqueue("C");

                        List<String> backup = queue.clearAndReturn(); // Backup before clear
                        queue.clear(); // Simple clear
                        """));

        codeExamples.put("Queue Size", new CodeExample(
                "Queue Size Management",
                "Methods for checking and managing queue size",
                "O(1)",
                """
                        // Get current size of queue
                        public int size() {
                            return queue.size();
                        }

                        // Check remaining capacity
                        public int getRemainingCapacity() {
                            return maxSize - size();
                        }

                        // Check if can accommodate n more elements
                        public boolean canAccommodate(int count) {
                            return size() + count <= maxSize;
                        }

                        // Get usage percentage
                        public double getUsagePercentage() {
                            return (double) size() / maxSize * 100;
                        }

                        // Usage Example:
                        Queue<Integer> queue = new Queue<>(10);
                        queue.enqueue(1);
                        queue.enqueue(2);
                        queue.enqueue(3);

                        System.out.println("Size: " + queue.size()); // 3
                        System.out.println("Remaining: " + queue.getRemainingCapacity()); // 7
                        System.out.println("Can fit 5 more: " + queue.canAccommodate(5)); // true
                        System.out.println("Usage: " + queue.getUsagePercentage() + "%"); // 30%
                        """));

        // =============== CIRCULAR QUEUE ===============
        codeExamples.put("Circular Queue", new CodeExample(
                "Circular Queue Implementation",
                "Space-efficient queue using circular array",
                "O(1)",
                """
                        // Circular Queue using array
                        public class CircularQueue<T> {
                            private Object[] array;
                            private int front;
                            private int rear;
                            private int size;
                            private int capacity;

                            public CircularQueue(int capacity) {
                                this.capacity = capacity;
                                this.array = new Object[capacity];
                                this.front = 0;
                                this.rear = -1;
                                this.size = 0;
                            }

                            public void enqueue(T element) {
                                if (isFull()) {
                                    throw new IllegalStateException("Queue is full");
                                }
                                // Circular increment
                                rear = (rear + 1) % capacity;
                                array[rear] = element;
                                size++;
                            }

                            @SuppressWarnings("unchecked")
                            public T dequeue() {
                                if (isEmpty()) {
                                    throw new NoSuchElementException("Queue is empty");
                                }
                                T element = (T) array[front];
                                array[front] = null; // Help GC
                                // Circular increment
                                front = (front + 1) % capacity;
                                size--;
                                return element;
                            }

                            @SuppressWarnings("unchecked")
                            public T front() {
                                if (isEmpty()) {
                                    throw new NoSuchElementException("Queue is empty");
                                }
                                return (T) array[front];
                            }

                            public boolean isEmpty() { return size == 0; }
                            public boolean isFull() { return size == capacity; }
                            public int size() { return size; }
                        }

                        // Usage Example:
                        CircularQueue<Integer> cq = new CircularQueue<>(5);
                        cq.enqueue(10);
                        cq.enqueue(20);
                        cq.enqueue(30);
                        System.out.println(cq.dequeue()); // 10
                        cq.enqueue(40);  // Reuses space from dequeued element
                        cq.enqueue(50);
                        cq.enqueue(60);  // Uses the circular wrap-around
                        """));

        // =============== PRIORITY QUEUE ===============
        codeExamples.put("Priority Queue", new CodeExample(
                "Priority Queue Implementation",
                "Queue where elements are dequeued based on priority",
                "O(log n)",
                """
                        // Priority Queue using PriorityQueue class
                        import java.util.PriorityQueue;
                        import java.util.Comparator;

                        // Min-Heap Priority Queue (smallest element first)
                        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
                        minHeap.add(30);
                        minHeap.add(10);
                        minHeap.add(20);
                        System.out.println(minHeap.poll()); // 10 (smallest)
                        System.out.println(minHeap.poll()); // 20
                        System.out.println(minHeap.poll()); // 30

                        // Max-Heap Priority Queue (largest element first)
                        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
                        maxHeap.add(30);
                        maxHeap.add(10);
                        maxHeap.add(20);
                        System.out.println(maxHeap.poll()); // 30 (largest)

                        // Custom Priority Queue with objects
                        class Task {
                            String name;
                            int priority;

                            Task(String name, int priority) {
                                this.name = name;
                                this.priority = priority;
                            }
                        }

                        PriorityQueue<Task> taskQueue = new PriorityQueue<>(
                            (t1, t2) -> Integer.compare(t2.priority, t1.priority) // Higher priority first
                        );

                        taskQueue.add(new Task("Low priority", 1));
                        taskQueue.add(new Task("High priority", 10));
                        taskQueue.add(new Task("Medium priority", 5));

                        while (!taskQueue.isEmpty()) {
                            Task task = taskQueue.poll();
                            System.out.println(task.name + " (priority: " + task.priority + ")");
                        }
                        // Output: High priority (10), Medium priority (5), Low priority (1)
                        """));

        // =============== QUEUE APPLICATIONS ===============
        codeExamples.put("BFS Traversal", new CodeExample(
                "Breadth-First Search using Queue",
                "Graph traversal algorithm using queue",
                "O(V + E)",
                """
                        // BFS using Queue for graph traversal
                        import java.util.*;

                        public class BFSTraversal {

                            public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
                                List<Integer> result = new ArrayList<>();
                                Set<Integer> visited = new HashSet<>();
                                Queue<Integer> queue = new LinkedList<>();

                                queue.offer(start);
                                visited.add(start);

                                while (!queue.isEmpty()) {
                                    int current = queue.poll();
                                    result.add(current);

                                    // Visit all neighbors
                                    for (int neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                                        if (!visited.contains(neighbor)) {
                                            visited.add(neighbor);
                                            queue.offer(neighbor);
                                        }
                                    }
                                }

                                return result;
                            }

                            // Level-order BFS (returns nodes by level)
                            public static List<List<Integer>> bfsLevels(Map<Integer, List<Integer>> graph, int start) {
                                List<List<Integer>> levels = new ArrayList<>();
                                Set<Integer> visited = new HashSet<>();
                                Queue<Integer> queue = new LinkedList<>();

                                queue.offer(start);
                                visited.add(start);

                                while (!queue.isEmpty()) {
                                    int levelSize = queue.size();
                                    List<Integer> currentLevel = new ArrayList<>();

                                    for (int i = 0; i < levelSize; i++) {
                                        int current = queue.poll();
                                        currentLevel.add(current);

                                        for (int neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                                            if (!visited.contains(neighbor)) {
                                                visited.add(neighbor);
                                                queue.offer(neighbor);
                                            }
                                        }
                                    }

                                    levels.add(currentLevel);
                                }

                                return levels;
                            }
                        }

                        // Usage Example:
                        Map<Integer, List<Integer>> graph = new HashMap<>();
                        graph.put(1, Arrays.asList(2, 3));
                        graph.put(2, Arrays.asList(4, 5));
                        graph.put(3, Arrays.asList(6));

                        System.out.println(bfs(graph, 1)); // [1, 2, 3, 4, 5, 6]
                        System.out.println(bfsLevels(graph, 1)); // [[1], [2, 3], [4, 5, 6]]
                        """));

        codeExamples.put("Task Scheduler", new CodeExample(
                "Task Scheduler using Queue",
                "Simple round-robin task scheduler",
                "O(n)",
                """
                        // Task Scheduler using Queue (Round-Robin)
                        import java.util.*;

                        public class TaskScheduler {
                            private Queue<Task> taskQueue;
                            private int timeQuantum;

                            public TaskScheduler(int timeQuantum) {
                                this.taskQueue = new LinkedList<>();
                                this.timeQuantum = timeQuantum;
                            }

                            public void addTask(String name, int burstTime) {
                                taskQueue.offer(new Task(name, burstTime));
                            }

                            public void execute() {
                                int currentTime = 0;

                                while (!taskQueue.isEmpty()) {
                                    Task task = taskQueue.poll();

                                    int executionTime = Math.min(timeQuantum, task.remainingTime);
                                    task.remainingTime -= executionTime;
                                    currentTime += executionTime;

                                    System.out.println("Time " + currentTime + ": Executed " +
                                                     task.name + " for " + executionTime + "ms");

                                    if (task.remainingTime > 0) {
                                        // Re-add to queue if not complete
                                        taskQueue.offer(task);
                                    } else {
                                        System.out.println("  → " + task.name + " completed!");
                                    }
                                }
                            }

                            static class Task {
                                String name;
                                int remainingTime;

                                Task(String name, int remainingTime) {
                                    this.name = name;
                                    this.remainingTime = remainingTime;
                                }
                            }
                        }

                        // Usage Example:
                        TaskScheduler scheduler = new TaskScheduler(3); // 3ms time quantum
                        scheduler.addTask("Process A", 10);
                        scheduler.addTask("Process B", 5);
                        scheduler.addTask("Process C", 8);
                        scheduler.execute();

                        /* Output:
                        Time 3: Executed Process A for 3ms
                        Time 6: Executed Process B for 3ms
                        Time 9: Executed Process C for 3ms
                        Time 12: Executed Process A for 3ms
                        Time 14: Executed Process B for 2ms
                          → Process B completed!
                        ... */
                        """));

        codeExamples.put("Level Order Tree", new CodeExample(
                "Level Order Tree Traversal",
                "Binary tree level-by-level traversal using queue",
                "O(n)",
                """
                        // Level Order Traversal of Binary Tree
                        import java.util.*;

                        class TreeNode {
                            int val;
                            TreeNode left, right;

                            TreeNode(int val) {
                                this.val = val;
                            }
                        }

                        public class LevelOrderTraversal {

                            // Basic level order traversal
                            public static List<Integer> levelOrder(TreeNode root) {
                                List<Integer> result = new ArrayList<>();
                                if (root == null) return result;

                                Queue<TreeNode> queue = new LinkedList<>();
                                queue.offer(root);

                                while (!queue.isEmpty()) {
                                    TreeNode node = queue.poll();
                                    result.add(node.val);

                                    if (node.left != null) queue.offer(node.left);
                                    if (node.right != null) queue.offer(node.right);
                                }

                                return result;
                            }

                            // Level order with level separation
                            public static List<List<Integer>> levelOrderByLevel(TreeNode root) {
                                List<List<Integer>> result = new ArrayList<>();
                                if (root == null) return result;

                                Queue<TreeNode> queue = new LinkedList<>();
                                queue.offer(root);

                                while (!queue.isEmpty()) {
                                    int levelSize = queue.size();
                                    List<Integer> currentLevel = new ArrayList<>();

                                    for (int i = 0; i < levelSize; i++) {
                                        TreeNode node = queue.poll();
                                        currentLevel.add(node.val);

                                        if (node.left != null) queue.offer(node.left);
                                        if (node.right != null) queue.offer(node.right);
                                    }

                                    result.add(currentLevel);
                                }

                                return result;
                            }

                            // Zigzag level order (alternating direction)
                            public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
                                List<List<Integer>> result = new ArrayList<>();
                                if (root == null) return result;

                                Queue<TreeNode> queue = new LinkedList<>();
                                queue.offer(root);
                                boolean leftToRight = true;

                                while (!queue.isEmpty()) {
                                    int levelSize = queue.size();
                                    LinkedList<Integer> currentLevel = new LinkedList<>();

                                    for (int i = 0; i < levelSize; i++) {
                                        TreeNode node = queue.poll();

                                        if (leftToRight) {
                                            currentLevel.addLast(node.val);
                                        } else {
                                            currentLevel.addFirst(node.val);
                                        }

                                        if (node.left != null) queue.offer(node.left);
                                        if (node.right != null) queue.offer(node.right);
                                    }

                                    result.add(currentLevel);
                                    leftToRight = !leftToRight;
                                }

                                return result;
                            }
                        }

                        // Usage Example:
                        //        1
                        //       / \\
                        //      2   3
                        //     / \\   \\
                        //    4   5   6

                        TreeNode root = new TreeNode(1);
                        root.left = new TreeNode(2);
                        root.right = new TreeNode(3);
                        root.left.left = new TreeNode(4);
                        root.left.right = new TreeNode(5);
                        root.right.right = new TreeNode(6);

                        System.out.println(levelOrder(root)); // [1, 2, 3, 4, 5, 6]
                        System.out.println(levelOrderByLevel(root)); // [[1], [2, 3], [4, 5, 6]]
                        System.out.println(zigzagLevelOrder(root)); // [[1], [3, 2], [4, 5, 6]]
                        """));

        codeExamples.put("Deque Operations", new CodeExample(
                "Double-Ended Queue (Deque)",
                "Queue with operations at both ends",
                "O(1)",
                """
                        // Deque (Double-Ended Queue) Operations
                        import java.util.*;

                        public class DequeExample {

                            public static void main(String[] args) {
                                Deque<Integer> deque = new ArrayDeque<>();

                                // Add to front
                                deque.addFirst(10);
                                deque.offerFirst(5);  // Safe version

                                // Add to rear
                                deque.addLast(20);
                                deque.offerLast(25);  // Safe version

                                // Current deque: [5, 10, 20, 25]

                                // Peek operations
                                System.out.println("First: " + deque.peekFirst()); // 5
                                System.out.println("Last: " + deque.peekLast());   // 25

                                // Remove from front
                                int front = deque.removeFirst();  // 5
                                int safeFront = deque.pollFirst(); // 10 (or null if empty)

                                // Remove from rear
                                int rear = deque.removeLast();    // 25
                                int safeRear = deque.pollLast();  // 20 (or null if empty)

                                // Use as Stack (LIFO)
                                Deque<String> stack = new ArrayDeque<>();
                                stack.push("A");
                                stack.push("B");
                                stack.push("C");
                                System.out.println(stack.pop()); // C

                                // Use as Queue (FIFO)
                                Deque<String> queue = new ArrayDeque<>();
                                queue.offer("A");
                                queue.offer("B");
                                queue.offer("C");
                                System.out.println(queue.poll()); // A

                                // Sliding Window Maximum using Deque
                                int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
                                int k = 3;
                                int[] result = maxSlidingWindow(nums, k);
                                System.out.println(Arrays.toString(result)); // [3, 3, 5, 5, 6, 7]
                            }

                            // Classic problem: Sliding Window Maximum
                            public static int[] maxSlidingWindow(int[] nums, int k) {
                                if (nums == null || nums.length == 0) return new int[0];

                                int[] result = new int[nums.length - k + 1];
                                Deque<Integer> deque = new ArrayDeque<>(); // Stores indices

                                for (int i = 0; i < nums.length; i++) {
                                    // Remove elements outside window
                                    while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                                        deque.pollFirst();
                                    }

                                    // Remove smaller elements
                                    while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                                        deque.pollLast();
                                    }

                                    deque.offerLast(i);

                                    // Add to result when window is complete
                                    if (i >= k - 1) {
                                        result[i - k + 1] = nums[deque.peekFirst()];
                                    }
                                }

                                return result;
                            }
                        }
                        """));
    }

    public CodeExample getCodeExample(String name) {
        return codeExamples.get(name);
    }

    public String getWelcomeMessage() {
        return """
                ╔══════════════════════════════════════════════════════════════════╗
                ║                                                                    ║
                ║    🎉 Welcome to the Queue Code Repository!                        ║
                ║                                                                    ║
                ║    This comprehensive collection includes:                         ║
                ║                                                                    ║
                ║    📚 Basic Operations                                             ║
                ║       • Create Queue, Enqueue, Dequeue, Front, Rear                ║
                ║                                                                    ║
                ║    🔧 Queue Management                                             ║
                ║       • Clear, Size Management, Capacity Handling                  ║
                ║                                                                    ║
                ║    🚀 Advanced Queue Types                                         ║
                ║       • Circular Queue, Priority Queue, Deque                      ║
                ║                                                                    ║
                ║    ⚡ Queue Applications                                           ║
                ║       • BFS Traversal, Task Scheduler, Level Order Traversal       ║
                ║                                                                    ║
                ║    👈 Select a category from the left panel to explore             ║
                ║       detailed code examples with explanations.                    ║
                ║                                                                    ║
                ║    💡 Tip: Use the search bar to find specific operations!         ║
                ║                                                                    ║
                ╚══════════════════════════════════════════════════════════════════╝
                """;
    }

    // Inner class for code examples
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

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getComplexity() {
            return complexity;
        }

        public String getCode() {
            return code;
        }
    }
}
