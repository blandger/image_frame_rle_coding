package com.example.demo.utils;

/**
 * Record is used for composing bufferImage comparison result + integer difference array
 * @param isEqual
 * @param outputDifferenceResultArray
 */
public record BufferComparisonResult(boolean isEqual, Integer[] outputDifferenceResultArray) {}
