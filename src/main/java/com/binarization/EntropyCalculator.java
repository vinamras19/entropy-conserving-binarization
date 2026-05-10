package com.binarization;

import java.util.HashMap;
import java.util.Map;

public class EntropyCalculator {

    // Equation 1
    public static double sourceEntropyRate(char[] data) {
        if (data.length == 0) return 0.0;

        Map<Character, Integer> freq = new HashMap<>();
        for (char c : data) {
            freq.merge(c, 1, Integer::sum);
        }

        double entropy = 0.0;
        int n = data.length;
        for (int count : freq.values()) {
            double p = (double) count / n;
            entropy -= p * log2(p);
        }
        return entropy;
    }

    public static double binaryEntropyRate(int[] binary) {
        if (binary.length == 0) return 0.0;

        int ones = 0;
        for (int b : binary) ones += b;
        int zeros = binary.length - ones;

        if (ones == 0 || zeros == 0) return 0.0;

        double p1 = (double) ones / binary.length;
        double p0 = (double) zeros / binary.length;
        return -p1 * log2(p1) - p0 * log2(p0);
    }

    public static double totalSourceEntropy(char[] data) {
        return data.length * sourceEntropyRate(data);
    }

    // Equation 2
    public static double totalBinarizedEntropy(BinarizationResult result) {
        double total = 0.0;
        for (int[] binary : result.getBinaryStrings()) {
            total += binary.length * binaryEntropyRate(binary);
        }
        return total;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}