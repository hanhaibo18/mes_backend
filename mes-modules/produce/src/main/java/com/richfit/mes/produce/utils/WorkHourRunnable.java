package com.richfit.mes.produce.utils;

/**
 * @author Q
 */
public class WorkHourRunnable implements Runnable {

    @Override
    public void run() {
        System.out.println("当前运行的线程名为： " + Thread.currentThread().getName());
    }

    public static void main(String[] args) throws Exception {
        WorkHourRunnable runnable = new WorkHourRunnable();
        for (int i = 0; i < 10; i++) {
            new Thread(runnable, "MyThreadOne").start();
            new Thread(runnable, "MyThreadTwo").start();
        }
        System.out.println("-------------");
        System.out.println(123);
    }
}