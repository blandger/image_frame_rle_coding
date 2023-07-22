package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RleEncDecTest {

    @Test
    void test_rle_encode_empty() {
        int[] source = {};
        List<Integer> result = RleEncDec.encodeArray(source);
        Integer[] empty = {};
        assertArrayEquals(empty, result.toArray(Integer[]::new));
    }

    @Test
    void test_rle_encoding() {
        int[] source = {0,0,0,0,1,2,3,4,4,4,5,0,0,0,2,3,3,2,2,1};

        List<Integer> result = RleEncDec.encodeArray(source);

        Integer[] expected = {4,0,1,1,1,2,1,3,3,4,1,5,3,0,1,2,2,3,2,2,1,1};
        assertArrayEquals(expected, result.toArray(Integer[]::new));

        log.debug("source length = {}, result length = {}", source.length, expected.length);
    }

    @Test
    void test_rle_decode_empty() {
        int[] source = {};
        List<Integer> result = RleEncDec.decodeArray(source);
        Integer[] empty = {};
        assertArrayEquals(empty, result.toArray(Integer[]::new));
    }

    @Test
    void test_rle_decoding() {
        int[] source = {4,0,1,1,1,2,1,3,3,4,1,5,3,0,1,2,2,3,2,2,1,1};

        List<Integer> result = RleEncDec.decodeArray(source);
        log.debug("result = {}", result);

        Integer[] expected = {0,0,0,0,1,2,3,4,4,4,5,0,0,0,2,3,3,2,2,1};
        assertArrayEquals(expected, result.toArray(Integer[]::new));

        log.debug("source length = {}, result length = {}", source.length, expected.length);
    }
}