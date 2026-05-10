package com.binarization;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BinarizerTest {

    @Test
    void testPaperExample() {
        // Table 1 from the paper
        char[] data = "AABCBACBBACCABACB".toCharArray();
        char[] order = {'A', 'B', 'C'};

        BinarizationResult result = Binarizer.binarize(data, order);

        assertEquals(17, result.getBinaryStrings()[0].length);
        assertEquals(11, result.getBinaryStrings()[1].length);

        // verify first binary string matches paper: 11000100010010100
        int[] expected0 = {1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0};
        assertArrayEquals(expected0, result.getBinaryStrings()[0]);

        // verify second binary string matches paper: 10101100101
        int[] expected1 = {1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1};
        assertArrayEquals(expected1, result.getBinaryStrings()[1]);
    }

    @Test
    void testRoundtrip() {
        char[] data = "AABCBACBBACCABACB".toCharArray();
        char[] order = {'A', 'B', 'C'};

        BinarizationResult result = Binarizer.binarize(data, order);
        char[] recovered = Binarizer.debinarize(result);

        assertArrayEquals(data, recovered, "De-binarization should recover original data");
    }

    @Test
    void testRoundtripAllOrders() {
        char[] data = "AABCBACBBACCABACB".toCharArray();
        char[][] orders = {
                {'A', 'B', 'C'}, {'A', 'C', 'B'},
                {'B', 'A', 'C'}, {'B', 'C', 'A'},
                {'C', 'A', 'B'}, {'C', 'B', 'A'}
        };

        for (char[] order : orders) {
            BinarizationResult result = Binarizer.binarize(data, order);
            char[] recovered = Binarizer.debinarize(result);
            assertArrayEquals(data, recovered,
                    "Roundtrip should work for order " + new String(order));
        }
    }

    @Test
    void testEntropyConservation() {
        char[] data = "AABCBACBBACCABACB".toCharArray();
        char[] order = {'A', 'B', 'C'};

        BinarizationResult result = Binarizer.binarize(data, order);

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);

        assertEquals(sourceEntropy, binarizedEntropy, 1e-6,
                "Binarized entropy should equal source entropy");
    }

    @Test
    void testEntropyConservationAllOrders() {
        char[] data = "AABCBACBBACCABACB".toCharArray();
        char[][] orders = {
                {'A', 'B', 'C'}, {'A', 'C', 'B'},
                {'B', 'A', 'C'}, {'B', 'C', 'A'},
                {'C', 'A', 'B'}, {'C', 'B', 'A'}
        };

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);

        for (char[] order : orders) {
            BinarizationResult result = Binarizer.binarize(data, order);
            double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);

            assertEquals(sourceEntropy, binarizedEntropy, 1e-6,
                    "Entropy should be conserved for order " + new String(order));
        }
    }

    @Test
    void testUniformDistribution() {
        char[] data = "ABCDABCDABCDABCD".toCharArray();
        char[] order = {'A', 'B', 'C', 'D'};

        BinarizationResult result = Binarizer.binarize(data, order);
        char[] recovered = Binarizer.debinarize(result);

        assertArrayEquals(data, recovered);

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
        assertEquals(sourceEntropy, binarizedEntropy, 1e-6);
    }

    @Test
    void testSkewedDistribution() {
        char[] data = "AAAAAAAABBCCCCCC".toCharArray();
        char[] order = {'A', 'B', 'C'};

        BinarizationResult result = Binarizer.binarize(data, order);
        char[] recovered = Binarizer.debinarize(result);

        assertArrayEquals(data, recovered);

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
        assertEquals(sourceEntropy, binarizedEntropy, 1e-6);
    }

    @Test
    void testBinarySource() {
        // m=2: only one binary string produced
        char[] data = "AABBAABB".toCharArray();
        char[] order = {'A', 'B'};

        BinarizationResult result = Binarizer.binarize(data, order);
        assertEquals(1, result.getBinaryStrings().length);

        char[] recovered = Binarizer.debinarize(result);
        assertArrayEquals(data, recovered);

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
        assertEquals(sourceEntropy, binarizedEntropy, 1e-6);
    }

    @Test
    void testLargeRandomData() {
        Random rng = new Random(42);
        int n = 10000;
        char[] symbols = {'A', 'B', 'C', 'D', 'E'};
        char[] data = new char[n];
        for (int i = 0; i < n; i++) {
            data[i] = symbols[rng.nextInt(symbols.length)];
        }

        BinarizationResult result = Binarizer.binarize(data, symbols);
        char[] recovered = Binarizer.debinarize(result);

        assertArrayEquals(data, recovered, "Roundtrip should work for large random data");

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
        assertEquals(sourceEntropy, binarizedEntropy, 1e-4,
                "Entropy should be conserved for large random data");
    }
}