package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class implements RLE en/de coding for integer array to list
 */
@Slf4j
public class RleEncoderDecoder {

    /**
     * Bigger initial, source int array is converted into smaller RLE int array
     * @param source image pixels linearized as int array
     * @return list of 'count' + 'value' pair integers (result has ALWAYS ODD size !)
     */
    public List<Integer> encodeArray(Integer[] source) {
        Objects.requireNonNull(source, "source array is null");
        List<Integer> result = new LinkedList<>();
        if (source.length == 0) {
            return result;
        }
        int count;
        for (int i = 0; i < source.length; i++) {
            // count occurrences of int at index `i`
            count = 1;
            while (i < source.length - 1 && source[i] == source[i + 1]) {
                count++;
                i++;
            }
            // append 'count' and 'value' to the result
            result.add(count); // number of repetitions
            Integer value = source[i]; // repeated value
            if (value == null) { // can be null at the end of array
                value = 0;
            }
            result.add(value);
        }
        log.debug("Encode: Input has size = [{}] elements, output has [{}] elements", source.length, result.size());
        return result;
    }

    /**
     * Converts smaller RLE int array into bigger int pixel array
     * @param source small RLE encoded int array, it has ALWAYS ODD size !
     * @return bigger, linearized int List for pixels
     */
    public List<Integer> decodeArray(Integer[] source) {
        Objects.requireNonNull(source, "source array is null");
        List<Integer> result = new LinkedList<>();
        if (source.length == 0) {
            return result;
        }
        if (source.length % 2 != 0) {
            throw new IllegalArgumentException("source array must have an ODD LENGTH, but it has = " + source.length);
        }
        int count;
        int value;
        for (int i = 0; i <= source.length - 2; i+=2) {
            count = source[i]; // number of repeats
            value = source[i + 1]; // value to be repeated
            while (count > 0) {
                result.add(value); // put into result array value as many times as stored in 'count'
                count--;
            }
        }
        log.debug("Decode: Input has size = [{}] elements, output has [{}] elements", source.length, result.size());
        return result;
    }

}
