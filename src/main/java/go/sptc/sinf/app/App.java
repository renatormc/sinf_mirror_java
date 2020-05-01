package go.sptc.sinf.app;

public class App {

    public static void main(String[] args) {
        // String source = args[0];
        Synchronizer sync = new Synchronizer();
        sync.setSource("D:\\teste_report\\C1");
        sync.setDest("D:\teste_report\\c1_copia_deletar");
        sync.countFiles();
        System.out.println(sync.totalNumber);

    }
}
