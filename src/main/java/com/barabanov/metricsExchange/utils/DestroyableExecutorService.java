package com.barabanov.metricsExchange.utils;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;


/**
 * Обёртка нужна только для указания метода для уничтожения ExecutorService.
 * Если использовать destroyMethod параметр внутри @Bean, то нельзя указать наиболее корреткную логику реализованную тут
 * для уничтожения объекта
 */
@RequiredArgsConstructor
public class DestroyableExecutorService implements ExecutorService {
    private final ExecutorService executorService;


    @PreDestroy
    public void reliableShutDown() {
        this.executorService.shutdown();
        try {
            if (!this.executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
        }
    }


    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.executorService.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.executorService.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.executorService.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.executorService.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.executorService.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.executorService.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.executorService.execute(command);
    }
}
