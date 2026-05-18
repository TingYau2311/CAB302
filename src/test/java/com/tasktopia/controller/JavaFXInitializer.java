package com.tasktopia.controller;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

/**
 * Starts the JavaFX platform exactly once for unit tests.
 *
 * JavaFX controls (TextField, Label, etc.) require the toolkit to be
 * initialised before they can be instantiated, even outside the FX thread.
 * Call JavaFXInitializer.init() in a @BeforeAll method in any test class
 * that creates JavaFX nodes.
 */
public class JavaFXInitializer {

    private static boolean started = false;

    public static synchronized void init() {
        if (started) return;

        CountDownLatch latch = new CountDownLatch(1);

        try {
            // Platform.startup() boots the toolkit without opening a window.
            // It calls our runnable once the toolkit is ready.
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            // Already running (e.g. another test class started it first)
            latch.countDown();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("JavaFX toolkit failed to start", e);
        }

        started = true;
    }
}