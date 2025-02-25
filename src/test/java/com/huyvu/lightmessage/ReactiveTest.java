package com.huyvu.lightmessage;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

public class ReactiveTest {
    @Test
    public void whenRequestingChunks10_thenMessagesAreReceived() {
        Flux request = Flux.range(1, 50);

        request.subscribe(
                System.out::println,
                (Consumer<Throwable>) Throwable::printStackTrace,
                () -> System.out.println("All 50 items have been successfully processed!!!"),
                (Consumer<? super Subscription>) subscription -> {
                    for (int i = 0; i < 5; i++) {
                        System.out.println("Requesting the next 10 elements!!!");
                        subscription.request(10);
                    }
                }
        );

        StepVerifier.create(request)
                .expectSubscription()
                .thenRequest(10)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .thenRequest(10)
                .expectNext(11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
                .thenRequest(10)
                .expectNext(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
                .thenRequest(10)
                .expectNext(31, 32, 33, 34, 35, 36, 37, 38, 39, 40)
                .thenRequest(10)
                .expectNext(41, 42, 43, 44, 45, 46, 47, 48, 49, 50)
                .verifyComplete();
    }
}