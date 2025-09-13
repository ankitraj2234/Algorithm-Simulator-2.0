package com.simulator;

import java.util.List;

public interface SortingAlgorithm {
    List<SortingController.SortingStep> generateSteps(int[] array);
}
