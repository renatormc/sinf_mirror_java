package go.sptc.sinf.app;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.apache.commons.io.FileUtils;

public class Progress implements Runnable {
    public long totalSize;
    public long totalNumber;
    public long currentSize;
    public long currentNumber;
    public long totalToCopySize;
    public long totalToCopyNumber;
    public long sizeAnalyzed;
    public long numberAnalyzed;
    public long sizeCopied;
    public long numberCopied;
    public LocalDateTime startTime;
    public LocalDateTime eta;
    public LocalDateTime eta2;
    public long elapsed;
    public long avgSpeed;
    // public long speed;
    public long remainingTime;
    public long remainingTime2;
    public double progressSize;
    public double progressNumber;
    public long newFiles;
    public long updatedFiles;
    public long deletedItems;
    public long equalFiles;
    private Synchronizer synchronizer;

    public Progress() {
        newFiles = 0;
        updatedFiles = 0;
        deletedItems = 0;
        totalSize = 0;
        currentNumber = 0;
        currentSize = 0;
        sizeCopied = 0;
        numberCopied = 0;
    }

    public void setSynchronizer(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    public void calculateProgress(String action) {
        long speed = 0;
        double estimatedSpeed = 0;
        String remainingTimeStr2 = "-";
        String remainingTimeStr = "-";
        String etaStr = "-";
        String etaStr2 = "-";
        progressSize = (double) sizeCopied / (double) totalToCopyNumber;
        progressNumber = (double) numberCopied / (double) totalToCopyNumber;
        elapsed = startTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);
        avgSpeed = sizeCopied / elapsed;
        if (progressSize != 0) {
            double alfa = (double) progressNumber / (double) progressSize;
            alfa = 0.6 * alfa + 0.4;
            estimatedSpeed = alfa * avgSpeed;
            remainingTime = (long) (((double) (totalToCopySize - sizeCopied)) / avgSpeed);
            remainingTime2 = (long) (((double) (totalToCopySize - sizeCopied)) / estimatedSpeed);
            eta = LocalDateTime.now().plus(remainingTime, ChronoUnit.SECONDS);
            eta = LocalDateTime.now().plus(remainingTime2, ChronoUnit.SECONDS);
            speed = avgSpeed * 60;
            remainingTimeStr = Helpers.fmtDuration(remainingTime);
            remainingTimeStr2 = Helpers.fmtDuration(remainingTime2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            etaStr = eta.format(formatter);
            etaStr2 = eta2.format(formatter);
        }

        System.out.printf("%-8v %s/min %d/%d %0.2f%% %s %s %s %s \n", action,
                FileUtils.byteCountToDisplaySize(BigInteger.valueOf(speed)), currentNumber, totalNumber,
                100 * progressSize, remainingTimeStr, etaStr, remainingTimeStr2, etaStr2);
    }

    public void countFiles() {
        totalNumber = 0;
        totalSize = 0;
        for (Path source : synchronizer.sources) {
            synchronizer.source = source;
            countFiles(source);
        }
    }

    private void countFiles(Path path) {
        try {
            Files.list(path).forEach(item -> {
                if (Files.isDirectory(item)) {
                    countFiles(item);
                } else {
                    if (!synchronizer.autoFind || item.getFileName().toString() != ".sinf_mark.json") {
                        try {
                            totalNumber++;
                            totalSize += Files.size(item);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            
        } catch (Exception e) {
            //TODO: handle exception
        }

    }
}