package org.ly.demo.increment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IncrementTask implements Runnable {
    private final int limit;
    private final Lock simpleFileLock = new ReentrantLock();

    public IncrementTask(int limit) {
        this.limit = limit;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread());
                Path path = Paths.get("out.txt");
                Integer counter = 0;
                simpleFileLock.lock();
                try {
                    try {
                        counter = new Integer(new String(Files.readAllBytes(path)));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (counter >= limit) {
                        break;
                    }
                    counter = counter + 1;

                    Files.write(path, counter.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                } finally {
                    simpleFileLock.unlock();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
