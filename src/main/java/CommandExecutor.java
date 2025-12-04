import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CommandExecutor {

    private static final String SHELL_BUILTIN = "%s is a shell builtin";
    private static final String NOT_FOUND = "%s: not found";
    private static final String NO_SUCH_FILE_OR_DIRECTORY = "cd: %s: No such file or directory";
    private static final String TILDE = "~";

    private final FileManager fileManager;

    public CommandExecutor(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void executeCommand(CommandBuiltin commandBuiltin, String[] lineArgs) {
        Cmd cmd = createBuiltinCmd(commandBuiltin, lineArgs);
        switch (cmd) {
            case Cmd.EXIT _ -> System.exit(0);
            case Cmd.ECHO(String value) -> IO.println(value);
            case Cmd.TYPE(String msg) -> IO.println(msg);
            case Cmd.PWD(String value) -> IO.println(value);
            case Cmd.CD(String value) -> {
                if (TILDE.equals(value)) {
                    fileManager.changeDirectoryToHome();
                } else {
                    Path path = fileManager.getCurrentPath().resolve(value).toAbsolutePath().normalize();
                    if (!fileManager.changeDirectoryTo(path)) {
                        IO.println(NO_SUCH_FILE_OR_DIRECTORY.formatted(value));
                    }
                }
            }
        }
    }

    private Cmd createBuiltinCmd(final CommandBuiltin commandBuiltin, final String[] lineArgs) {
        return switch (commandBuiltin) {
            case EXIT -> new Cmd.EXIT();
            case ECHO -> {
                String value = Arrays.stream(lineArgs, 1, lineArgs.length).collect(Collectors.joining(" "));
                yield new Cmd.ECHO(value);
            }
            case TYPE -> getTypeCmd(lineArgs[1]);
            case PWD -> new Cmd.PWD(fileManager.getCurrentPath().toString());
            case CD -> {
                String value = Arrays.stream(lineArgs, 1, lineArgs.length).collect(Collectors.joining(" "));
                yield new Cmd.CD(value);
            }
        };
    }

    private Cmd getTypeCmd(final String typeArg) {
        Optional<CommandBuiltin> commandOpt = CommandBuiltin.getByName(typeArg);
        final String value = commandOpt.map(command -> SHELL_BUILTIN.formatted(command.name().toLowerCase()))
                .orElseGet(() -> fileManager.findFileInPath(typeArg)
                        .map(f -> "%s is %s".formatted(typeArg, f.getPath()))
                        .orElse(NOT_FOUND.formatted(typeArg)));
        return new Cmd.TYPE(value);
    }
}
