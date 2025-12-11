import java.util.Arrays;

public class Task1 {

    void main() {
        System.out.println(Joiner.on("<>").join(1, null, 2, "4"));
    }

    public static class Joiner {

        private final String separator;

        private Joiner(String separator) {
            this.separator = separator;
        }

        public static Joiner on(String separator) {
            return new Joiner(separator);
        }

        public static Joiner on() {
            return new Joiner("");
        }

        @SafeVarargs
        public final <T> String join(T... elements) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (T e : elements) {
                if (e == null) {
                    continue;
                }
                if (!first) {
                    sb.append(separator);
                } else {
                    first = false;
                }
                sb.append(e);
            }
            return sb.toString();
        }
    }
}
