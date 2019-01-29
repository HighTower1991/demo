package org.ly.demo.increment;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class IncrementTaskTest {

    /**
     * Исходя из условия, что оба потока должны корректно завершить свою работу, следует отсутствие дополнительных
     * технических и функциональных ограничений. Достаточно ограничить одновременную работу с файлом одним потоком
     * посредством блокировки.
     */
    @Test
    public void runTasksTest() throws Exception{
        Path path = Paths.get("target","out.txt");
        Files.write(path, "0".getBytes(), StandardOpenOption.CREATE);
        Integer limit = 50;
        IncrementTask incrementTask = new IncrementTask(limit);
        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<?> submitFirst = es.submit(incrementTask);
        Future<?> submitSecond = es.submit(incrementTask);
        submitFirst.get();
        submitSecond.get();
        String x = new String(Files.readAllBytes(path));
        System.out.println(x);
        assertEquals(limit.toString(), x);
    }
}