package com.rxjava.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;

public class PhaserTest {


    public static void main(String[] args) {
        String name = "明刚红丽黑白";
        Phaser phaser = new Phaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }

    private static class TourismRunnable implements Runnable{
        Phaser phaser;
        Random random;
        public TourismRunnable(Phaser phaser) {
            this.phaser = phaser;
            this.random = new Random();
        }

        @Override
        public void run() {
            tourism();
        }

        /**
         * 旅游过程
         */
        private void tourism() {
            goToStartingPoint();
            goToHotel();
            goToTourismPoint1();
            final Random random = new Random();
            if (random.nextInt(5) == 2){
                toPointEnd("1");
                return;
            }
            goToTourismPoint2();
            if (random.nextInt(5) == 3){
                toPointEnd("2");
                return;
            }
            goToTourismPoint3();
            if (random.nextInt(5) == 4){
                toPointEnd("3");
                return;
            }
            goToEndPoint();
        }

        /**
         * 装备返程
         */
        private void goToEndPoint() {
            goToPoint("飞机场,准备登机回家");
        }

        /**
         * 到达旅游点3
         */
        private void goToTourismPoint3() {
            goToPoint("旅游点3");
        }

        /**
         * 到达旅游点2
         */
        private void goToTourismPoint2() {
            goToPoint("旅游点2");
        }

        /**
         * 到达旅游点1
         */
        private void goToTourismPoint1() {
            goToPoint("旅游点1");
        }

        /**
         * 入住酒店
         */
        private void goToHotel() {
            goToPoint("酒店");
        }

        /**
         * 出发点集合
         */
        private void goToStartingPoint() {
            goToPoint("出发点");
        }

        private int getRandomTime(){
            int time = this.random.nextInt(400) + 100;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return time;
        }

        private void goToPoint(String point){
            try {
                String name = Thread.currentThread().getName();
                System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
                // 到达并等待
                phaser.arriveAndAwaitAdvance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void toPointEnd(String point){
            try {
                String name = Thread.currentThread().getName();
                System.out.println(name + " 到了 " + getRandomTime() + " 停在了" + point);
                // 到达并等待
                phaser.arriveAndDeregister();
                System.out.println("结束！"+name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
