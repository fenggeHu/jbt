package utils;

/**
 * @author hujinfeng  @Date 2020/11/28
 **/
public class NameStringUtil {

    /**
     * 属性命名 - 首字母小写
     */
    public static String toPropertyName(String name) {
        String camel = toCamelCase(name);
        char[] ch = camel.toCharArray();
        ch[0] = Character.toLowerCase(ch[0]);
        return String.valueOf(ch);
    }

    /**
     * 类名 - 首字母大写
     */
    public static String toClassName(String name) {
        String camel = toCamelCase(name);
        char[] ch = camel.toCharArray();
        ch[0] = Character.toUpperCase(ch[0]);
        return String.valueOf(ch);
    }

    /**
     * 类名转属性名
     */
    public static String classNameToPropertyName(String name) {
        char[] ch = name.toCharArray();
        ch[0] = Character.toLowerCase(ch[0]);
        return String.valueOf(ch);
    }

    /**
     * 驼峰转下划线
     *
     * @param name
     * @return
     */
    public static String toSnakeName(String name) {
        return reverseCamelLowerCase(name);
    }

    /**
     * 把驼峰转下划线,并全部转小写. UserName --> user_name
     */
    public static String reverseCamelLowerCase(String name) {
        StringBuilder sb = new StringBuilder();
        char[] ch = name.trim().toCharArray();

        for (int i = 0; i < ch.length; i++) {
            if (Character.isUpperCase(ch[i])) {
                if (i > 0) sb.append("_");
                sb.append(Character.toLowerCase(ch[i]));
            } else if (ch[i] == '-') {
                sb.append('_');
            } else {
                sb.append(ch[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线/横线转驼峰
     *
     * @param name
     * @return
     */
    public static String toCamelCase(String name) {
        StringBuilder sb = new StringBuilder();
        char[] ch = name.trim().toCharArray();
        boolean split = false;
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == '-' || ch[i] == '_') {
                split = true;
                continue;
            }
            if (split) {
                sb.append(Character.toUpperCase(ch[i]));
                split = false;
            } else {
                sb.append(ch[i]);
            }
        }

        return sb.toString();
    }

}
