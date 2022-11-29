package com.learnreactiveprogramming.service;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {

    public Mono<String> namesMono_map_filter(int stringLength) {

        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log()
                ;
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log()
                ;
    }


    /**
     * 모노 파이프라인에서 플럭스를 변환할때 flatMapMany를 사용
     * @param stringLength
     * @return
     */
    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log()
                ;
    }

    private Mono<List<String>> splitStringMono(String name) {
        var charArray = name.split("");
        List<String> charList = List.of(charArray);
        return Mono.just(charList);
    }


    public Flux<String> namesFlux_flatmap(int stringLength) {

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s))
                .log()
                ;
    }

    /**
     * flatmap
     * 1대N 변환
     * 비동기변환에 사용할 수 있다.
     * @param stringLength
     * @return
     */
    public Flux<String> namesFlux_flatmap_aync(int stringLength) {

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString_withDelay(s)) //각각의 delay시간이 다르므로 비동기적으로 순서 없이 실행된다.
                .log()
                ;
    }


    /**
     * pipeline의 순서가 중요한 경우 concatmap을 사용한다.
     * @param stringLength
     * @return
     */
    public Flux<String> namesFlux_concatmap(int stringLength) {

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(s -> splitString_withDelay(s)) //각각의 delay시간이 다르므로 비동기적으로 순서 없이 실행된다.
                .log()
                ;
    }

    private Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    private Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");

        int delay = new Random().nextInt(1000);

        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }
}
