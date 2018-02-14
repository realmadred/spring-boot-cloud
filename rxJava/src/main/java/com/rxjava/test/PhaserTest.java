package com.rxjava.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

public class PhaserTest {

    private static void test1(){
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

    private static void test2(){
        String name = "明刚红丽黑白";
        Phaser phaser = new SubPhaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()){
            tourismThread.add(new Thread(new TourismRunnable1(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread){
            thread.start();
        }
    }


    public static void main(String[] args) {
        test2();
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

    private static class TourismRunnable1 implements Runnable{
        Phaser phaser;
        Random random;
        /**
         * 每个线程保存一个朋友计数器,比如小红第一次遇到一个朋友,则取名`小红的朋友0号`,
         * 然后旅游到其他景点的时候,如果小红又遇到一个朋友,这取名为`小红的朋友1号`
         */
        AtomicInteger friendCount = new AtomicInteger();
        public TourismRunnable1(Phaser phaser) {
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
            switch (phaser.getPhase()){
                case 0:if(!goToStartingPoint()) break;
                case 1:if(!goToHotel()) break;
                case 2:if(!goToTourismPoint1()) break;
                case 3:if(!goToTourismPoint2()) break;
                case 4:if(!goToTourismPoint3()) break;
                case 5:if(!goToEndPoint()) break;
            }
        }

        /**
         * 准备返程
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToEndPoint() {
            return goToPoint("飞机场,准备登机回家");
        }

        /**
         * 到达旅游点3
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToTourismPoint3() {
            return goToPoint("旅游点3");
        }

        /**
         * 到达旅游点2
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToTourismPoint2() {
            return goToPoint("旅游点2");
        }

        /**
         * 到达旅游点1
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToTourismPoint1() {
            return goToPoint("旅游点1");
        }

        /**
         * 入住酒店
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToHotel() {
            return goToPoint("酒店");
        }

        /**
         * 出发点集合
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToStartingPoint() {
            return goToPoint("出发点");
        }

        private int getRandomTime() throws InterruptedException {
            int time = random.nextInt(400) + 100;
            Thread.sleep(time);
            return time;
        }

        /**
         * @param point 集合点
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean goToPoint(String point){
            try {
                if(!randomEvent()){
                    phaser.arriveAndDeregister();
                    System.out.println(point+" 离开！");
                    return false;
                }
                String name = Thread.currentThread().getName();
                System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
                phaser.arriveAndAwaitAdvance();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * 随机事件
         * @return 返回true,说明还要继续旅游,否则就临时退出了
         */
        private boolean randomEvent() {
            int r = random.nextInt(100);
            String name = Thread.currentThread().getName();
            if (r < 10){
                int friendNum =  1;
                System.out.println(name + ":在这里竟然遇到了"+friendNum+"个朋友,他们说要一起去旅游...");
                phaser.bulkRegister(friendNum);
                for (int i = 0; i < friendNum; i++){
                    new Thread(new TourismRunnable1(phaser), name + "的朋友" + friendCount.getAndAdd(1) + "号").start();
                }
            }else if(r > 90){
                System.out.println(name + ":突然有事要离开一下,不和他们继续旅游了....");
                return false;
            }
            return true;
        }
    }

    public static class SubPhaser extends Phaser{
        public SubPhaser(int parties) {
            super(parties);
        }

        @Override
        protected boolean onAdvance(int phase, int registeredParties) {

            System.out.println(Thread.currentThread().getName() + ":全部"+getArrivedParties()+"个人都到齐了,现在是第"+(phase + 1)
                    +"次集合准备去下一个地方..................\n");
            return super.onAdvance(phase, registeredParties);
        }
    }
}
