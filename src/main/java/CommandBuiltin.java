import java.util.Arrays;
import java.util.Optional;

public enum CommandBuiltin {
    EXIT,
    ECHO,
    TYPE,
    PWD,
    CD;

    public static Optional<CommandBuiltin> getByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String str = name.toUpperCase();
        return Arrays.stream(CommandBuiltin.values())
                .filter(cm -> cm.name().equals(str))
                .findFirst();
    }
}