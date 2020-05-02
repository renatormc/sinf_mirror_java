package go.sptc.sinf.app;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.time.Duration;

import org.apache.commons.io.FileUtils;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {

    public static void main(String[] args) throws Exception {

        ArgumentParser parser = ArgumentParsers.newFor("Sinf Mirror").build().defaultHelp(true)
                .description("Mirror one folder to another");
        parser.addArgument("-s", "--source").action(Arguments.append()).help("Folder to be mirroed");
        parser.addArgument("-d", "--dest").help("Folder to mirror to");
        parser.addArgument("-c", "--casename").help("Case name");
        parser.addArgument("-m", "--max-depth").setDefault(3)
                .help("Specifeis how deep the program search for case folders on existing drives.");
        parser.addArgument("-w", "--workers").setDefault(10).help("Number of workers");
        parser.addArgument("-t", "--threshold").type(Long.class).setDefault((long) 8)
                .help("Size in megabytes above which there will be no concurrency");
        parser.addArgument("-b", "--buffer").setDefault((long) 1).type(Long.class).help("Buffer size in megabytes");
        parser.addArgument("-v", "--verbose").setDefault(false).help("Print extra messages");
        parser.addArgument("-p", "--purge").setDefault(false)
                .help("Purge files on destination if it doesn't exist on source");
        parser.addArgument("-r", "--retries").setDefault(10).help("Specifies the number of retries on failed copies");
        parser.addArgument("-i", "--wait").setDefault(1).help("Specifies the wait time between retries, in seconds.");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        Synchronizer synchronizer = new Synchronizer();
        for (String source : ns.<String>getList("source")) {
            synchronizer.sources.add(Paths.get(source));
        }
        synchronizer.dest = Paths.get(ns.getString("dest"));
        synchronizer.nWorkers = ns.getInt("workers");
        synchronizer.threshold = ns.getLong("threshold") * 1048576;
        synchronizer.bufferSize = ns.getLong("buffer") * 1048576;
        synchronizer.verbose = ns.getBoolean("verbose");
        synchronizer.purge = ns.getBoolean("purge");
        synchronizer.retries = ns.getInt("retries");
        synchronizer.wait = Duration.ofSeconds(ns.getInt("wait"));

        System.out.println("Contando arquivos...");
        Progress progress = new Progress();
        progress.setSynchronizer(synchronizer);
        progress.countFiles();

        System.out.printf("N arquivos encontrados: %d\n", progress.totalNumber);
        System.out.printf("Total bytes: %s\n",
                FileUtils.byteCountToDisplaySize(BigInteger.valueOf(progress.totalSize)));

        Thread threadProgress = new Thread(progress);
        Thread threadSynchronizer = new Thread(synchronizer);
        threadProgress.start();
        threadSynchronizer.start();

        threadProgress.join();

        System.out.printf("\nTamanho total:           %s\n",
                FileUtils.byteCountToDisplaySize(BigInteger.valueOf(progress.totalSize)));
        System.out.printf("Arquivos analisados:     %d\n", progress.totalNumber);
        System.out.printf("Arquivos novos:     %d\n", progress.newFiles);
        System.out.printf("Arquivos atualizados:     %d\n", progress.updatedFiles);
        System.out.printf("Arquivos iguais:     %d\n", progress.equalFiles);
        System.out.printf("Arquivos deletados:     %d\n", progress.deletedItems);
        System.out.printf("N workers:     %d\n", synchronizer.nWorkers);
        System.out.printf("Tempo gasto:     %s\n", Helpers.fmtDuration(progress.elapsed));
        System.out.printf("Velocidade média:        %s/min\n",
                FileUtils.byteCountToDisplaySize(BigInteger.valueOf(progress.avgSpeed * 60)));
    }
}
