package io.rapha.spring.reactive.security;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
 * @author Kurenai
 * @since 2019-05-24 22:01
 */


public class TestWebFlux {

    @Before
    public void debug() {
//        Hooks.onOperatorDebug();
    }

    @Test
    public void test() {
        Flux.just("Hello", "World").subscribe(System.out::println);
        Flux.fromArray(new Integer[] {1, 2, 3}).subscribe(System.out::println);
        Flux.empty().subscribe(System.out::println);
        Flux.range(1, 10).subscribe(System.out::println);
        Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
    }
    @Test
    public void testFluxGenerate() {
        Flux.generate(sink -> {
            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);


        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);
    }
    @Test
    public void testFluxCreate() {
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    @Test
    public void testMono() {
        Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
    }

    @Test
    public void testBuffer() {
        Flux.range(1, 100).buffer(20).subscribe(System.out::println);
        Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).buffer(Duration.of(1001, ChronoUnit.MILLIS)).take(2).toStream().forEach(System.out::println);
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
    }

    @Test
    public void testFilter() {
        Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);
    }

    @Test
    public void testWindow() {
        Flux.range(1, 100).window(20).subscribe(System.out::println);
        Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).window(Duration.of(1001, ChronoUnit.MILLIS)).take(2).toStream().forEach(System.out::println);
    }

    @Test
    public void testZipWith() {
        Flux.just("a", "b")
                .zipWith(Flux.just("c", "d"))
                .subscribe(System.out::println);
        Flux.just("a", "b")
                .zipWith(Flux.just("c", "d"), (s1, s2) -> String.format("%s-%s", s1, s2))
                .subscribe(System.out::println);
    }

    @Test
    public void testTake() {
        Flux.range(1, 1000).take(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);
    }

    @Test
    public void testReduce() {
        Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
        Flux.range(1, 100).reduceWith(() -> 100, (x, y) -> x + y).subscribe(System.out::println);
    }

    @Test
    public void testMerge() {
        Flux.merge(Flux.interval(Duration.of(0, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5))
                .toStream()
                .forEach(System.out::println);
        Flux.mergeSequential(Flux.interval(Duration.of(0, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5))
                .toStream()
                .forEach(System.out::println);
    }

    @Test
    public void testFlatMap() {
        Flux.just(5, 10)
                .flatMap(x -> Flux.interval(Duration.of(x * 10, ChronoUnit.MILLIS),
                        Duration.of(100, ChronoUnit.MILLIS))
                        .take(x))
                .toStream()
                .forEach(System.out::println);
        Flux.just(5, 10)
                .flatMapSequential(x -> Flux.interval(Duration.of(x * 10, ChronoUnit.MILLIS),
                        Duration.of(100, ChronoUnit.MILLIS))
                        .take(x))
                .toStream()
                .forEach(System.out::println);
    }

    @Test
    public void testConcatMap() {
        Flux.just(5, 10)
                .concatMap(x -> Flux.interval(Duration.of(x * 10, ChronoUnit.MILLIS),
                        Duration.of(100, ChronoUnit.MILLIS))
                        .take(x))
                .toStream()
                .forEach(System.out::println);
    }

    @Test
    public void testCombineLatest() {
        Flux.combineLatest(
                Arrays::toString,
                Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).take(5),
                Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5)
        ).toStream().forEach(System.out::println);
    }

    @Test
    public void testReturnMessage() {
        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .subscribe(System.out::println, System.err::println);

        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .onErrorReturn(0)
                .subscribe(System.out::println);

//        Flux.just(1, 2)
//                .concatWith(Mono.error(new IllegalStateException()).then(Mono.empty()))
//                .switchIfEmpty(Mono.just(0))
//                .subscribe(System.out::println);

        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(0);
                    } else if (e instanceof IllegalArgumentException) {
                        return Mono.just(-1);
                    }
                    return Mono.empty();
                })
                .subscribe(System.out::println);

        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .retry(1)
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void testSchedulers() {
        Flux.create(sink -> {
            sink.next(Thread.currentThread().getName());
            sink.complete();
        })
        .publishOn(Schedulers.single())
        .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
        .publishOn(Schedulers.elastic())
        .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
        .subscribeOn(Schedulers.parallel())
        .toStream()
        .forEach(System.out::println);
    }

    @Test
    public void testStepVerifier() {
        StepVerifier.create(Flux.just("a", "b"))
                .expectNext("a")
                .expectNext("b")
                .verifyComplete();

        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(4), Duration.ofDays(1)).take(2))
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(4))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(1))
                .expectNext(1L)
                .verifyComplete();

        final TestPublisher<String> testPublisher = TestPublisher.create();
        testPublisher.next("a");
        testPublisher.next("b");
        testPublisher.complete();

        StepVerifier.create(testPublisher)
                .expectNext("a")
                .expectNext("b")
                .expectComplete();
    }

    @Test
    public void testDebug() {
        Flux.just(0, 1).map(x -> 1 / x).checkpoint("test").onErrorReturn(0).subscribe(System.out::println);

        Flux.range(1, 2).log("Range").subscribe(System.out::println);
    }

    @Test
    public void testHotSequence() throws InterruptedException {
        final Flux<Long> source = Flux.interval(Duration.ofMillis(1000))
                .take(10)
                .publish()
                .autoConnect();
        source.subscribe();
        Thread.sleep(5000);
        source
                .toStream()
                .forEach(System.out::println);
    }
}
