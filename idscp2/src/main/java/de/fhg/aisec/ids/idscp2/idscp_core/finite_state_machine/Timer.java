package de.fhg.aisec.ids.idscp2.idscp_core.finite_state_machine;

import java.util.concurrent.locks.ReentrantLock;

public class Timer {

    private TimerThread thread = null;
    private final ReentrantLock fsmIsBusy;
    private final ReentrantLock mutex = new ReentrantLock(true);
    private final Runnable timeoutHandler;

    Timer(ReentrantLock fsmIsBusy, Runnable timeoutHandler){
        this.fsmIsBusy = fsmIsBusy;
        this.timeoutHandler = timeoutHandler;
    }

    void resetTimeout(long delay){
        cancelTimeout();
        start(delay);
    }

    public void start(long delay){
        mutex.lock();
        thread = new TimerThread(delay, timeoutHandler, fsmIsBusy);
        thread.start();
        mutex.unlock();
    }

    void cancelTimeout(){
        mutex.lock();
        if (thread != null){
            thread.safeStop();
            thread = null;
        }
        mutex.unlock();
    }
}
