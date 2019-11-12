package parsing;

import error.PL0Error;
import lexical.Token;

import java.util.ArrayList;

/**
 * 语句分析类，生成目标代码
 *
 * @author ZY
 */
public class Parser {
    private static int tokenListIndex = 0;  // 扫描token表用的指针
    private static ArrayList<Code> code = new ArrayList<Code>();  // 生成的output list，即为作业中的code数组
    private static int filledId = -1;  // 回填用的id

    public static void init(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList) {
        gen("JMP", "0", "main");
        for (Declaration declaration : declarationList) {
            if (declaration.getKind().equals("PROCEDURE")) {
                gen("JMP", "0", declaration.getName());
            }
        }
        for (Declaration declaration : declarationList) {
            if (declaration.getKind().equals("PROCEDURE")) {
                declaration.setCodeStartIndex(code.size());
                gen("INT", "0", "3");
                parse(tokenList, declarationList, declaration.getStart(), declaration.getEnd());
                gen("OPR", "0", "0");
            }
        }

        filledCodeList("main", code.size() + "");  // 回填main函数指令
        gen("INT", "0", "3");
        parse(tokenList, declarationList, declarationList.get(declarationList.size() - 1).getEnd(), tokenList.size());
        gen("OPR", "0", "0");

        // 回填call中的所有函数地址
        for (Declaration declaration : declarationList) {
            if (declaration.getKind().equals("PROCEDURE")) {
                filledCodeList(declaration.getName(), declaration.getCodeStartIndex() + "");
            }
        }
    }

    /**
     * 对语句进行翻译，生成code list
     * read和write暂时不进行处理（因为不知道怎么处理，demo中也并没有给出io相关的指令）
     *
     * @param tokenList token列表
     */
    private static void parse(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList, int startIndex, int endIndex) {
        while (tokenListIndex < endIndex) {
            switch (tokenList.get(tokenListIndex).getSym()) {
                case "IDENT":
                    identParser(tokenList, declarationList);
                    break;
                case "BEGINSYM":
                    beginParser(tokenList, declarationList, startIndex);
                    break;
                case "IFSYM":
                    ifParser(tokenList, declarationList, startIndex, endIndex);
                    break;
                case "CALLSYM":
                    callParser(tokenList, declarationList);
                    break;
                case "WHILESYM":
                    whileParser(tokenList, declarationList, startIndex, endIndex);
                    break;
            }
            tokenListIndex++;
        }
    }

    /**
     * 翻译ident，例如 x := x + y 或 x := 20
     *
     * @param tokenList       token列表
     * @param declarationList 声明table
     */
    private static void identParser(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList) {
        Token left = tokenList.get(tokenListIndex);
        int leftIndex = getItemIndexInDeclarationList(left, declarationList);
        if (left != null && leftIndex != -1) {
            Token equals = getNext(tokenList);
            if (equals != null && equals.getSym().equals("SYM_:=")) {
                expressionParser(tokenList, declarationList, declarationList.get(leftIndex));
            } else if (equals != null && !(equals.getSym().equals("SYM_,") || equals.getSym().equals("SYM_;"))) {
                PL0Error.log(8);
            }
        } else {
            PL0Error.log(9);
        }
    }

    /**
     * 处理赋值时 := 的右半部分，即处理表达式
     * TODO：可简化代码
     *
     * @param tokenList       token列表
     * @param declarationList 变量声明表
     * @param left            := 的左半部分
     */
    private static void expressionParser(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList, Declaration left) {
        Token first = getNext(tokenList);
        int firstIndex = getItemIndexInDeclarationList(first, declarationList);
        if (first != null && firstIndex != -1 && first.getSym().equals("IDENT")) {
            Token next = getNext(tokenList);
            if (next != null && next.getSym().equals("SYM_+")) {
                // 如 x := x + y;
                generateOperatorCode(tokenList, declarationList, left, firstIndex, "2");
            } else if (next != null && next.getSym().equals("SYM_-")) {
                // 如 x := x - y
                generateOperatorCode(tokenList, declarationList, left, firstIndex, "3");
            } else if (next != null && next.getSym().equals("SYM_*")) {
                // 如 x := x * y
                generateOperatorCode(tokenList, declarationList, left, firstIndex, "4");
            } else if (next != null && next.getSym().equals("SYM_/")) {
                // 如 x := x / y
                generateOperatorCode(tokenList, declarationList, left, firstIndex, "5");
            } else if (next != null && next.getSym().equals("SYM_;")) {
                // 如 x := y;
                gen("LOD", declarationList.get(firstIndex).getLevel(), declarationList.get(firstIndex).getAdr());
                gen("STO", left.getLevel(), left.getAdr());
            } else {
                PL0Error.log(10);
            }
        } else if (first != null && first.getSym().equals("NUMBER")) {
            // 如 x := 20;
            gen("LIT", "0", first.getNum());
            gen("STO", left.getLevel(), left.getAdr());
        } else {
            PL0Error.log(9);
        }
    }

    /**
     * 翻译call
     *
     * @param tokenList       token列表
     * @param declarationList 声明列表
     */
    private static void callParser(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList) {
        Token ident = getNext(tokenList);
        int identIndex = getItemIndexInDeclarationList(ident, declarationList);
        if (ident != null && ident.getSym().equals("IDENT")) {
            Token end = getNext(tokenList);
            if (end != null && end.getSym().equals("SYM_;")) {
                if (identIndex != -1) {
                    gen("CAL", declarationList.get(identIndex).getLevel(), declarationList.get(identIndex).getName());
                }
            } else {
                PL0Error.log(16);
            }
        } else {
            PL0Error.log(15);
        }
    }

    private static void beginParser(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList, int startIndex) {
        int endIndex = findEnd(tokenList, tokenListIndex);
        while (tokenListIndex < endIndex) {
            Token next = getNext(tokenList);
            if (next != null && next.getSym().equals("ENDSYM")) {
                Token end = getNext(tokenList);
                if (end != null && (end.getSym().equals("SYM_.") || end.getSym().equals("SYM_;"))) {
                    gen("OPR", "0", "0");
                    return;
                } else {
                    PL0Error.log(14);
                }
            } else {
                parse(tokenList, declarationList, startIndex, endIndex);
            }
        }
    }

    /**
     * 翻译if
     *
     * @param tokenList       token列表
     * @param declarationList 声明列表
     */
    private static void ifParser(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList, int startIndex, int endIndex) {
        Token left = getNext(tokenList);
        Token operator = getNext(tokenList);
        Token right = getNext(tokenList);
        Token then = getNext(tokenList);
        if (left != null && operator != null && right != null && then != null && then.getSym().equals("THENSYM")) {
            booleanParser(left, right, operator, declarationList);

            Token next = getNext(tokenList);
            if (next != null && next.getSym().equals("BEGINSYM")) {
                int tempKey = --filledId;
                gen("JPC", "0", tempKey + "");
                beginParser(tokenList, declarationList, startIndex);
                filledCodeList(tempKey + "", code.size() + "");
            } else {
                PL0Error.log(11);
            }
        } else {
            PL0Error.log(11);
        }
    }

    /**
     * 翻译while
     *
     * @param tokenList       token列表
     * @param declarationList 声明列表
     */
    private static void whileParser(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList, int startIndex, int endIndex) {
        Token left = getNext(tokenList);
        Token operator = getNext(tokenList);
        Token right = getNext(tokenList);
        Token doIdent = getNext(tokenList);
        if (left != null && operator != null && right != null && doIdent != null && doIdent.getSym().equals("DOSYM")) {
            int loopStartPosition = code.size();
            booleanParser(left, right, operator, declarationList);

            Token next = getNext(tokenList);
            if (next != null && next.getSym().equals("BEGINSYM")) {
                int tempKey = --filledId;
                gen("JPC", "0", filledId + "");
                beginParser(tokenList, declarationList, startIndex);
                gen("JMP", "0", loopStartPosition + "");
                filledCodeList(tempKey + "", code.size() + "");
            } else {
                PL0Error.log(13);
            }
        } else {
            PL0Error.log(13);
        }
    }

    /**
     * 翻译布尔表达式，如 x < y
     *
     * @param left            左面的元素
     * @param right           右面的元素
     * @param operator        操作符
     * @param declarationList 声明列表
     */
    private static void booleanParser(Token left, Token right, Token operator, ArrayList<Declaration> declarationList) {
        booleanItemParser(left, declarationList);
        booleanItemParser(right, declarationList);
        switch (operator.getSym()) {
            case "SYM_==":
                gen("OPR", "0", "7");
                break;
            case "SYM_<>":
                gen("OPR", "0", "8");
                break;
            case "SYM_<":
                gen("OPR", "0", "9");
                break;
            case "SYM_<=":
                gen("OPR", "0", "10");
                break;
            case "SYM_>":
                gen("OPR", "0", "11");
                break;
            case "SYM_>=":
                gen("OPR", "0", "12");
                break;
        }
    }

    /**
     * 翻译布尔表达式中的某一项
     *
     * @param token           某一个token
     * @param declarationList 声明列表
     */
    private static void booleanItemParser(Token token, ArrayList<Declaration> declarationList) {
        if (token.getSym().equals("IDENT")) {
            int index = getItemIndexInDeclarationList(token, declarationList);
            if (index != -1) {
                gen("LOD", declarationList.get(index).getLevel(), declarationList.get(index).getAdr());
            } else {
                PL0Error.log(9);
            }
        } else if (token.getSym().equals("NUMBER")) {
            gen("LIT", "0", token.getNum());
        } else {
            PL0Error.log(12);
        }
    }

    /**
     * 根据操作符生成目标代码
     *
     * @param tokenList       token列表
     * @param declarationList 声明列表
     * @param left            操作符左面的声明
     * @param firstIndex      :=左面的声明
     * @param offset          操作符的偏移量
     */
    private static void generateOperatorCode(ArrayList<Token> tokenList, ArrayList<Declaration> declarationList, Declaration left, int firstIndex, String offset) {
        Token second = getNext(tokenList);
        int secondIndex = getItemIndexInDeclarationList(second, declarationList);
        Token end = getNext(tokenList);
        if (second != null && end != null && end.getSym().equals("SYM_;")) {
            gen("LOD", declarationList.get(firstIndex).getLevel(), declarationList.get(firstIndex).getAdr());
            gen("LOD", declarationList.get(secondIndex).getLevel(), declarationList.get(secondIndex).getAdr());
            gen("OPR", "0", offset);
            gen("STO", left.getLevel(), left.getAdr());
        }
    }

    /**
     * 回填数组
     *
     * @param key   占位数值
     * @param value 回填的数值
     */
    private static void filledCodeList(String key, String value) {
        for (Code item : code) {
            if (item.getAddressOffset().equals(key)) {
                item.setAddressOffset(value);
            }
        }
    }

    /**
     * 获取下一个token
     *
     * @param tokenList token列表
     * @return token
     */
    private static Token getNext(ArrayList<Token> tokenList) {
        if (tokenListIndex < tokenList.size() - 1) {
            tokenListIndex++;
            return tokenList.get(tokenListIndex);
        } else {
            return null;
        }
    }

    /**
     * 判断某个token是否已经在declaration list中声明
     *
     * @param token           token
     * @param declarationList 声明列表
     * @return 如果有，返回index，若没有，返回-1
     */
    private static int getItemIndexInDeclarationList(Token token, ArrayList<Declaration> declarationList) {
        for (int a = 0; a < declarationList.size(); a++) {
            if (token.getId().equals(declarationList.get(a).getName())) {
                return a;
            }
        }
        return -1;
    }

    /**
     * 将翻译好的功能码加入code数组中
     *
     * @param function        功能码
     * @param levelDifference 层次差
     * @param addressOffset   位移量
     */
    private static void gen(String function, String levelDifference, String addressOffset) {
        if (levelDifference.equals("")) {
            levelDifference = "0";
        }
        if (addressOffset.equals("")) {
            addressOffset = "0";
        }
        Code addCode = new Code(function, levelDifference, addressOffset);
        code.add(addCode);
    }

    /**
     * 匹配与当前begin对应的end
     *
     * @param tokenArrayList token列表
     * @param index          begin的位置
     * @return 对应的end的位置
     */
    private static int findEnd(ArrayList<Token> tokenArrayList, int index) {
        int begin = 1;
        for (int a = index + 1; a < tokenArrayList.size(); a++) {
            if (tokenArrayList.get(a).getSym().equals("BEGINSYM")) {
                begin++;
            } else if (tokenArrayList.get(a).getSym().equals("ENDSYM")) {
                begin--;
                if (begin == 0) {
                    return a;
                }
            }
        }
        return 0;
    }

    public static ArrayList<Code> getCode() {
        return code;
    }
}
