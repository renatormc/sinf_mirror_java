package go.sptc.sinf.app;

import java.nio.file.Path;

public class JobConfig {
    public Path relPath;
    public boolean acknowledge;

    public JobConfig(Path relPath, boolean acknowledge) {
        this.relPath = relPath;
        this.acknowledge = acknowledge;
    }
}