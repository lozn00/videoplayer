package cn.qssq666.videoplayer.video_player_manager;

/**
 * Created by qssq on 2018/4/20 qssq666@foxmail.com
 */
public class WaitTest {

    @org.junit.Test
    public void waitAndNotify() {


        final Object lock = new Object();

        new Thread(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                while (count < 5) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("current :" + count);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;

                }

                System.out.println("我是子线程:" + Thread.currentThread().getName());
                synchronized (lock) {

                System.out.println("调用notify start");
                    lock.notify();//必须同步锁包裹才能进行调用 Exception in thread "Thread-0" java.lang.IllegalMonitorStateException
                System.out.println("调用notify end");
                }
                System.out.println("调用notify end 非锁中..gradlew ");

            }
        }).start();
        System.out.println("即将进入线程等待");
        synchronized (lock) {

            try {
            System.out.println("进入等待同步锁start");
                lock.wait();
            System.out.println("退出等待同步锁exit");
//                lock.wait(1000555);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
