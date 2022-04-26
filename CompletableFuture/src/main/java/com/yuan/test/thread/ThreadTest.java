package com.yuan.test.thread;


import java.util.concurrent.*;

/**
 * 继承thread
 * 实现runnable
 * 实现callable+futureTask(可拿到返回结果，处理异常)
 * 线程池,给线程池提交任务即可（最好用这个）
 */
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

//=====================================================CompletableFuture测试==========================================================

        //串行化
        CompletableFutures completableFutures = new CompletableFutures();
        completableFutures.getProByParallel();
//        //多任务组合或并行化
//        completableFuture.getProBySerial();
// =============================================================线程测试===============================================================
//        System.out.println("===========main开始===========");
//        Thread01 thread01 = new Thread01();
//        thread01.start();
//        System.out.println("===========main结束===========");
//        Runnable Runnable01=new Runnable01();
//        executorService.submit(Runnable01);
    }

    public static class CompletableFutures {
        /**
         * 模拟商品信息串行化获取，且在自定义的线程池中执行
         * 先查询商品id,再根据该商品id查询库存
         * thenRunAsync获取不到上一步的结果
         * thenAcceptAsync可获取上一步的结果
         */
        public void getProBySerial() {
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            System.out.println("===========main开始===========");
            final java.util.concurrent.CompletableFuture<Void> Future = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                System.out.println("当前线程：" + Thread.currentThread().getId());
                int i = 1283;
                System.out.println("查商品id:" + i);
                return i;
            }, executorService).thenAcceptAsync((i) -> {
                int j = i - 1000;
                System.out.println("根据商品id" + i + "查库存,库存为：" + j);
            }, executorService);
            System.out.println("===========main结束===========");
        }

        /**
         * 模拟异步获取商品信息，且在自定义的线程池中执行
         * allOf.get()可等全部执行完后,再一起获取全部信息
         * anyOf.get()在任何流程执行完后,都可返回信息
         */
        public void getProByParallel() throws ExecutionException, InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            System.out.println("===========main开始===========");
            //模拟商品信息异步获取流程
            java.util.concurrent.CompletableFuture<String> futureImg = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                System.out.println("查询商品图片信息");
                return "查询商品图片信息";
            }, executorService);

            java.util.concurrent.CompletableFuture<Void> futureAttr = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(3000);
                    System.out.println("查询商品属性");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }, executorService);

            java.util.concurrent.CompletableFuture<Void> futureDesc = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                System.out.println("查询商品介绍");
                return null;
            }, executorService);

            java.util.concurrent.CompletableFuture<Object> anyOf = java.util.concurrent.CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
//        CompletableFuture<Void> allOf=CompletableFuture.allOf(futureImg,futureAttr,futureDesc);
            System.out.println("最先执行完的是:" + anyOf.get());
            //        allOf.get();
            System.out.println("图片：" + futureImg.get() + ",属性：" + futureAttr.get() + ",描述：" + futureDesc.get());
            System.out.println("===========main结束===========");

        }
    }


    //线程池，10个线程
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }
    }

    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call()  {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
            return i;
        }
    }
}
