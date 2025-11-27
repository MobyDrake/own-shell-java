import java.util.Arrays;
import java.util.Optional;

public enum Command {
    EXIT,
    ECHO,
    TYPE;

    public static Optional<Command> getByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String str = name.toUpperCase();
        return Arrays.stream(Command.values())
                .filter(cm -> cm.name().equals(str))
                .findFirst();
    }
}