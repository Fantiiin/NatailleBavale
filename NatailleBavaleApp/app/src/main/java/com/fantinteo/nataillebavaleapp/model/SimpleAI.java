package com.fantinteo.nataillebavaleapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleAI {
    private final Random random = new Random();
    private final int rows;
    private final int cols;
    private final List<int[]> availableTargets;

    public SimpleAI(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.availableTargets = new ArrayList<>();
        initializeTargets();
    }

    private void initializeTargets() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                availableTargets.add(new int[]{x, y});
            }
        }
    }

    public int[] getNextShot() {
        if (availableTargets.isEmpty()) {
            return null;
        }

        int index = random.nextInt(availableTargets.size());
        int[] target = availableTargets.remove(index);

        return target;
    }
}