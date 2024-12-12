package com.huyvu.lightmessage;

import io.micrometer.observation.Observation;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReorderingTest {
    private static int x = 0;
    private static boolean done = false;

    public static void main(String[] args) throws InterruptedException {
        var executor = Executors.newFixedThreadPool(3);
        // Thread 1: Ghi x và done
        Callable<Void> writer = () -> {
            x = 1;         // Ghi x
            Thread.sleep(2000);
            done = true;   // Ghi done
            return null;
        };

        // Thread 2: Đọc done và x
        Callable<Void> reader = () -> {
            if (done) {    // Nếu done là true
                System.out.println("x = " + x); // Có thể in ra x = 0
            }
            return null;
        };

        executor.invokeAll(List.of(writer, reader));
        executor.shutdown();
    }
}
