package com.simulator;

import java.util.HashMap;
import java.util.Map;

public class StackCodeRepository {
    private final Map<String, CodeExample> codeExamples;

    public StackCodeRepository() {
        codeExamples = new HashMap<>();
        initializeCodeExamples();
    }

    private void initializeCodeExamples() {
        // =============== BASIC STACK OPERATIONS ===============
        codeExamples.put("Create Stack", new CodeExample(
                "Create Stack",
                "Basic Stack class structure with array/list implementation",
                "O(1)",
                """
                // Stack implementation using ArrayList
                import java.util.ArrayList;
                import java.util.EmptyStackException;
                
                public class Stack<T> {
                    private ArrayList<T> stack;
                    private int maxSize;
                    
                    // Constructor
                    public Stack() {
                        this(100); // Default max size
                    }
                    
                    public Stack(int maxSize) {
                        this.maxSize = maxSize;
                        this.stack = new ArrayList<>();
                    }
                    
                    // Check if stack is empty
                    public boolean isEmpty() {
                        return stack.isEmpty();
                    }
                    
                    // Check if stack is full
                    public boolean isFull() {
                        return stack.size() >= maxSize;
                    }
                    
                    // Get current size
                    public int size() {
                        return stack.size();
                    }
                    
                    // Get capacity
                    public int getCapacity() {
                        return maxSize;
                    }
                    
                    // Display stack contents
                    public void display() {
                        if (isEmpty()) {
                            System.out.println("Stack is empty");
                            return;
                        }
                        System.out.println("Stack contents (top to bottom): " + stack);
                    }
                }
                
                // Usage Example:
                Stack<Integer> stack = new Stack<>(10);
                System.out.println("Empty: " + stack.isEmpty()); // true
                System.out.println("Capacity: " + stack.getCapacity()); // 10
                """
        ));

        codeExamples.put("Push Operation", new CodeExample(
                "Push Operation",
                "Add element to top of stack with overflow check",
                "O(1)",
                """
                // Push element to stack
                public void push(T element) {
                    // Check for stack overflow
                    if (isFull()) {
                        throw new IllegalStateException("Stack Overflow: Cannot push to full stack");
                    }
                    
                    // Add element to top of stack
                    stack.add(element);
                    System.out.println("Pushed: " + element + " | Size: " + size());
                }
                
                // Bulk push operation
                public void pushAll(T[] elements) {
                    for (T element : elements) {
                        if (!isFull()) {
                            push(element);
                        } else {
                            System.out.println("Stack full! Cannot push: " + element);
                            break;
                        }
                    }
                }
                
                // Safe push (returns boolean instead of exception)
                public boolean safePush(T element) {
                    if (isFull()) {
                        return false;
                    }
                    stack.add(element);
                    return true;
                }
                
                // Usage Example:
                Stack<Integer> stack = new Stack<>(5);
                stack.push(10);
                stack.push(20);
                stack.push(30);
                // Stack: [10, 20, 30] (30 is top)
                
                Integer[] values = {40, 50, 60};
                stack.pushAll(values);
                
                boolean success = stack.safePush(70); // Returns false if full
                """
        ));

        codeExamples.put("Pop Operation", new CodeExample(
                "Pop Operation",
                "Remove and return top element from stack",
                "O(1)",
                """
                // Pop element from stack
                public T pop() {
                    // Check for stack underflow
                    if (isEmpty()) {
                        throw new EmptyStackException();
                    }
                    
                    // Remove and return top element
                    T element = stack.remove(stack.size() - 1);
                    System.out.println("Popped: " + element + " | Size: " + size());
                    return element;
                }
                
                // Safe pop (returns null instead of exception)
                public T safePop() {
                    if (isEmpty()) {
                        return null;
                    }
                    return stack.remove(stack.size() - 1);
                }
                
                // Pop multiple elements
                public T[] popMultiple(int count) {
                    if (count > size()) {
                        throw new IllegalArgumentException("Cannot pop more elements than stack size");
                    }
                    
                    @SuppressWarnings("unchecked")
                    T[] result = (T[]) new Object[count];
                    
                    for (int i = 0; i < count; i++) {
                        result[i] = pop();
                    }
                    return result;
                }
                
                // Usage Example:
                Stack<Integer> stack = new Stack<>();
                stack.push(10);
                stack.push(20);
                stack.push(30);
                
                int poppedElement = stack.pop(); // Returns 30
                // Stack: [10, 20] (20 is now top)
                
                Integer safePopped = stack.safePop(); // Returns 20 or null
                Integer[] multiple = stack.popMultiple(2); // Pop 2 elements
                """
        ));

        codeExamples.put("Peek Operation", new CodeExample(
                "Peek Operation",
                "View top element without removing it from stack",
                "O(1)",
                """
                // Peek at top element without removing
                public T peek() {
                    // Check if stack is empty
                    if (isEmpty()) {
                        throw new EmptyStackException();
                    }
                    
                    // Return top element without removing
                    T topElement = stack.get(stack.size() - 1);
                    System.out.println("Top element: " + topElement);
                    return topElement;
                }
                
                // Alternative: top() method (same functionality)
                public T top() {
                    return peek();
                }
                
                // Safe peek (returns null instead of exception)
                public T safePeek() {
                    if (isEmpty()) {
                        return null;
                    }
                    return stack.get(stack.size() - 1);
                }
                
                // Peek at nth element from top (0 = top, 1 = second from top, etc.)
                public T peekAt(int index) {
                    if (index < 0 || index >= size()) {
                        throw new IndexOutOfBoundsException("Invalid index: " + index);
                    }
                    return stack.get(size() - 1 - index);
                }
                
                // Usage Example:
                Stack<Integer> stack = new Stack<>();
                stack.push(10);
                stack.push(20);
                stack.push(30);
                
                int topElement = stack.peek(); // Returns 30, stack unchanged
                // Stack: [10, 20, 30] (30 still on top)
                
                Integer safeTop = stack.safePeek(); // Safe version
                int secondFromTop = stack.peekAt(1); // Returns 20
                """
        ));

        // =============== STACK MANAGEMENT OPERATIONS ===============
        codeExamples.put("Stack Overflow Check", new CodeExample(
                "Stack Overflow Handling",
                "Comprehensive overflow detection and handling mechanisms",
                "O(1)",
                """
                // Enhanced overflow checking with detailed reporting
                public class StackOverflowManager {
                    
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
                    
                    // Push with overflow warning
                    public boolean pushWithWarning(T element, double warningThreshold) {
                        if (isFull()) {
                            System.err.println("OVERFLOW: Stack is full!");
                            return false;
                        }
                        
                        push(element);
                        
                        if (getUsagePercentage() >= warningThreshold) {
                            System.out.println("WARNING: Stack is " + 
                                String.format("%.1f", getUsagePercentage()) + "% full");
                        }
                        
                        return true;
                    }
                    
                    // Auto-resize stack when near capacity
                    public void enableAutoResize() {
                        if (getUsagePercentage() >= 80) {
                            int newCapacity = (int) (maxSize * 1.5);
                            System.out.println("Auto-resizing stack from " + maxSize + 
                                             " to " + newCapacity);
                            maxSize = newCapacity;
                        }
                    }
                }
                
                // Usage Example:
                Stack<String> stack = new Stack<>(5);
                stack.pushWithWarning("A", 70.0); // Pushes and checks threshold
                stack.pushWithWarning("B", 70.0);
                stack.pushWithWarning("C", 70.0);
                stack.pushWithWarning("D", 70.0); // Warning at 80%
                
                System.out.println("Remaining capacity: " + stack.getRemainingCapacity());
                System.out.println("Can accommodate 2 more: " + stack.canAccommodate(2));
                """
        ));

        codeExamples.put("Stack Underflow Check", new CodeExample(
                "Stack Underflow Handling",
                "Comprehensive underflow detection and safe operations",
                "O(1)",
                """
                // Enhanced underflow checking and safe operations
                public class StackUnderflowManager {
                    
                    // Safe operations that handle underflow gracefully
                    public Optional<T> safePeek() {
                        return isEmpty() ? Optional.empty() : Optional.of(peek());
                    }
                    
                    public Optional<T> safePop() {
                        return isEmpty() ? Optional.empty() : Optional.of(pop());
                    }
                    
                    // Pop with default value
                    public T popOrDefault(T defaultValue) {
                        return isEmpty() ? defaultValue : pop();
                    }
                    
                    // Conditional operations
                    public boolean popIf(Predicate<T> condition) {
                        if (isEmpty()) return false;
                        
                        T top = peek();
                        if (condition.test(top)) {
                            pop();
                            return true;
                        }
                        return false;
                    }
                    
                    // Bulk safe pop
                    public List<T> safePop(int count) {
                        List<T> result = new ArrayList<>();
                        int actualCount = Math.min(count, size());
                        
                        for (int i = 0; i < actualCount; i++) {
                            result.add(pop());
                        }
                        
                        if (actualCount < count) {
                            System.out.println("Warning: Could only pop " + actualCount + 
                                             " elements (requested: " + count + ")");
                        }
                        
                        return result;
                    }
                    
                    // Stack state validation
                    public void validateState() throws IllegalStateException {
                        if (size() < 0) {
                            throw new IllegalStateException("Invalid stack state: negative size");
                        }
                        if (stack == null) {
                            throw new IllegalStateException("Stack not initialized");
                        }
                    }
                }
                
                // Usage Example:
                Stack<Integer> stack = new Stack<>();
                
                // Safe operations
                Optional<Integer> safeTop = stack.safePeek(); // Returns empty Optional
                Optional<Integer> safePop = stack.safePop();  // Returns empty Optional
                
                stack.push(10);
                stack.push(20);
                
                Integer value = stack.popOrDefault(-1); // Returns 20
                List<Integer> values = stack.safePop(5); // Pops available elements
                
                // Conditional pop
                stack.push(15);
                boolean popped = stack.popIf(x -> x > 10); // Pops if condition true
                """
        ));

        codeExamples.put("Clear Stack", new CodeExample(
                "Clear Stack Operations",
                "Various methods to clear and reset stack",
                "O(1)",
                """
                // Clear all elements from stack
                public void clear() {
                    stack.clear();
                    System.out.println("Stack cleared. Size: " + size());
                }
                
                // Clear with confirmation
                public boolean clearWithConfirmation() {
                    if (isEmpty()) {
                        System.out.println("Stack is already empty");
                        return false;
                    }
                    
                    int oldSize = size();
                    clear();
                    System.out.println("Cleared " + oldSize + " elements from stack");
                    return true;
                }
                
                // Clear and return all elements
                public List<T> clearAndReturn() {
                    List<T> allElements = new ArrayList<>(stack);
                    clear();
                    return allElements;
                }
                
                // Selective clear (remove elements matching condition)
                public int clearIf(Predicate<T> condition) {
                    List<T> toRemove = stack.stream()
                                           .filter(condition)
                                           .collect(Collectors.toList());
                    
                    stack.removeAll(toRemove);
                    System.out.println("Removed " + toRemove.size() + " elements");
                    return toRemove.size();
                }
                
                // Reset stack to initial state
                public void reset() {
                    clear();
                    maxSize = 100; // Reset to default capacity
                    System.out.println("Stack reset to initial state");
                }
                
                // Clear top n elements
                public List<T> clearTop(int n) {
                    if (n >= size()) {
                        return clearAndReturn();
                    }
                    
                    List<T> removed = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        removed.add(pop());
                    }
                    return removed;
                }
                
                // Usage Example:
                Stack<String> stack = new Stack<>();
                stack.push("A");
                stack.push("B");
                stack.push("C");
                
                // Different clear operations
                List<String> backup = stack.clearAndReturn(); // Backup before clear
                
                // Restore and selective clear
                stack.pushAll(backup.toArray(new String[0]));
                int removed = stack.clearIf(s -> s.equals("B")); // Remove specific elements
                
                List<String> topElements = stack.clearTop(2); // Clear top 2 elements
                stack.reset(); // Complete reset
                """
        ));

        // =============== ADVANCED STACK APPLICATIONS ===============
        codeExamples.put("Balanced Parentheses", new CodeExample(
                "Balanced Parentheses Checker",
                "Check if parentheses, brackets, and braces are properly balanced",
                "O(n)",
                """
                // Comprehensive balanced parentheses checker
                public class BalancedParenthesesChecker {
                    
                    public static boolean isBalanced(String expression) {
                        Stack<Character> stack = new Stack<>();
                        
                        for (char ch : expression.toCharArray()) {
                            // Push opening brackets
                            if (ch == '(' || ch == '[' || ch == '{') {
                                stack.push(ch);
                            }
                            // Check closing brackets
                            else if (ch == ')' || ch == ']' || ch == '}') {
                                if (stack.isEmpty()) {
                                    return false; // No matching opening bracket
                                }
                                
                                char top = stack.pop();
                                if (!isMatchingPair(top, ch)) {
                                    return false; // Mismatched brackets
                                }
                            }
                        }
                        
                        return stack.isEmpty(); // All brackets should be matched
                    }
                    
                    private static boolean isMatchingPair(char open, char close) {
                        return (open == '(' && close == ')') ||
                               (open == '[' && close == ']') ||
                               (open == '{' && close == '}');
                    }
                    
                    // Enhanced version with error reporting
                    public static class ValidationResult {
                        public final boolean isValid;
                        public final String errorMessage;
                        public final int errorPosition;
                        
                        public ValidationResult(boolean isValid, String errorMessage, int errorPosition) {
                            this.isValid = isValid;
                            this.errorMessage = errorMessage;
                            this.errorPosition = errorPosition;
                        }
                    }
                    
                    public static ValidationResult validateWithDetails(String expression) {
                        Stack<Character> stack = new Stack<>();
                        Stack<Integer> positions = new Stack<>();
                        
                        for (int i = 0; i < expression.length(); i++) {
                            char ch = expression.charAt(i);
                            
                            if (ch == '(' || ch == '[' || ch == '{') {
                                stack.push(ch);
                                positions.push(i);
                            }
                            else if (ch == ')' || ch == ']' || ch == '}') {
                                if (stack.isEmpty()) {
                                    return new ValidationResult(false, 
                                        "Unmatched closing bracket '" + ch + "'", i);
                                }
                                
                                char top = stack.pop();
                                positions.pop();
                                
                                if (!isMatchingPair(top, ch)) {
                                    return new ValidationResult(false, 
                                        "Mismatched brackets: '" + top + "' and '" + ch + "'", i);
                                }
                            }
                        }
                        
                        if (!stack.isEmpty()) {
                            return new ValidationResult(false, 
                                "Unmatched opening bracket '" + stack.peek() + "'", positions.peek());
                        }
                        
                        return new ValidationResult(true, "All brackets are balanced", -1);
                    }
                }
                
                // Usage Examples:
                System.out.println(isBalanced("({[]})")); // true
                System.out.println(isBalanced("({[})")); // false
                System.out.println(isBalanced("((()))")); // true
                System.out.println(isBalanced("((())")); // false
                
                ValidationResult result = validateWithDetails("({[})");
                System.out.println("Valid: " + result.isValid);
                System.out.println("Error: " + result.errorMessage);
                System.out.println("Position: " + result.errorPosition);
                """
        ));

        codeExamples.put("String Reversal", new CodeExample(
                "String Reversal Using Stack",
                "Reverse strings and validate palindromes using stack",
                "O(n)",
                """
                // Comprehensive string operations using stack
                public class StringReverser {
                    
                    // Basic string reversal
                    public static String reverse(String str) {
                        Stack<Character> stack = new Stack<>();
                        
                        // Push all characters onto stack
                        for (char ch : str.toCharArray()) {
                            stack.push(ch);
                        }
                        
                        // Pop characters to build reversed string
                        StringBuilder reversed = new StringBuilder();
                        while (!stack.isEmpty()) {
                            reversed.append(stack.pop());
                        }
                        
                        return reversed.toString();
                    }
                    
                    // Reverse words in a sentence (keep word order, reverse each word)
                    public static String reverseWords(String sentence) {
                        String[] words = sentence.split(" ");
                        StringBuilder result = new StringBuilder();
                        
                        for (int i = 0; i < words.length; i++) {
                            if (i > 0) result.append(" ");
                            result.append(reverse(words[i]));
                        }
                        
                        return result.toString();
                    }
                    
                    // Reverse sentence order (reverse word order)
                    public static String reverseSentence(String sentence) {
                        Stack<String> wordStack = new Stack<>();
                        String[] words = sentence.split(" ");
                        
                        for (String word : words) {
                            wordStack.push(word);
                        }
                        
                        StringBuilder result = new StringBuilder();
                        boolean first = true;
                        while (!wordStack.isEmpty()) {
                            if (!first) result.append(" ");
                            result.append(wordStack.pop());
                            first = false;
                        }
                        
                        return result.toString();
                    }
                    
                    // Check if string is palindrome using stack
                    public static boolean isPalindrome(String str) {
                        // Remove non-alphanumeric and convert to lowercase
                        String cleaned = str.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                        
                        Stack<Character> stack = new Stack<>();
                        int length = cleaned.length();
                        
                        // Push first half onto stack
                        for (int i = 0; i < length / 2; i++) {
                            stack.push(cleaned.charAt(i));
                        }
                        
                        // Compare second half with popped characters
                        int start = (length % 2 == 0) ? length / 2 : (length / 2) + 1;
                        for (int i = start; i < length; i++) {
                            if (stack.isEmpty() || stack.pop() != cleaned.charAt(i)) {
                                return false;
                            }
                        }
                        
                        return true;
                    }
                    
                    // Remove adjacent duplicates
                    public static String removeAdjacentDuplicates(String str) {
                        Stack<Character> stack = new Stack<>();
                        
                        for (char ch : str.toCharArray()) {
                            if (!stack.isEmpty() && stack.peek() == ch) {
                                stack.pop(); // Remove duplicate
                            } else {
                                stack.push(ch);
                            }
                        }
                        
                        StringBuilder result = new StringBuilder();
                        while (!stack.isEmpty()) {
                            result.insert(0, stack.pop());
                        }
                        
                        return result.toString();
                    }
                }
                
                // Usage Examples:
                System.out.println(reverse("HELLO")); // "OLLEH"
                System.out.println(reverseWords("Hello World")); // "olleH dlroW"
                System.out.println(reverseSentence("Hello World Java")); // "Java World Hello"
                System.out.println(isPalindrome("A man a plan a canal Panama")); // true
                System.out.println(removeAdjacentDuplicates("abbaca")); // "ca"
                """
        ));

        codeExamples.put("Expression Evaluation", new CodeExample(
                "Mathematical Expression Evaluation",
                "Evaluate infix, postfix, and prefix expressions using stack",
                "O(n)",
                """
                // Comprehensive expression evaluator
                public class ExpressionEvaluator {
                    
                    // Evaluate postfix expression
                    public static double evaluatePostfix(String expression) {
                        Stack<Double> stack = new Stack<>();
                        String[] tokens = expression.trim().split("\\s+");
                        
                        for (String token : tokens) {
                            if (isOperator(token)) {
                                if (stack.size() < 2) {
                                    throw new IllegalArgumentException("Invalid postfix expression");
                                }
                                
                                double operand2 = stack.pop();
                                double operand1 = stack.pop();
                                double result = performOperation(operand1, operand2, token);
                                stack.push(result);
                            } else {
                                try {
                                    double operand = Double.parseDouble(token);
                                    stack.push(operand);
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException("Invalid number: " + token);
                                }
                            }
                        }
                        
                        if (stack.size() != 1) {
                            throw new IllegalArgumentException("Invalid postfix expression");
                        }
                        
                        return stack.pop();
                    }
                    
                    // Convert infix to postfix
                    public static String infixToPostfix(String infix) {
                        StringBuilder postfix = new StringBuilder();
                        Stack<Character> operatorStack = new Stack<>();
                        
                        for (char ch : infix.toCharArray()) {
                            if (Character.isDigit(ch) || Character.isLetter(ch)) {
                                postfix.append(ch).append(' ');
                            }
                            else if (ch == '(') {
                                operatorStack.push(ch);
                            }
                            else if (ch == ')') {
                                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                                    postfix.append(operatorStack.pop()).append(' ');
                                }
                                operatorStack.pop(); // Remove '('
                            }
                            else if (isOperator(String.valueOf(ch))) {
                                while (!operatorStack.isEmpty() && 
                                       getPrecedence(operatorStack.peek()) >= getPrecedence(ch)) {
                                    postfix.append(operatorStack.pop()).append(' ');
                                }
                                operatorStack.push(ch);
                            }
                        }
                        
                        while (!operatorStack.isEmpty()) {
                            postfix.append(operatorStack.pop()).append(' ');
                        }
                        
                        return postfix.toString().trim();
                    }
                    
                    // Evaluate infix expression directly
                    public static double evaluateInfix(String infix) {
                        String postfix = infixToPostfix(infix);
                        return evaluatePostfix(postfix);
                    }
                    
                    private static boolean isOperator(String token) {
                        return token.equals("+") || token.equals("-") || 
                               token.equals("*") || token.equals("/") || 
                               token.equals("^");
                    }
                    
                    private static int getPrecedence(char operator) {
                        switch (operator) {
                            case '+':
                            case '-':
                                return 1;
                            case '*':
                            case '/':
                                return 2;
                            case '^':
                                return 3;
                            default:
                                return 0;
                        }
                    }
                    
                    private static double performOperation(double a, double b, String operator) {
                        switch (operator) {
                            case "+": return a + b;
                            case "-": return a - b;
                            case "*": return a * b;
                            case "/": 
                                if (b == 0) throw new ArithmeticException("Division by zero");
                                return a / b;
                            case "^": return Math.pow(a, b);
                            default: throw new IllegalArgumentException("Unknown operator: " + operator);
                        }
                    }
                }
                
                // Usage Examples:
                // Postfix evaluation
                System.out.println(evaluatePostfix("2 3 + 4 *")); // (2+3)*4 = 20
                System.out.println(evaluatePostfix("15 7 1 1 + - / 3 * 2 1 1 + + -")); // Complex expression
                
                // Infix to postfix conversion
                System.out.println(infixToPostfix("2+3*4")); // "2 3 4 * +"
                System.out.println(infixToPostfix("(2+3)*4")); // "2 3 + 4 *"
                
                // Direct infix evaluation
                System.out.println(evaluateInfix("(2+3)*4-1")); // 19.0
                """
        ));

        codeExamples.put("Function Call Stack", new CodeExample(
                "Function Call Stack Simulation",
                "Simulate function call stack for recursion and debugging",
                "O(n)",
                """
                // Function call stack simulator
                public class CallStackSimulator {
                    
                    public static class StackFrame {
                        public final String functionName;
                        public final Map<String, Object> localVariables;
                        public final List<Object> parameters;
                        public final int lineNumber;
                        
                        public StackFrame(String functionName, int lineNumber) {
                            this.functionName = functionName;
                            this.lineNumber = lineNumber;
                            this.localVariables = new HashMap<>();
                            this.parameters = new ArrayList<>();
                        }
                        
                        public void addVariable(String name, Object value) {
                            localVariables.put(name, value);
                        }
                        
                        public void addParameter(Object value) {
                            parameters.add(value);
                        }
                        
                        @Override
                        public String toString() {
                            return String.format("%s(line %d) - Vars: %s, Params: %s", 
                                               functionName, lineNumber, localVariables, parameters);
                        }
                    }
                    
                    private static Stack<StackFrame> callStack = new Stack<>();
                    
                    // Function entry
                    public static void enterFunction(String functionName, int lineNumber) {
                        StackFrame frame = new StackFrame(functionName, lineNumber);
                        callStack.push(frame);
                        System.out.println("CALL: " + functionName + " at line " + lineNumber);
                        printCallStack();
                    }
                    
                    // Function exit
                    public static void exitFunction(Object returnValue) {
                        if (!callStack.isEmpty()) {
                            StackFrame frame = callStack.pop();
                            System.out.println("RETURN: " + frame.functionName + 
                                             " returns " + returnValue);
                        }
                    }
                    
                    // Add variable to current frame
                    public static void addVariable(String name, Object value) {
                        if (!callStack.isEmpty()) {
                            callStack.peek().addVariable(name, value);
                        }
                    }
                    
                    // Print current call stack
                    public static void printCallStack() {
                        System.out.println("=== Call Stack ===");
                        Stack<StackFrame> temp = new Stack<>();
                        
                        // Print from top to bottom
                        while (!callStack.isEmpty()) {
                            StackFrame frame = callStack.pop();
                            System.out.println("  " + frame);
                            temp.push(frame);
                        }
                        
                        // Restore original stack
                        while (!temp.isEmpty()) {
                            callStack.push(temp.pop());
                        }
                        System.out.println("==================");
                    }
                    
                    // Recursive factorial with call stack tracking
                    public static long factorial(int n) {
                        enterFunction("factorial", 1);
                        addVariable("n", n);
                        
                        if (n <= 1) {
                            exitFunction(1L);
                            return 1;
                        }
                        
                        long result = n * factorial(n - 1);
                        addVariable("result", result);
                        exitFunction(result);
                        return result;
                    }
                    
                    // Fibonacci with call stack tracking
                    public static long fibonacci(int n) {
                        enterFunction("fibonacci", 1);
                        addVariable("n", n);
                        
                        if (n <= 1) {
                            exitFunction((long)n);
                            return n;
                        }
                        
                        long result = fibonacci(n - 1) + fibonacci(n - 2);
                        addVariable("result", result);
                        exitFunction(result);
                        return result;
                    }
                }
                
                // Usage Examples:
                System.out.println("=== Factorial Call Stack ===");
                long fact = CallStackSimulator.factorial(4);
                System.out.println("Result: " + fact);
                
                System.out.println("\\n=== Fibonacci Call Stack ===");
                long fib = CallStackSimulator.fibonacci(3);
                System.out.println("Result: " + fib);
                """
        ));

        // =============== DIFFERENT STACK IMPLEMENTATIONS ===============
        codeExamples.put("Array-Based Stack", new CodeExample(
                "Array-Based Stack Implementation",
                "Fixed-size stack implementation using arrays",
                "O(1) for all operations",
                """
                // Array-based stack implementation
                public class ArrayStack<T> {
                    private Object[] stack;
                    private int top;
                    private final int maxSize;
                    
                    public ArrayStack(int capacity) {
                        this.maxSize = capacity;
                        this.stack = new Object[capacity];
                        this.top = -1;
                    }
                    
                    public boolean isEmpty() {
                        return top == -1;
                    }
                    
                    public boolean isFull() {
                        return top == maxSize - 1;
                    }
                    
                    public int size() {
                        return top + 1;
                    }
                    
                    public void push(T element) {
                        if (isFull()) {
                            throw new RuntimeException("Stack Overflow");
                        }
                        stack[++top] = element;
                    }
                    
                    @SuppressWarnings("unchecked")
                    public T pop() {
                        if (isEmpty()) {
                            throw new RuntimeException("Stack Underflow");
                        }
                        T element = (T) stack[top];
                        stack[top--] = null; // Help GC
                        return element;
                    }
                    
                    @SuppressWarnings("unchecked")
                    public T peek() {
                        if (isEmpty()) {
                            throw new RuntimeException("Stack is empty");
                        }
                        return (T) stack[top];
                    }
                    
                    // Display stack contents
                    public void display() {
                        if (isEmpty()) {
                            System.out.println("Stack is empty");
                            return;
                        }
                        
                        System.out.print("Stack (top to bottom): ");
                        for (int i = top; i >= 0; i--) {
                            System.out.print(stack[i] + " ");
                        }
                        System.out.println();
                    }
                    
                    // Get all elements as array
                    @SuppressWarnings("unchecked")
                    public T[] toArray() {
                        Object[] result = new Object[size()];
                        for (int i = 0; i < size(); i++) {
                            result[i] = stack[top - i];
                        }
                        return (T[]) result;
                    }
                }
                
                // Usage Example:
                ArrayStack<Integer> stack = new ArrayStack<>(5);
                
                // Push elements
                stack.push(10);
                stack.push(20);
                stack.push(30);
                stack.display(); // Stack (top to bottom): 30 20 10
                
                // Pop and peek
                System.out.println("Popped: " + stack.pop()); // 30
                System.out.println("Top: " + stack.peek()); // 20
                
                // Check state
                System.out.println("Size: " + stack.size()); // 2
                System.out.println("Is full: " + stack.isFull()); // false
                """
        ));

        codeExamples.put("Linked List Stack", new CodeExample(
                "Linked List-Based Stack Implementation",
                "Dynamic stack implementation using linked list",
                "O(1) for all operations",
                """
                // Linked list-based stack implementation
                public class LinkedStack<T> {
                    
                    private static class Node<T> {
                        T data;
                        Node<T> next;
                        
                        Node(T data) {
                            this.data = data;
                            this.next = null;
                        }
                    }
                    
                    private Node<T> head;
                    private int size;
                    
                    public LinkedStack() {
                        this.head = null;
                        this.size = 0;
                    }
                    
                    public boolean isEmpty() {
                        return head == null;
                    }
                    
                    public int size() {
                        return size;
                    }
                    
                    public void push(T element) {
                        Node<T> newNode = new Node<>(element);
                        newNode.next = head;
                        head = newNode;
                        size++;
                    }
                    
                    public T pop() {
                        if (isEmpty()) {
                            throw new RuntimeException("Stack Underflow");
                        }
                        
                        T data = head.data;
                        head = head.next;
                        size--;
                        return data;
                    }
                    
                    public T peek() {
                        if (isEmpty()) {
                            throw new RuntimeException("Stack is empty");
                        }
                        return head.data;
                    }
                    
                    // Clear all elements
                    public void clear() {
                        head = null;
                        size = 0;
                    }
                    
                    // Display stack contents
                    public void display() {
                        if (isEmpty()) {
                            System.out.println("Stack is empty");
                            return;
                        }
                        
                        System.out.print("Stack (top to bottom): ");
                        Node<T> current = head;
                        while (current != null) {
                            System.out.print(current.data + " ");
                            current = current.next;
                        }
                        System.out.println();
                    }
                    
                    // Check if element exists
                    public boolean contains(T element) {
                        Node<T> current = head;
                        while (current != null) {
                            if (current.data.equals(element)) {
                                return true;
                            }
                            current = current.next;
                        }
                        return false;
                    }
                    
                    // Get stack as list (top to bottom)
                    public List<T> toList() {
                        List<T> result = new ArrayList<>();
                        Node<T> current = head;
                        while (current != null) {
                            result.add(current.data);
                            current = current.next;
                        }
                        return result;
                    }
                }
                
                // Usage Example:
                LinkedStack<String> stack = new LinkedStack<>();
                
                // Push elements
                stack.push("First");
                stack.push("Second");
                stack.push("Third");
                stack.display(); // Stack (top to bottom): Third Second First
                
                // Operations
                System.out.println("Size: " + stack.size()); // 3
                System.out.println("Contains 'Second': " + stack.contains("Second")); // true
                System.out.println("Peek: " + stack.peek()); // Third
                System.out.println("Pop: " + stack.pop()); // Third
                
                // Convert to list
                List<String> stackList = stack.toList();
                System.out.println("As list: " + stackList); // [Second, First]
                """
        ));

        codeExamples.put("Generic Stack", new CodeExample(
                "Generic Stack with Advanced Features",
                "Type-safe stack with iterator support and advanced operations",
                "O(1) for basic operations, O(n) for advanced operations",
                """
                // Advanced generic stack implementation
                public class AdvancedStack<T> implements Iterable<T> {
                    private ArrayList<T> stack;
                    private int maxSize;
                    
                    public AdvancedStack() {
                        this(Integer.MAX_VALUE);
                    }
                    
                    public AdvancedStack(int maxSize) {
                        this.maxSize = maxSize;
                        this.stack = new ArrayList<>();
                    }
                    
                    // Basic operations
                    public void push(T element) {
                        if (size() >= maxSize) {
                            throw new IllegalStateException("Stack overflow");
                        }
                        stack.add(element);
                    }
                    
                    public T pop() {
                        if (isEmpty()) {
                            throw new RuntimeException("Stack underflow");
                        }
                        return stack.remove(stack.size() - 1);
                    }
                    
                    public T peek() {
                        if (isEmpty()) {
                            throw new RuntimeException("Stack is empty");
                        }
                        return stack.get(stack.size() - 1);
                    }
                    
                    public boolean isEmpty() {
                        return stack.isEmpty();
                    }
                    
                    public int size() {
                        return stack.size();
                    }
                    
                    // Advanced operations
                    
                    // Search for element (returns position from top, 1-based)
                    public int search(T element) {
                        int index = stack.lastIndexOf(element);
                        if (index >= 0) {
                            return stack.size() - index;
                        }
                        return -1;
                    }
                    
                    // Swap top two elements
                    public void swapTop() {
                        if (size() < 2) {
                            throw new IllegalStateException("Need at least 2 elements to swap");
                        }
                        
                        T first = pop();
                        T second = pop();
                        push(first);
                        push(second);
                    }
                    
                    // Duplicate top element
                    public void duplicate() {
                        if (isEmpty()) {
                            throw new IllegalStateException("Cannot duplicate empty stack");
                        }
                        push(peek());
                    }
                    
                    // Rotate top n elements
                    public void rotate(int n) {
                        if (n < 2 || n > size()) {
                            throw new IllegalArgumentException("Invalid rotation size: " + n);
                        }
                        
                        List<T> temp = new ArrayList<>();
                        for (int i = 0; i < n; i++) {
                            temp.add(pop());
                        }
                        
                        // Push back in rotated order (move top to bottom of group)
                        T topElement = temp.get(0);
                        for (int i = n - 1; i > 0; i--) {
                            push(temp.get(i));
                        }
                        push(topElement);
                    }
                    
                    // Filter stack elements
                    public AdvancedStack<T> filter(Predicate<T> predicate) {
                        AdvancedStack<T> filtered = new AdvancedStack<>();
                        AdvancedStack<T> temp = new AdvancedStack<>();
                        
                        // Empty current stack to temp while filtering
                        while (!isEmpty()) {
                            T element = pop();
                            temp.push(element);
                            if (predicate.test(element)) {
                                filtered.push(element);
                            }
                        }
                        
                        // Restore original stack
                        while (!temp.isEmpty()) {
                            push(temp.pop());
                        }
                        
                        return filtered;
                    }
                    
                    // Map stack elements to new type
                    public <R> AdvancedStack<R> map(Function<T, R> mapper) {
                        AdvancedStack<R> mapped = new AdvancedStack<>();
                        AdvancedStack<T> temp = new AdvancedStack<>();
                        
                        while (!isEmpty()) {
                            T element = pop();
                            temp.push(element);
                            mapped.push(mapper.apply(element));
                        }
                        
                        // Restore original stack
                        while (!temp.isEmpty()) {
                            push(temp.pop());
                        }
                        
                        return mapped;
                    }
                    
                    // Iterator implementation
                    @Override
                    public Iterator<T> iterator() {
                        return new StackIterator();
                    }
                    
                    private class StackIterator implements Iterator<T> {
                        private int index = stack.size() - 1;
                        
                        @Override
                        public boolean hasNext() {
                            return index >= 0;
                        }
                        
                        @Override
                        public T next() {
                            if (!hasNext()) {
                                throw new NoSuchElementException();
                            }
                            return stack.get(index--);
                        }
                    }
                    
                    @Override
                    public String toString() {
                        return "AdvancedStack" + stack.toString();
                    }
                }
                
                // Usage Examples:
                AdvancedStack<Integer> stack = new AdvancedStack<>();
                
                // Basic operations
                stack.push(1);
                stack.push(2);
                stack.push(3);
                stack.push(4);
                
                System.out.println("Search for 2: " + stack.search(2)); // Position from top
                
                // Advanced operations
                stack.duplicate(); // Duplicate top element
                stack.swapTop();   // Swap top two
                stack.rotate(3);   // Rotate top 3 elements
                
                // Functional operations
                AdvancedStack<Integer> evens = stack.filter(x -> x % 2 == 0);
                AdvancedStack<String> strings = stack.map(Object::toString);
                
                // Iterate through stack
                System.out.println("Stack contents (top to bottom):");
                for (Integer element : stack) {
                    System.out.println(element);
                }
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
         * Stack Code Repository - Welcome
         * ================================================================
         *
         * This comprehensive collection includes:
         *
         * 📚 Basic Operations:
         * • Create, Push, Pop, Peek operations
         * • Stack overflow and underflow handling
         * • Size and capacity management
         * • Clear and reset operations
         *
         * 🔧 Implementation Details:
         * • Array-based implementation (fixed size)
         * • LinkedList-based implementation (dynamic)
         * • Generic stack for any data type
         * • Advanced stack with functional features
         *
         * 🚀 Advanced Applications:
         * • Balanced parentheses checker with error reporting
         * • String reversal and palindrome detection
         * • Mathematical expression evaluation (infix/postfix)
         * • Function call stack simulation
         * • Recursive algorithm tracking
         *
         * ⚙️ Stack Management:
         * • Comprehensive overflow/underflow detection
         * • Safe operations with Optional returns
         * • Bulk operations (push/pop multiple elements)
         * • Conditional operations with predicates
         * • Auto-resizing capabilities
         *
         * 💡 Advanced Features:
         * • Iterator support for traversal
         * • Functional programming operations (map, filter)
         * • Element search and manipulation
         * • Stack rotation and swapping operations
         * • Performance monitoring and statistics
         *
         * 🎯 Real-world Applications:
         * • Undo/Redo functionality implementation
         * • Browser back/forward navigation
         * • Compiler expression parsing
         * • Memory management simulation
         * • Algorithm recursion tracking
         *
         * Select any operation from the left panel to begin exploring!
         *
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
