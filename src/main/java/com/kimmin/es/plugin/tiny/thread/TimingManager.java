package com.kimmin.es.plugin.tiny.thread;

import com.kimmin.es.plugin.tiny.exception.NoSuchTaskException;
import com.kimmin.es.plugin.tiny.service.RegisterService;
import com.kimmin.es.plugin.tiny.task.CycleTimingTask;
import jdk.nashorn.internal.runtime.Timing;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by kimmin on 3/8/16.
 */



public class TimingManager {
    /** Singleton **/
    private TimingManager(){}
    private static class Singleton{
        private static TimingManager instance = new TimingManager();
    }
    public static TimingManager getInstance(){ return Singleton.instance; }

    /** Constant Variable **/
    public final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(CORE_POOL_SIZE);

    public void startTask(String taskName) throws NoSuchTaskException{
        CycleTimingTask task = RegisterService.getInstance().getTaskByName(taskName);
        Boolean enabled = RegisterService.getInstance().getTaskStatusByName(taskName);
        while(enabled != null && enabled == true) {
            ScheduledFuture future = service.schedule(task, task.milliDelay, TimeUnit.MILLISECONDS);
            try{
                future.get();
            }catch (ExecutionException ee){
                ee.printStackTrace();
                future.cancel(true);
            }catch (InterruptedException ie){
                ie.printStackTrace();
                future.cancel(true);
            }
            enabled = RegisterService.getInstance().getTaskStatusByName(taskName);
        }
    }

    public void shutdown(){
        /** Disable all tasks **/
        Iterator<String> iter = RegisterService.getInstance().statusMap().keySet().iterator();
        while(iter.hasNext()){
            RegisterService.getInstance().disableTask(iter.next());
        }
        /** Shutdown executors pool **/
        service.shutdown();
    }

    public void start(){
        service = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
    }

    public boolean isStarted(){
        return !service.isShutdown();
    }

}
