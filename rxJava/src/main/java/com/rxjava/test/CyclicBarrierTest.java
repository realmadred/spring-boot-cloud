package com.rxjava.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(CyclicBarrierTest.class);

    public static void main(String[] args) {
        final CyclicBarrier barrier = new CyclicBarrier(10, () -> LOGGER.info("都到齐了！"));
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(new BarrierThread(barrier,"BarrierThread"+i)).start();
        }
    }

    private static class BarrierThread implements Runnable{
        private CyclicBarrier barrier;
        private String name;

        public BarrierThread(CyclicBarrier barrier,String name) {
            this.barrier = barrier;
            this.name = name;
        }

        @Override
        public void run() {
            LOGGER.info("thread name:{},Parties:{},NumberWaiting:{}",name,barrier.getParties()
                    ,barrier.getNumberWaiting());
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
