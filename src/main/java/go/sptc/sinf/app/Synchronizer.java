package go.sptc.sinf.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Synchronizer {
    public long totalSize;
    public long totalNumber;
    public long currentNumber;
    public long currentSize;
    public Path source;
    public Path dest;

    public void setSource(String source) {
        this.source = Paths.get(source);
    }

    public void setDest(String dest) {
        this.dest = Paths.get(dest);
    }

    public void sync(String path) {

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null)
            return;

        for (File f : list) {
            if (f.isDirectory()) {
                sync(f.getAbsolutePath());
            } else {
                System.out.println("File:" + f.getAbsoluteFile());
            }
        }
    }

    public void countFiles() {
        totalNumber = 0;
        totalSize = 0;
        countFiles(source);
    }

    public void countFiles(Path path) {

        File root = path.toFile();
        File[] list = root.listFiles();

        if (list == null)
            return;

        for (File f : list) {
            if (f.isDirectory()) {
                countFiles(Paths.get(f.getAbsolutePath()));
            } else {
                totalNumber += 1;
                totalSize += f.length();
            }

        }
    }

    public void copyFile(File in, File out) throws IOException {

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(in);
            os = new FileOutputStream(out);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            currentNumber += 1;
            currentSize += in.length();
        } finally {
            is.close();
            os.close();
        }
    }

    public void update(){
        update(source);
    }

    public void update(Path path){
       
        File[] list = path.toFile().listFiles();
        Path relativePath = null;
        Path sourcePath = null;
        Path destPath = null;

        if (list == null)
            return;

        for (File f : list) {
            relativePath = path.relativize(source);
            destPath = dest.resolve(relativePath);
            if (f.isDirectory()) {
                if(destPath.e){

                }
            } else {
                totalNumber += 1;
                totalSize += f.length();
            }

        }
    }

}
