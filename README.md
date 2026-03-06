try {
    Thread.sleep(1000); 
} catch (InterruptedException e) {
    // 当线程在睡眠时被中断，会抛出此异常
    e.printStackTrace();
}
