package go.sptc.sinf.app;

import java.util.concurrent.SynchronousQueue;

public class Worker implements Runnable {

    public Synchronizer synchronizer;
    public int id;
    private SynchronousQueue<JobConfig> jobs;
    private SynchronousQueue<ResultData> results;


    public void run(){

    }

    public Worker(SynchronousQueue<JobConfig> jobs, SynchronousQueue<ResultData> results, int id){
        this.jobs = jobs;
        this.results = results;
        this.id = id;
    }

}