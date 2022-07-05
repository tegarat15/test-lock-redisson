package com.tamvan.demolockprocess.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;;

@RestController
@RequestMapping("/normal-lock-test")
@RequiredArgsConstructor
@Slf4j
public class NormalLockController {

    @GetMapping("/lock/execute")
    public HttpEntity<String> testLock() {
        String message = "Tegar Tamvan Normal Lock Biasa.";
        HttpStatus httpStatus = HttpStatus.OK;
        Instant start = Instant.now();
        Lock queueLock = new ReentrantLock();
        try {
            queueLock.lock();
            Thread.sleep(10000);
        } catch (Exception e) {
            log.error("Error : " + e);
            message = "Tegar Tamvan Normal Lock Biasa. Error.";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } finally {
            queueLock.unlock();
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        message = message + " Duration : " + timeElapsed + " ms";

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/try-lock/execute")
    public HttpEntity<String> testTryLock() {
        String message = "Tegar Tamvan Normal Try Lock Biasa.";
        HttpStatus httpStatus = HttpStatus.OK;
        Instant start = Instant.now();
        Lock queueLock = new ReentrantLock();
        try {
            boolean lockedSuccess = queueLock.tryLock(20000, TimeUnit.MILLISECONDS);
            if(!lockedSuccess){
                message = "Tegar Tamvan Try Lock. Locked.";
                httpStatus = HttpStatus.CONFLICT;
            }else{
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            log.error("Error : " + e);
            message = "Tegar Tamvan Normal Try Lock Biasa. Error.";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } finally {
            queueLock.unlock();
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        message = message + " Duration : " + timeElapsed + " ms";

        return new ResponseEntity<>(message, httpStatus);
    }
}
