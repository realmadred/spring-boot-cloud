package com.rxjava.test;

import com.rxjava.service.UserService;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


class Test1 {

    private static Observer<String> subscribe;

    private static Observer<Long> subscribeLong;

    private static Observer<Integer> subscribeInteger;

    private static  Subscriber<Integer> subscriber;

    @BeforeAll
    static void init(){
        subscribe = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                System.out.println("isDisposed:"+disposable.isDisposed());
                System.out.println("onSubscribe:"+disposable);
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext:"+s);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("success");
            }
        };

        subscribeLong = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                System.out.println("isDisposed:"+disposable.isDisposed());
                System.out.println("onSubscribe:"+disposable);
            }

            @Override
            public void onNext(Long s) {
                System.out.println("onNextLong:"+s);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("success");
            }
        };

        subscribeInteger = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                System.out.println("isDisposed:"+disposable.isDisposed());
                System.out.println("onSubscribe:"+disposable);
            }

            @Override
            public void onNext(Integer s) {
                System.out.println("onNextInteger:"+s);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("success");
            }
        };

        subscriber = new Subscriber<Integer>() {
            Subscription chche = null;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                chche = s;
                s.request(1);
            }
            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext: " + integer);
                // 继续拉取
                chche.request(1);
            }
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    @Test
    void test1(){
        Observable<String> sender = Observable.create(observableEmitter -> {
            observableEmitter.onNext("hello world");
            observableEmitter.onComplete();
        });
        sender.subscribe(subscribe);
    }

    @Test
    void testJust() {
        //使用just( )，将为你创建一个Observable并自动为你调用onNext( )发射数据
        Observable<String> just = Observable.just("hello just one");
        just.subscribe(subscribe);
    }

    @Test
    void repeat() {
        //创建一个Observable，该Observable的事件可以重复调用
        Observable<String> just = Observable.just("hello just one").repeat(3);
        just.subscribe(subscribe);
    }

    @Test
    void fromIterable() {
        //使用fromIterable()，遍历集合，发送每个item。相当于多次回调onNext()方法，每次传入一个item
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("hello:"+i);
        }
        Observable<String> fromIterable = Observable.fromIterable(list);
        fromIterable.subscribe(subscribe);
    }

    @Test
    void interval() throws InterruptedException {
        //一个按固定时间间隔发射整数序列的Observable，可用作定时器
        final Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);
        interval.subscribe(subscribeLong);
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    void range() {
        //第一个参数为起始值，第二个为发送的个数，如果为0则不发送，负数则抛异常
        final Observable<Integer> interval = Observable.range(1, 10);
        interval.subscribe(subscribeInteger);
    }

    @Test
    void timer() throws InterruptedException {
        //它在一个给定的延迟后发射一个特殊的值，即表示延迟2秒后
        final Observable<Long> interval = Observable.timer(2, TimeUnit.SECONDS);
        interval.subscribe(subscribeLong);
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void consumer() throws InterruptedException {
        Disposable disposable = Observable.just("hello just one")
                .subscribe(System.out::print);
    }

    @Test
    void map() throws InterruptedException {
        //它在一个给定的延迟后发射一个特殊的值，即表示延迟2秒后
        Observable.timer(2, TimeUnit.SECONDS)
                .map(String::valueOf)
                .subscribe(subscribe);
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void flatMap(){

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("hello:"+i);
        }
        final int[] count = {0};
        Observable.just(list)
                .flatMap((Function<List<String>, ObservableSource<String>>) Observable::fromIterable)
                .filter(s -> count[0]++ >= 5)
                .subscribe(subscribe);
    }

    @Test
    void take(){
        //输出最多指定数量的结果
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("hello:"+i);
        }
        Observable.just(list)
                .flatMap((Function<List<String>, ObservableSource<String>>) Observable::fromIterable)
                .take(5)
                .subscribe(subscribe);
    }

    @Test
    void doOnNext(){
        //每次输出一个元素之前做一些额外的事情
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("hello:"+i);
        }
        Observable.just(list)
                .flatMap((Function<List<String>, ObservableSource<String>>) Observable::fromIterable)
                .take(5)
                .doOnNext(s -> System.out.println("准备一些东西："+s))
                .subscribe(subscribe);
    }

    @Test
    void flowableError() throws InterruptedException {
        // 支持背压
        Flowable<Integer> flowable = Flowable.create(emitter -> {
            //缓存池的默认大小为128，即只能缓存128个事件
            for (int i = 0; i < 129; i++) {
                System.out.println("emit " + i);
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.ERROR); //增加了一个参数

        flowable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    void flowableBuffer() throws InterruptedException {
        // 支持背压
        Flowable<Integer> flowable = Flowable.create(emitter -> {
            //缓存池的默认大小为128，即只能缓存128个事件
            for (int i = 0; i < 129; i++) {
                System.out.println("emit " + i);
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER); //增加了一个参数缓存

        flowable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    void flowableCall(){
        Observable.fromCallable(UserService::getUserIds)
        .flatMap(Observable::fromIterable)
        .flatMap((Function<String, ObservableSource<String>>) s -> Observable.just(UserService.getUser(s)))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .subscribe(subscribe);
    }

}
