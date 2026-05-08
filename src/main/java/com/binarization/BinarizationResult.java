package com.binarization;

public class BinarizationResult {

    private final char[] originalData;
    private final int[][] binaryStrings;
    private final char[] symbolOrder;

    public BinarizationResult(char[] originalData, int[][] binaryStrings, char[] symbolOrder) {
        this.originalData = originalData;
        this.binaryStrings = binaryStrings;
        this.symbolOrder = symbolOrder;
    }

    public char[] getOriginalData() { return originalData; }
    public int[][] getBinaryStrings() { return binaryStrings; }
    public char[] getSymbolOrder() { return symbolOrder; }
    public int getOriginalLength() { return originalData.length; }
    public int getSymbolCount() { return symbolOrder.length; }
}