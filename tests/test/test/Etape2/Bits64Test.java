package test.test.Etape2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static test.test.TestRandomizer.RANDOM_ITERATIONS;
import static test.test.TestRandomizer.newRandom;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits64;

class Bits64Test {

    @Test
    void maskProducesCorrectMasks() {
        for (int size = 0; size <= Long.SIZE; ++size) {
            for (int start = 0; start <= Long.SIZE - size; ++start) {
//                if (start!=32 && (start+size)!=32) {
                    System.out.println(start+" "+ (start+size));
                long m = Bits64.mask(start, size);
                assertEquals(size, Long.bitCount(m));
                assertEquals(size, Long.bitCount(m >>> start));
                if (start + size < Long.SIZE)
                    assertEquals(0, m >>> (start + size));
//                }
            }
        }
    }
    
    @Test
    void maskFailsWithNegativeStart() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int start = -(i + 1);
            int size = rng.nextInt(Long.SIZE + 1);
            assertThrows(IllegalArgumentException.class, () -> {
                Bits64.mask(start, size);
            });
        }
    }
    
    @Test
    void maskFailsWithNegativeSize() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int start = rng.nextInt(Long.SIZE + 1);
            int size = -(i + 1);
            System.out.println(start+" "+size);
            assertThrows(IllegalArgumentException.class, () -> {
                Bits64.mask(start, size);
            });
        }
    }
    
    @Test
    void maskFailsWithTooBigSize() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int start = rng.nextInt(Long.SIZE + 1);
            int size = Long.SIZE- start + rng.nextInt(1, 10);
            assertThrows(IllegalArgumentException.class, () -> {
                Bits64.mask(start, size);
            });
        }
    }
    
    @Test
    void extractCanExtractAllBitsInOneGo() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            long bits = rng.nextLong();
            assertEquals(bits, Bits64.extract(bits, 0, Long.SIZE));
        }
    }
    
    @Test
    void extractCanExtractSubgroupsOfBits() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            long bits = rng.nextLong();
            for (int size = 1; size <= Long.SIZE; size *= 2) {
                long reComputedBits = 0L;
                for (int start = Long.SIZE - size; start >= 0; start -= size) 
                    
                    reComputedBits = (reComputedBits << size) | Bits64.extract(bits, start, size);
                 
                assertEquals(bits, reComputedBits);
                
            }
        }
    }
    
    @Test
    void extractFailsWithNegativeStart() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int start = -(i + 1);
            int size = rng.nextInt(Long.SIZE + 1);
            assertThrows(IllegalArgumentException.class, () -> {
                Bits64.extract(rng.nextInt(), start, size);
            });
        }
    }
    
    @Test
    void extractFailsWithNegativeSize() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int start = rng.nextInt(Long.SIZE + 1);
            int size = -(i + 1);
            assertThrows(IllegalArgumentException.class, () -> {
                Bits64.extract(rng.nextInt(), start, size);
            });
        }
    }
    
    @Test
    void extractFailsWithTooBigSize() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int start = rng.nextInt(Long.SIZE + 1);
            int size = Long.SIZE- start + rng.nextInt(1, 10);
            assertThrows(IllegalArgumentException.class, () -> {
                Bits64.extract(rng.nextInt(), start, size);
            });
        }
    }
    private int[] getSizes(SplittableRandom rng, int n) {
        int[] sizes = new int[n];
        int remainingBits = Long.SIZE;
        for (int i = 0; i < n; ++i) {
            sizes[i] = rng.nextInt(1, remainingBits - (n - 1 - i) + 1);
            remainingBits -= sizes[i];
        }
        return sizes;
    }
    
    private long[] getValues(SplittableRandom rng, int[] sizes) {
        long[] values = new long[sizes.length];
        for (int i = 0; i < sizes.length; ++i)
            if(sizes[i]!=63) {
            values[i] = rng.nextLong(1L << sizes[i]);
            }
            else {
                values[i] = rng.nextLong(1L << 62);
            }
        return values;
    }
    
    @Test
    void pack2Works() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int[] s = getSizes(rng, 2);
            long[] v = getValues(rng, s);
            long packed = Bits64.pack(v[0], s[0], v[1], s[1]);
            for (int j = 0; j < s.length; ++j) {
              assertEquals(v[j], packed & ((1l << s[j]) - 1));
              packed >>>= s[j];
            }
            assertEquals(0, packed);
        }
    }
    @Test
    void pack2FailsWithTooBigValues() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits64.pack(0b100, 2, 0b11, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits64.pack(0b100, 3, 0b11, 1);
        });
    }

    @Test
    void pack2FailsForTooManyBits() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits64.pack(0, 35, 0, 34);
        });
    }
    
    
    

}
