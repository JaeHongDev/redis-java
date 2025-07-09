package util;

import java.util.List;
import java.util.regex.Pattern;

public class PatternMatcher {

    public static Pattern globToRegex(String glob) {
        StringBuilder sb = new StringBuilder("^");
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*': sb.append(".*"); break;
                case '?': sb.append("."); break;
                case '.': case '(': case ')': case '+':
                case '|': case '^': case '$': case '@':
                case '%': case '\\': sb.append("\\").append(c); break;
                default: sb.append(c); break;
            }
        }
        sb.append("$");
        return Pattern.compile(sb.toString());
    }

    public static void main(String...args) {
        System.out.println(PatternMatcher.globToRegex("*"));

        var pattern = PatternMatcher.globToRegex("*");

        List.of("f1", "a1","f2").forEach(value -> {
            Printer.print(pattern.matcher(value).matches() + " " + value);
        });
    }
}
