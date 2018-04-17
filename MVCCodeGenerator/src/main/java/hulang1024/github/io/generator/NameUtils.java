package hulang1024.github.io.generator;

public class NameUtils {
    /* 将下划线大写风格转换为驼峰风格 */
    public static String underlineToCamelCase(String str) {
        StringBuilder sb = new StringBuilder();
        
        boolean nextUpperCase = false;
        char c;
        for (int i = 0, len = str.length(); i < len; i++) {
            c = str.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        
        return sb.toString();
    }
}
