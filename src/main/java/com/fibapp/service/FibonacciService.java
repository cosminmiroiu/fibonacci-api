package com.fibapp.service;

import com.fibapp.exception.FibonacciRuntimeException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FibonacciService {

    private final Map<String, List<Long>> clientSequences = new ConcurrentHashMap<>();

    public long next(String clientId) {

        final List<Long> sequence = clientSequences.computeIfAbsent(clientId, id -> new ArrayList<>());

        synchronized (sequence) {

            if (sequence.size() < 2) {
                sequence.add(1L);
                return 1L;
            }

            int lastIndex = sequence.size() - 1;
            long next = sequence.get(lastIndex) + sequence.get(lastIndex - 1);

            sequence.add(next);
            return next;
        }
    }

    public String back(String clientId) {

        final List<Long> sequence = clientSequences.get(clientId);

        if (sequence == null) {
            throw new FibonacciRuntimeException("Client does not exist");
        }

        synchronized (sequence) {

            if (sequence.isEmpty()) {
                throw new FibonacciRuntimeException("Back limit reached");
            }

            sequence.removeLast();

            return "OK";
        }
    }

    public List<Long> list(String clientId) {

        final List<Long> sequence = clientSequences.get(clientId);

        if (sequence == null) {
            throw new FibonacciRuntimeException("Client does not exist");
        }

        synchronized (sequence) {
            return new ArrayList<>(sequence);
        }
    }
}