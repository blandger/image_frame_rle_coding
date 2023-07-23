package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RleEncoderDecoderTest {

    private RleEncoderDecoder rleEncoderDecoder;

    @BeforeEach
    void setUp() {
        rleEncoderDecoder = new RleEncoderDecoder();
    }

    @Test
    void test_rle_encode_empty() {
        Integer[] source = {};
        List<Integer> result = rleEncoderDecoder.encodeArray(source);
        Integer[] empty = {};
        assertArrayEquals(empty, result.toArray(Integer[]::new));
    }

    @Test
    void test_rle_encoding() {
        Integer[] source = {0,0,0,0,1,2,3,4,4,4,5,0,0,0,2,3,3,2,2,1};

        List<Integer> result = rleEncoderDecoder.encodeArray(source);

        Integer[] expected = {4,0,1,1,1,2,1,3,3,4,1,5,3,0,1,2,2,3,2,2,1,1};
        assertArrayEquals(expected, result.toArray(Integer[]::new));

        log.debug("source length = {}, result length = {}", source.length, expected.length);
    }

    @Test
    void test_rle_encoding_tiny() {
        Integer[] source = {0,0};

        List<Integer> result = rleEncoderDecoder.encodeArray(source);

        Integer[] expected = {2,0};
        assertArrayEquals(expected, result.toArray(Integer[]::new));

        log.debug("source length = {}, result length = {}", source.length, expected.length);
    }

    @Test
    void test_rle_decode_empty() {
        Integer[] source = {};
        List<Integer> result = rleEncoderDecoder.decodeArray(source);
        Integer[] empty = {};
        assertArrayEquals(empty, result.toArray(Integer[]::new));
    }

    @Test
    void test_rle_decoding() {
        Integer[] source = {4,0,1,1,1,2,1,3,3,4,1,5,3,0,1,2,2,3,2,2,1,1};

        List<Integer> result = rleEncoderDecoder.decodeArray(source);
        log.debug("result = {}", result);

        Integer[] expected = {0,0,0,0,1,2,3,4,4,4,5,0,0,0,2,3,3,2,2,1};
        assertArrayEquals(expected, result.toArray(Integer[]::new));

        log.debug("source length = {}, result length = {}", source.length, expected.length);
    }

    @Test
    void test_rle_decoding_tiny() {
        Integer[] source = {2,0};

        List<Integer> result = rleEncoderDecoder.decodeArray(source);
        log.debug("result = {}", result);

        Integer[] expected = {0,0};
        assertArrayEquals(expected, result.toArray(Integer[]::new));

        log.debug("source length = {}, result length = {}", source.length, expected.length);
    }

    @Test
    void test_rle_decode_incorrect_source_length() {
        Integer[] source = {1};
        Throwable e = assertThrows(IllegalArgumentException.class, () -> rleEncoderDecoder.decodeArray(source));
        assertEquals("source array must have an ODD LENGTH, but it has = 1", e.getMessage());

        Integer[] source2 = {0,1,2,3,4,5,6,7,8};
        e = assertThrows(IllegalArgumentException.class, () -> rleEncoderDecoder.decodeArray(source2));
        assertEquals("source array must have an ODD LENGTH, but it has = 9", e.getMessage());
    }
}