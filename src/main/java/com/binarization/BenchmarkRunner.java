package com.binarization;

import java.util.*;

public class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("=== Entropy-Conserving Binarization ===\n");

        // Table 1
        runExample("AABCBACBBACCABACB", new char[]{'A', 'B', 'C'});

        // uniform distribution
        runExample("ABCDABCDABCDABCD", new char[]{'A', 'B', 'C', 'D'});

        // skewed distribution
        runExample("AAAAAAAABBCCCCCC", new char[]{'A', 'B', 'C'});

        // binary source (edge case)
        runExample("AABBAABB", new char[]{'A', 'B'});

        // all binarization orders for paper's example
        System.out.println("--- Order Independence ---");
        verifyOrderIndependence("AABCBACBBACCABACB", new char[]{'A', 'B', 'C'});

        // random data benchmark
        System.out.println("--- Random Data Benchmark ---");
        benchmarkRandom(1000, 4);
        benchmarkRandom(10000, 8);
        benchmarkRandom(100000, 16);
    }

    private static void runExample(String input, char[] symbols) {
        char[] data = input.toCharArray();

        System.out.printf("Input: %s (length=%d, symbols=%d)%n", input, data.length, symbols.length);

        BinarizationResult result = Binarizer.binarize(data, symbols);

        for (int i = 0; i < result.getBinaryStrings().length; i++) {
            int[] bs = result.getBinaryStrings()[i];
            StringBuilder sb = new StringBuilder();
            for (int b : bs) sb.append(b);
            System.out.printf("  String %d (symbol '%c', len=%d): %s%n",
                    i + 1, symbols[i], bs.length, sb);
        }

        char[] recovered = Binarizer.debinarize(result);
        boolean match = Arrays.equals(data, recovered);
        System.out.printf("  Roundtrip: %s%n", match ? "PASS" : "FAIL");

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
        double diff = Math.abs(sourceEntropy - binarizedEntropy);
        System.out.printf("  Source entropy:     %.6f bits%n", sourceEntropy);
        System.out.printf("  Binarized entropy:  %.6f bits%n", binarizedEntropy);
        System.out.printf("  Difference:         %.10f bits%n", diff);
        System.out.printf("  Conserved: %s%n%n", diff < 1e-6 ? "YES" : "NO");
    }

    private static void verifyOrderIndependence(String input, char[] symbols) {
        char[] data = input.toCharArray();
        List<char[]> permutations = permute(symbols);

        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        System.out.printf("  Source entropy: %.6f bits%n", sourceEntropy);

        for (char[] order : permutations) {
            BinarizationResult result = Binarizer.binarize(data, order);
            double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
            char[] recovered = Binarizer.debinarize(result);
            boolean roundtrip = Arrays.equals(data, recovered);

            System.out.printf("  Order %s: entropy=%.6f, roundtrip=%s%n",
                    new String(order), binarizedEntropy, roundtrip ? "PASS" : "FAIL");
        }
        System.out.println();
    }

    private static void benchmarkRandom(int dataLength, int alphabetSize) {
        Random rng = new Random(42);
        char[] symbols = new char[alphabetSize];
        for (int i = 0; i < alphabetSize; i++) {
            symbols[i] = (char) ('A' + i);
        }

        char[] data = new char[dataLength];
        for (int i = 0; i < dataLength; i++) {
            data[i] = symbols[rng.nextInt(alphabetSize)];
        }

        long start = System.nanoTime();
        BinarizationResult result = Binarizer.binarize(data, symbols);
        long binarizeTime = System.nanoTime() - start;

        start = System.nanoTime();
        char[] recovered = Binarizer.debinarize(result);
        long debinarizeTime = System.nanoTime() - start;

        boolean roundtrip = Arrays.equals(data, recovered);
        double sourceEntropy = EntropyCalculator.totalSourceEntropy(data);
        double binarizedEntropy = EntropyCalculator.totalBinarizedEntropy(result);
        double diff = Math.abs(sourceEntropy - binarizedEntropy);

        System.out.printf("  N=%d, m=%d: binarize=%.3fms, debinarize=%.3fms, " +
                        "roundtrip=%s, entropy_diff=%.10f%n",
                dataLength, alphabetSize,
                binarizeTime / 1e6, debinarizeTime / 1e6,
                roundtrip ? "PASS" : "FAIL", diff);
    }

    private static List<char[]> permute(char[] symbols) {
        List<char[]> result = new ArrayList<>();
        permuteHelper(symbols, 0, result);
        return result;
    }

    private static void permuteHelper(char[] arr, int start, List<char[]> result) {
        if (start == arr.length - 1) {
            result.add(Arrays.copyOf(arr, arr.length));
            return;
        }
        for (int i = start; i < arr.length; i++) {
            char temp = arr[start];
            arr[start] = arr[i];
            arr[i] = temp;
            permuteHelper(arr, start + 1, result);
            arr[i] = arr[start];
            arr[start] = temp;
        }
    }
}