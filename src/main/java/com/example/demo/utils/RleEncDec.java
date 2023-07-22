package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RleEncDec {

    public static List<Integer> encodeArray(int[] source) {
        Objects.requireNonNull(source, "source array is null");
        List<Integer> result = new ArrayList<>(source.length);
        if (source.length == 0) {
            return result;
        }
        int count;
        for (int i = 0; i < source.length; i++) {
            // count occurrences of int at index `i`
            count = 1;
            while (i + 1 < source.length && source[i] == source[i + 1]) {
                count++;
                i++;
            }
            // append current character and its count to the result
            result.add(count);
            result.add(source[i]);
        }
        return result;
    }

    public static List<Integer> decodeArray(int[] source) {
        Objects.requireNonNull(source, "source array is null");
        List<Integer> result = new ArrayList<>(source.length);
        if (source.length == 0) {
            return result;
        }
        if (source.length %2 != 0) {
            throw new IllegalArgumentException("source array must have odd length");
        }
        int count;
        int value;
        for (int i = 0; i <= source.length - 2; i+=2) {
            count = source[i]; // number of repeats
            value = source[i + 1]; // value to be repeated
            while (count > 0) {
                result.add(value);
                count--;
            }
        }
        return result;
    }

}
