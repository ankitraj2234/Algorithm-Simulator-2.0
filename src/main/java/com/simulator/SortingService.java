package com.simulator;

import java.util.*;

/**
 * Service class that manages sorting algorithms and performance metrics
 */
public class SortingService {

    private int comparisons = 0;
    private int swaps = 0;
    private long startTime = 0;
    private final Map<String, SortingAlgorithm> algorithms;

    public SortingService() {
        algorithms = new HashMap<>();
        algorithms.put("Bubble Sort", new BubbleSortAlgorithm());
        algorithms.put("Selection Sort", new SelectionSortAlgorithm());
        algorithms.put("Insertion Sort", new InsertionSortAlgorithm());
        algorithms.put("Merge Sort", new MergeSortAlgorithm());
        algorithms.put("Quick Sort", new QuickSortAlgorithm());
        // NEW: Advanced sorting algorithms
        algorithms.put("Heap Sort", new HeapSortAlgorithm());
        algorithms.put("Shell Sort", new ShellSortAlgorithm());
        algorithms.put("Radix Sort", new RadixSortAlgorithm());
        algorithms.put("Counting Sort", new CountingSortAlgorithm());
    }

    public SortingAlgorithm getAlgorithm(String name) {
        return algorithms.get(name);
    }

    public void incrementComparisons() { comparisons++; }
    public void incrementSwaps() { swaps++; }
    public int getComparisons() { return comparisons; }
    public int getSwaps() { return swaps; }

    public void resetCounters() {
        comparisons = 0;
        swaps = 0;
    }

    public void startTiming() {
        startTime = System.currentTimeMillis();
    }

    public long stopTiming() {
        return System.currentTimeMillis() - startTime;
    }

    // ==================== EXISTING ALGORITHMS ====================

    private static class BubbleSortAlgorithm implements SortingAlgorithm {
        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            List<SortingController.SortingStep> steps = new ArrayList<>();
            int n = array.length;

            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.COMPARE, j, j + 1));

                    if (array[j] > array[j + 1]) {
                        steps.add(new SortingController.SortingStep(
                                SortingController.SortingStep.StepType.SWAP, j, j + 1));
                        // Perform actual swap
                        int temp = array[j];
                        array[j] = array[j + 1];
                        array[j + 1] = temp;
                    }
                }
            }
            return steps;
        }
    }

    private static class SelectionSortAlgorithm implements SortingAlgorithm {
        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            List<SortingController.SortingStep> steps = new ArrayList<>();
            int n = array.length;

            for (int i = 0; i < n - 1; i++) {
                int minIdx = i;
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.HIGHLIGHT, i));

                for (int j = i + 1; j < n; j++) {
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.COMPARE, minIdx, j));
                    if (array[j] < array[minIdx]) {
                        minIdx = j;
                    }
                }

                if (minIdx != i) {
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.SWAP, i, minIdx));
                    // Perform actual swap
                    int temp = array[i];
                    array[i] = array[minIdx];
                    array[minIdx] = temp;
                }
            }
            return steps;
        }
    }

    private static class InsertionSortAlgorithm implements SortingAlgorithm {
        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            List<SortingController.SortingStep> steps = new ArrayList<>();

            for (int i = 1; i < array.length; i++) {
                int key = array[i];
                int j = i - 1;

                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.HIGHLIGHT, i));

                while (j >= 0 && array[j] > key) {
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.COMPARE, j, j + 1));
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.SET, j + 1, -1, array[j]));
                    array[j + 1] = array[j];
                    j--;
                }

                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, j + 1, -1, key));
                array[j + 1] = key;
            }
            return steps;
        }
    }

    private static class MergeSortAlgorithm implements SortingAlgorithm {
        private List<SortingController.SortingStep> steps;
        private int[] workingArray;

        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            steps = new ArrayList<>();
            workingArray = Arrays.copyOf(array, array.length);
            mergeSort(0, array.length - 1);
            return steps;
        }

        private void mergeSort(int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;
                mergeSort(left, mid);
                mergeSort(mid + 1, right);
                merge(left, mid, right);
            }
        }

        private void merge(int left, int mid, int right) {
            int[] leftArray = new int[mid - left + 1];
            int[] rightArray = new int[right - mid];

            System.arraycopy(workingArray, left, leftArray, 0, leftArray.length);
            System.arraycopy(workingArray, mid + 1, rightArray, 0, rightArray.length);

            int i = 0, j = 0, k = left;

            while (i < leftArray.length && j < rightArray.length) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.COMPARE, left + i, mid + 1 + j));

                if (leftArray[i] <= rightArray[j]) {
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.SET, k, -1, leftArray[i]));
                    workingArray[k] = leftArray[i];
                    i++;
                } else {
                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.SET, k, -1, rightArray[j]));
                    workingArray[k] = rightArray[j];
                    j++;
                }
                k++;
            }

            // Copy remaining elements
            while (i < leftArray.length) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, k, -1, leftArray[i]));
                workingArray[k] = leftArray[i];
                i++;
                k++;
            }

            while (j < rightArray.length) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, k, -1, rightArray[j]));
                workingArray[k] = rightArray[j];
                j++;
                k++;
            }
        }
    }

    private static class QuickSortAlgorithm implements SortingAlgorithm {
        private List<SortingController.SortingStep> steps;
        private int[] workingArray;

        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            steps = new ArrayList<>();
            workingArray = Arrays.copyOf(array, array.length);
            quickSort(0, array.length - 1);
            return steps;
        }

        private void quickSort(int low, int high) {
            if (low < high) {
                int pivotIndex = partition(low, high);
                quickSort(low, pivotIndex - 1);
                quickSort(pivotIndex + 1, high);
            }
        }

        private int partition(int low, int high) {
            int pivot = workingArray[high];
            steps.add(new SortingController.SortingStep(
                    SortingController.SortingStep.StepType.HIGHLIGHT, high));

            int i = low - 1;

            for (int j = low; j < high; j++) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.COMPARE, j, high));

                if (workingArray[j] < pivot) {
                    i++;
                    if (i != j) {
                        steps.add(new SortingController.SortingStep(
                                SortingController.SortingStep.StepType.SWAP, i, j));
                        // Perform actual swap
                        int temp = workingArray[i];
                        workingArray[i] = workingArray[j];
                        workingArray[j] = temp;
                    }
                }
            }

            steps.add(new SortingController.SortingStep(
                    SortingController.SortingStep.StepType.SWAP, i + 1, high));
            // Final swap
            int temp = workingArray[i + 1];
            workingArray[i + 1] = workingArray[high];
            workingArray[high] = temp;

            return i + 1;
        }
    }

    // ==================== NEW ADVANCED ALGORITHMS ====================

    private static class HeapSortAlgorithm implements SortingAlgorithm {
        private List<SortingController.SortingStep> steps;
        private int[] workingArray;

        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            steps = new ArrayList<>();
            workingArray = Arrays.copyOf(array, array.length);
            heapSort();
            return steps;
        }

        private void heapSort() {
            int n = workingArray.length;

            // Build heap (rearrange array)
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(n, i);
            }

            // Extract elements from heap one by one
            for (int i = n - 1; i > 0; i--) {
                // Move current root to end
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SWAP, 0, i));
                int temp = workingArray[0];
                workingArray[0] = workingArray[i];
                workingArray[i] = temp;

                // Call heapify on the reduced heap
                heapify(i, 0);
            }
        }

        private void heapify(int n, int i) {
            int largest = i; // Initialize largest as root
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            // If left child is larger than root
            if (left < n) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.COMPARE, left, largest));
                if (workingArray[left] > workingArray[largest]) {
                    largest = left;
                }
            }

            // If right child is larger than largest so far
            if (right < n) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.COMPARE, right, largest));
                if (workingArray[right] > workingArray[largest]) {
                    largest = right;
                }
            }

            // If largest is not root
            if (largest != i) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SWAP, i, largest));
                int temp = workingArray[i];
                workingArray[i] = workingArray[largest];
                workingArray[largest] = temp;

                // Recursively heapify the affected sub-tree
                heapify(n, largest);
            }
        }
    }

    private static class ShellSortAlgorithm implements SortingAlgorithm {
        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            List<SortingController.SortingStep> steps = new ArrayList<>();
            int n = array.length;

            // Start with a big gap, then reduce the gap
            for (int gap = n / 2; gap > 0; gap /= 2) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.HIGHLIGHT, gap % n));

                // Do a gapped insertion sort for this gap size
                for (int i = gap; i < n; i++) {
                    int temp = array[i];
                    int j;

                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.HIGHLIGHT, i));

                    for (j = i; j >= gap; j -= gap) {
                        steps.add(new SortingController.SortingStep(
                                SortingController.SortingStep.StepType.COMPARE, j - gap, j));

                        if (array[j - gap] > temp) {
                            steps.add(new SortingController.SortingStep(
                                    SortingController.SortingStep.StepType.SET, j, -1, array[j - gap]));
                            array[j] = array[j - gap];
                        } else {
                            break;
                        }
                    }

                    steps.add(new SortingController.SortingStep(
                            SortingController.SortingStep.StepType.SET, j, -1, temp));
                    array[j] = temp;
                }
            }
            return steps;
        }
    }

    private static class RadixSortAlgorithm implements SortingAlgorithm {
        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            List<SortingController.SortingStep> steps = new ArrayList<>();

            // Find the maximum number to know number of digits
            int max = Arrays.stream(array).max().orElse(0);

            // Do counting sort for every digit
            for (int exp = 1; max / exp > 0; exp *= 10) {
                countingSortByDigit(array, steps, exp);
            }

            return steps;
        }

        private void countingSortByDigit(int[] array, List<SortingController.SortingStep> steps, int exp) {
            int n = array.length;
            int[] output = new int[n];
            int[] count = new int[10];

            // Store count of occurrences of each digit
            for (int i = 0; i < n; i++) {
                int digit = (array[i] / exp) % 10;
                count[digit]++;
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.HIGHLIGHT, i));
            }

            // Change count[i] so that it contains actual position of this digit in output[]
            for (int i = 1; i < 10; i++) {
                count[i] += count[i - 1];
            }

            // Build the output array
            for (int i = n - 1; i >= 0; i--) {
                int digit = (array[i] / exp) % 10;
                output[count[digit] - 1] = array[i];
                count[digit]--;

                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, count[digit], -1, array[i]));
            }

            // Copy the output array to array[]
            for (int i = 0; i < n; i++) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, i, -1, output[i]));
                array[i] = output[i];
            }
        }
    }

    private static class CountingSortAlgorithm implements SortingAlgorithm {
        @Override
        public List<SortingController.SortingStep> generateSteps(int[] array) {
            List<SortingController.SortingStep> steps = new ArrayList<>();

            if (array.length == 0) return steps;

            // Find range
            int max = Arrays.stream(array).max().orElse(0);
            int min = Arrays.stream(array).min().orElse(0);
            int range = max - min + 1;

            // Create count array
            int[] count = new int[range];
            int[] output = new int[array.length];

            // Count occurrences
            for (int i = 0; i < array.length; i++) {
                count[array[i] - min]++;
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.HIGHLIGHT, i));
            }

            // Calculate cumulative count
            for (int i = 1; i < range; i++) {
                count[i] += count[i - 1];
            }

            // Build output array
            for (int i = array.length - 1; i >= 0; i--) {
                int value = array[i];
                int pos = count[value - min] - 1;
                output[pos] = value;
                count[value - min]--;

                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, pos, -1, value));
            }

            // Copy output array back to original array
            for (int i = 0; i < array.length; i++) {
                steps.add(new SortingController.SortingStep(
                        SortingController.SortingStep.StepType.SET, i, -1, output[i]));
                array[i] = output[i];
            }

            return steps;
        }
    }
}
