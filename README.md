# Entropy-Conserving Binarization

Implementation of the entropy-conserving binarization scheme presented in [arXiv:1408.3083](https://arxiv.org/abs/1408.3083). The algorithm converts m-ary source data into m-1 binary strings while provably preserving the total Shannon entropy of the original source, with linear time complexity.

## Background

Most binarization schemes (unary, truncated unary, Golomb, fixed-length) are optimal only for specific probability distributions. This scheme conserves entropy for any probability distribution of the source symbols, without requiring prior knowledge of the distribution. The binarization order is arbitrary — all m! orderings produce the same total entropy.

## Project Structure
```text
src/main/java/com/binarization/
  Binarizer.java            - Binarization and de-binarization (Section 2)
  BinarizationResult.java   - Result container
  EntropyCalculator.java    - Shannon entropy computation (Equations 1, 2)
  BenchmarkRunner.java      - Verification and benchmarks

src/test/java/com/binarization/
  BinarizerTest.java        - Unit tests
```

## Getting Started

### Prerequisites

* Java 17+
* Maven 3.6+

### Build
* Compile the application and run unit tests.
```text
mvn clean package
```

### Run Benchmarks
```text
java -jar target/entropy-conserving-binarization-1.0.0.jar
```

## Verification

The implementation empirically verifies the central theorem of the paper (Equations 2–12).
```text
N · H(Y) = Σ N_i · H(X_i)

where:
  N        - source data length
  H(Y)     - source entropy rate
  N_i      - length of the i-th binary string
  H(X_i)   - binary entropy rate of the i-th string

Conservation is asserted to within 1e-6 (1e-4 for the N=10,000 stress test).
```

## Tests
```text
Run mvn test to verify the binarization scheme.

Test coverage:
  testPaperExample                  - Byte-for-byte match against Table 1
  testRoundtrip                     - Lossless reconstruction for default order
  testRoundtripAllOrders            - Lossless reconstruction across all 6 orderings
  testEntropyConservation           - Entropy theorem verified for default order
  testEntropyConservationAllOrders  - Entropy theorem verified across all orderings
  testUniformDistribution           - Conservation under uniform source
  testSkewedDistribution            - Conservation under skewed source
  testBinarySource                  - Edge case (m=2)
  testLargeRandomData               - Roundtrip + conservation at N=10,000, m=5
```

## Implementation Notes
```text
Binarizer.binarize / Binarizer.debinarize    - Two-step encoder/decoder (Section 2)
EntropyCalculator.totalBinarizedEntropy      - Computes Σ N_i · H(X_i) (Equation 2)
BinarizerTest.testPaperExample               - Reproduces Table 1 exactly
BinarizerTest.testEntropyConservationAllOrders - Verifies entropy theorem across orders
```

## Sample Output
```text
=== Entropy-Conserving Binarization ===
Input: AABCBACBBACCABACB (length=17, symbols=3)
  String 1 (symbol 'A', len=17): 11000100010010100
  String 2 (symbol 'B', len=11): 10101100101
  Roundtrip: PASS
  Source entropy:     26.857678 bits
  Binarized entropy:  26.857678 bits
  Difference:         0.0000000000 bits
  Conserved: YES

--- Order Independence ---
  Source entropy: 26.857678 bits
  Order ABC: entropy=26.857678, roundtrip=PASS
  Order ACB: entropy=26.857678, roundtrip=PASS
  Order BAC: entropy=26.857678, roundtrip=PASS
  Order BCA: entropy=26.857678, roundtrip=PASS
  Order CBA: entropy=26.857678, roundtrip=PASS
  Order CAB: entropy=26.857678, roundtrip=PASS

--- Random Data Benchmark ---
N=1000, m=4: binarize=0.083ms, debinarize=0.057ms, roundtrip=PASS, entropy_diff=0.0000000000
N=10000, m=8: binarize=1.688ms, debinarize=0.842ms, roundtrip=PASS, entropy_diff=0.0000000000
N=100000, m=16: binarize=5.978ms, debinarize=3.700ms, roundtrip=PASS, entropy_diff=0.0000000000
```

## Reference

*Entropy Conserving Binarization Scheme for Video and Image Compression*. [arXiv:1408.3083](https://arxiv.org/abs/1408.3083).

## License
See `LICENSE` for more information.