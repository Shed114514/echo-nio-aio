package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 编写一个模拟键盘输入工具类
 */
public class InputUtil {
    // 工具类构造私有化
    private InputUtil() {}
    // 通过缓冲区字符输入流读取字符数据
    public static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));
    //若result不为空则返回字符串信息
    public static String getString(String prompt) {
        String result = null;
        while (result == null || "".equals(result)) {
            System.err.println(prompt);
            try {
                result = KEYBOARD_INPUT.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
