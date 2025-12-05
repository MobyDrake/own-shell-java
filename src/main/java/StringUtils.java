import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static List<String> parse(String line) {
        if (line == null || line.isBlank()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        StringBuilder wordSb = new StringBuilder();
        boolean quoteStart = false;
        for (char ch : line.toCharArray()) {
            if (ch == '\'') {
                quoteStart = !quoteStart;
            } else if (ch == ' ' && !quoteStart) {
                if (!wordSb.isEmpty()) {
                    result.add(wordSb.toString());
                }
                wordSb.setLength(0);
            } else {
                wordSb.append(ch);
            }
        }
        if (!wordSb.isEmpty()) {
            result.add(wordSb.toString());
        }
        return result;
    }
}
