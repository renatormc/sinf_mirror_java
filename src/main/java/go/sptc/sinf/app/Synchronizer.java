package go.sptc.sinf.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class Synchronizer implements Runnable {
    public boolean autoFind;
    public List<Path> sources;
    public Path source;
    public Path dest;
    public boolean verbose;
    public long nWorkers;
    public long threshold;
    public long thresholdChunk;
    public long bufferSize;
    public boolean purge;
    public int retries;
    public Duration wait;
    private List<Thread> threads;
    private SynchronousQueue<JobConfig> jobs;
    private SynchronousQueue<ResultData> results;

    public Synchronizer() {
        this.sources = new ArrayList<Path>();
        this.jobs = new SynchronousQueue<>();
        this.results = new SynchronousQueue<>();
    }

    public void run() {
        SynchronousQueue<Boolean> acknowledgeDone = new SynchronousQueue<>();

        // Cria pasta destino se ela não existir
        if (!Files.exists(dest)) {
            try {
                Files.createDirectories(dest);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        for (Path source : sources) {
            this.source = source;
            threads = new ArrayList<Thread>();
            for (int i = 1; i <= this.nWorkers; i++) {
                Thread thread = new Thread(new Worker(jobs, results, i));
                threads.add(thread);
                thread.start();
            }

            update(source);

            // Aguarda todas as threads terminarem
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        ResultData data = new ResultData();
        data.action = "finish";
        results.add(data);
    }

    private void update(Path path) {
        Path relPath;
        Path destAbsolutePath;

        if (purge) {
            relPath = source.relativize(path);
            destAbsolutePath = dest.resolve(relPath);
            purgeItems(destAbsolutePath);
        }

        try {
            Files.list(path).forEach(item -> {
                if (Files.isDirectory(item)) {
                   relPath = source.relativize(item);
                } else {
                  
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(Path source, Path dest, ResultData resultaData) {

    }

    private void purgeItems(Path path) {

    }

    private void copyOrReplace(Path relPath) {

    }

}
