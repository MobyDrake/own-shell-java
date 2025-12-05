import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

public final class FileExecutor {

    private static final String NOT_FOUND = "%s: not found";

    private final FileManager fileManager;

    public FileExecutor(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void executeFile(final List<String> lineArgs) {
        final String fileName = lineArgs.getFirst();
        Optional<File> fileOpt = fileManager.findFileInPath(fileName);
        if (fileOpt.isPresent()) {
            startProcess(lineArgs);
        } else {
            IO.println(NOT_FOUND.formatted(fileName));
        }
    }

    private void startProcess(final List<String> args) {
        try {
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true); // Merges stderr into stdout
            Process process = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
