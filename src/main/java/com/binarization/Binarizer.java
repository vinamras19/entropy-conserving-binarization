package com.binarization;

import java.util.Arrays;

public class Binarizer {

    // Section 2
    public static BinarizationResult binarize(char[] data, char[] symbolOrder) {
        int m = symbolOrder.length;
        int[][] binaryStrings = new int[m - 1][];
        char[] remaining = Arrays.copyOf(data, data.length);

        for (int i = 0; i < m - 1; i++) {
            char symbol = symbolOrder[i];
            int[] binary = new int[remaining.length];

            int nextLen = 0;
            for (int j = 0; j < remaining.length; j++) {
                if (remaining[j] == symbol) {
                    binary[j] = 1;
                } else {
                    nextLen++;
                }
            }

            binaryStrings[i] = binary;

            char[] next = new char[nextLen];
            int idx = 0;
            for (int j = 0; j < remaining.length; j++) {
                if (remaining[j] != symbol) {
                    next[idx++] = remaining[j];
                }
            }
            remaining = next;
        }

        return new BinarizationResult(data, binaryStrings, symbolOrder);
    }

    // Section 2, Table 2
    public static char[] debinarize(BinarizationResult result) {
        int[][] binaryStrings = result.getBinaryStrings();
        char[] symbolOrder = result.getSymbolOrder();
        int m = symbolOrder.length;

        // start from deepest level: last binary string maps to last two symbols
        int[] deepest = binaryStrings[m - 2];
        char[] reconstructed = new char[deepest.length];
        for (int j = 0; j < deepest.length; j++) {
            reconstructed[j] = deepest[j] == 1 ? symbolOrder[m - 2] : symbolOrder[m - 1];
        }

        // work backwards through remaining levels
        for (int i = m - 3; i >= 0; i--) {
            int[] binary = binaryStrings[i];
            char[] expanded = new char[binary.length];
            int fillIdx = 0;
            for (int j = 0; j < binary.length; j++) {
                if (binary[j] == 1) {
                    expanded[j] = symbolOrder[i];
                } else {
                    expanded[j] = reconstructed[fillIdx++];
                }
            }
            reconstructed = expanded;
        }

        return reconstructed;
    }
}