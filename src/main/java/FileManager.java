import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class FileManager {

    private final String[] PATH;
    private final Path HOME_PATH;

    private Path currentPath = Path.of("");

    public FileManager() {
        PATH = Objects.toString(System.getenv("PATH"), "").split(File.pathSeparator);
        HOME_PATH = Path.of(Objects.toString(System.getenv("HOME"), System.getProperty("user.home")));
    }

    public Optional<File> findFileInPath(final String fileName) {
        return Arrays.stream(PATH)
                     .map(dir -> new File(dir, fileName))
                     .filter(f -> f.exists() && f.canExecute())
                     .findFirst();
    }

    public Path getCurrentPath() {
        return currentPath.toAbsolutePath();
    }

    public void changeDirectoryToHome() {
        this.currentPath = HOME_PATH;
    }

    public boolean changeDirectoryTo(Path path) {
        boolean isChanged = false;
        if (Files.exists(path) && Files.isDirectory(path)) {
            currentPath = path;
            isChanged = true;
        }
        return isChanged;
    }
}
