package com.fibapp.service;

import com.fibapp.exception.FibonacciRuntimeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Service
public class FibonacciService {

    private final Map<String, Integer> clientIndexStorage = new ConcurrentHashMap<>();
    private final Map<Integer, Long> fiboIndexValueStorage = new ConcurrentHashMap<>(){{
       put(0, 1L);
       put(1, 1L);
    }};

    public long next(String clientId) {
        final int currentIndex = clientIndexStorage.compute(
                clientId, (clientKey,indexValue) -> indexValue == null ? 0 : ++indexValue
        );
        return computeFibonacciNumber(currentIndex);
    }

    public String back(String clientId) {
        clientIndexStorage.compute(clientId, (clientKey, indexValue) -> {
            if (indexValue == null || indexValue == 0) {
                throw new FibonacciRuntimeException("Back limit reached");
            }
            return --indexValue;
        });
        return "OK";
    }

    public List<Long> list(String clientId) {
        final Integer currentIndex = clientIndexStorage.get(clientId);
        if (currentIndex == null) {
            throw new FibonacciRuntimeException("Client does not exist");
        }
        return IntStream.rangeClosed(0, currentIndex).mapToObj(this::computeFibonacciNumber).toList();
    }

    private long computeFibonacciNumber(int index) {
        return fiboIndexValueStorage.computeIfAbsent(
                index, i -> fiboIndexValueStorage.get(i - 1) + fiboIndexValueStorage.get(i - 2)
        );
    }
}