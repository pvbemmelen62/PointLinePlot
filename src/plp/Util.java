package plp;

import java.util.*;
import java.util.stream.*;

public class Util {

  /** Assert that b is true; if it isn't, throw AssertionError . */
  public static void myAssert(boolean b) {
    if(!b) {
      throw new AssertionError();
    }
  }
  /** Assert that b is true; if it isn't, throw AssertionError . */
  public static void myAssert(boolean b, String msg) {
    if(!b) {
      throw new AssertionError(msg);
    }
  }
  public static boolean equalsApprox(double d0, double d1) {
    // ieee double: mantissa 52 bits
    // 10 bits approx 1e-3 , so 52 bits approx 1e-15 .
    double epsilon = 1e-8;
    return d0-d1 < epsilon;
  }
  public static <T> void ensureIndex(ArrayList<T> arr, int index, T defVal) {
    arr.ensureCapacity(index+1);
    while(arr.size() < index+1) {
      arr.add(defVal);
    }
  }
  public static void arrayListIncr(ArrayList<Integer> numbers, int index,
      int change) {
    numbers.set(index, numbers.get(index)+change);
  }
  public static int sqr(int i) {
    return i*i;
  }
  public static long sqr(long i) {
    return i*i;
  }
  public static double sqr(double x) {
    return x*x;
  }
  /**
   * Returns numbers 0..max in random order.
   * @param random
   * @param max
   * @return
   */
  public static int[] permutation(Random random, int max) {
    int[] numbers = IntStream.range(0, max+1).toArray();
    for(int i=max; i>=1; --i) {
      int j = random.nextInt(i+1);   // j in 0...i
      swap(numbers, i, j);
    }
    return numbers;
  }
  /** Swaps values in ns at index i and j. */
  public static void swap(int[] ns, int i, int j) {
    if(i==j) {
      return;
    }
    int tmp = ns[i];
    ns[i] = ns[j];
    ns[j] = tmp;
  }
  /** Returns index of first occurrence of value, or -1 if not found. */
  public static int findFirst(int[] ns, int value) {
    int i0 = -1;
    for(int i=0; i<ns.length; ++i) {
      if(ns[i]==value) {
        i0 = i;
        break;
      }
    }
    return i0;
  }
}
