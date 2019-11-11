package lexical;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 词法分析类
 *
 * @author ZY
 */
public class LexicalAnalyzer {
    // 关键字
    private static ArrayList<String> keywords = new ArrayList<String>(Arrays.asList("const", "var", "procedure",
            "begin", "end", "if", "then", "call", "while", "do", "read", "write"));

    // 运算符
    private static ArrayList<String> operator = new ArrayList<String>(
            Arrays.asList("=", "<>", "<", ">", "<=", ">=", ":=", "+", "-", "*", "/", "#"));

    // 界符
    private static ArrayList<String> delimiters = new ArrayList<String>(Arrays.asList(";", ".", ",", "(", ")"));

    // 过滤掉所有的空格
    private static String filterBlank(String str) {
        return str.replace(" ", "").replace("\n", "").replace("\t", "");
    }

    /**
     * 判断str中从第index位开始，是否能匹配到target中的某一项，若能，则返回该项，否则返回空，若匹配出多项，以最长的为准
     *
     * @param str    输入的字符串
     * @param index  词法分析器扫描到哪一个字符
     * @param target 关键字数组
     * @return 返回匹配的字符
     */
    private static String matchList(String str, int index, ArrayList<String> target) {
        ArrayList<String> outputList = new ArrayList<String>();
        for (int a = 0; a < target.size(); a++) {
            int endIndex = index + target.get(a).length();
            if (endIndex <= str.length()) {
                String tempStr = str.substring(index, endIndex);
                if (target.indexOf(tempStr) != -1) {
                    outputList.add(tempStr);
                }
            }
        }

        if (outputList.size() > 0) {
            // 获取最长的字符
            int maxLength = outputList.get(0).length();
            int maxLengthIndex = 0;
            for (int a = 0; a < outputList.size(); a++) {
                if (outputList.get(a).length() > maxLength) {
                    maxLength = outputList.get(a).length();
                    maxLengthIndex = a;
                }
            }
            return outputList.get(maxLengthIndex);
        }
        return "";
    }

    /**
     * 匹配关键字
     *
     * @param str   关键字
     * @param index 扫描到的位置
     * @return 返回匹配的字符
     */
    private static String isKeywords(String str, int index) {
        return matchList(str, index, keywords);
    }

    /**
     * 匹配数字
     *
     * @param str   关键字
     * @param index 扫描到的位置
     * @return 返回匹配的字符
     */
    private static String isNumber(String str, int index) {
        String tempNum = "";
        while (index < str.length() && Character.isDigit(str.charAt(index))) {
            tempNum += str.charAt(index);
            index++;
        }
        return tempNum;
    }

    /**
     * 匹配运算符
     *
     * @param str   关键字
     * @param index 扫描到的位置
     * @return 返回匹配的字符
     */
    private static String isOperator(String str, int index) {
        return matchList(str, index, operator);
    }

    /**
     * 匹配界符
     *
     * @param str   关键字
     * @param index 扫描到的位置
     * @return 返回匹配的字符
     */
    private static String isDelimiters(String str, int index) {
        return matchList(str, index, delimiters);
    }

    /**
     * 词法分析函数，也就是作业中要求的GETSYM函数
     *
     * @param input 输入的字符串
     * @return 返回Token数组
     */
    public static ArrayList<Token> getsym(String input) {
        String str = filterBlank(input);
        ArrayList<Token> wordToken = new ArrayList<Token>();
        int index = 0;
        String temp = ""; // 用于缓存ident字符
        while (index < str.length()) {
            String keywords = isKeywords(str, index);
            String number = isNumber(str, index);
            String operator = isOperator(str, index);
            String delimiters = isDelimiters(str, index);
            if (keywords.length() != 0) {
                if (temp.length() != 0) {
                    wordToken.add(new Token("IDENT", temp, ""));
                    temp = "";
                }
                wordToken.add(new Token(keywords.toUpperCase() + "SYM", "", ""));
                index += keywords.length();
            } else if (number.length() != 0) {
                if (temp.length() != 0) {
                    wordToken.add(new Token("IDENT", temp, ""));
                    temp = "";
                }
                wordToken.add(new Token("NUMBER", "", number));
                index += number.length();
            } else if (operator.length() != 0) {
                if (temp.length() != 0) {
                    wordToken.add(new Token("IDENT", temp, ""));
                    temp = "";
                }
                wordToken.add(new Token("SYM_" + operator, "", ""));
                index += operator.length();
            } else if (delimiters.length() != 0) {
                if (temp.length() != 0) {
                    wordToken.add(new Token("IDENT", temp, ""));
                    temp = "";
                }
                wordToken.add(new Token("SYM_" + delimiters, "", ""));
                index += delimiters.length();
            } else {
                temp += str.charAt(index);
                index++;
            }
        }
        return wordToken;
    }
}
