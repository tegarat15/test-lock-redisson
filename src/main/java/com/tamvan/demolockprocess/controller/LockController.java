package com.tamvan.demolockprocess.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/lock-test")
@RequiredArgsConstructor
@Slf4j
public class LockController {

    private final RedissonClient redissonClient;
    String lockKey = "tamvan";

    @GetMapping("/lock/execute")
    public HttpEntity<String> testLock() {
        String message = "Tegar Tamvan Lock Biasa.";
        HttpStatus httpStatus = HttpStatus.OK;
        RLock lock = redissonClient.getLock(lockKey);
        Instant start = Instant.now();
        try {
            lock.lock(500, TimeUnit.MILLISECONDS);
            Thread.sleep(100);
        } catch (Exception e) {
            log.error("Error : " + e);
            message = "Tegar Tamvan Lock Biasa. Error.";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        message = message + " Duration : " + timeElapsed + " ms";

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/try-lock/execute")
    public HttpEntity<String> testTryLock(){
        String message = "Tegar Tamvan Try Lock.";
        HttpStatus httpStatus = HttpStatus.OK;
        RLock lock = redissonClient.getLock(lockKey);
        Instant start = Instant.now();
        try {
            boolean locked = lock.tryLock(0, 500, TimeUnit.MILLISECONDS);
            if(locked){
                message = "Tegar Tamvan Try Lock. Locked.";
                httpStatus = HttpStatus.CONFLICT;
            }else{
                Thread.sleep(100);
            }
        } catch (Exception e) {
            log.error("Error : " + e);
            message = "Tegar Tamvan Try Lock. Error.";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        message = message + " Duration : " + timeElapsed + " ms";
        return new ResponseEntity<>(message, httpStatus);
    }
}
