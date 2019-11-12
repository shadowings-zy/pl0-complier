package parsing;

import lexical.Token;
import error.PL0Error;

import java.util.ArrayList;

/**
 * 生成声明变量表的类
 * 声明变量表就是作业中的TABLE
 *
 * @author ZY
 */
public class DeclarationTableParser {
    private static int tokenListIndex = 0;  // 扫描token表用的指针
    private static int initLevel = 0;  // 初始深度，即为作业中的BLOCK
    private static int initAdr = 3;  // 初始地址，即为作业中的DX
    private static int procedureCounter = 2;  // 给procedure的index
    private static ArrayList<Declaration> declarationList = new ArrayList<Declaration>();  // 生成的declaration list

    /**
     * 生成declaration list的方法
     *
     * @param tokenList token列表
     * @param level     初始深度
     * @param adr       初始地址
     * @return declaration list
     */
    public static ArrayList<Declaration> parse(ArrayList<Token> tokenList, int level, int adr) {
        initLevel = level;
        initAdr = adr;
        while (tokenListIndex < tokenList.size()) {
            switch (tokenList.get(tokenListIndex).getSym()) {
                case "CONSTSYM":
                    constantDefinitionParser(tokenList);
                    break;
                case "VARSYM":
                    variableDefinitionParser(tokenList, initLevel, initAdr);
                    break;
                case "PROCEDURESYM":
                    procedureDefinitionParser(tokenList, initLevel);
                    break;
            }
            tokenListIndex++;
        }

        return declarationList;
    }

    /**
     * 对const进行语法翻译
     *
     * @param tokenList token列表
     */
    private static void constantDefinitionParser(ArrayList<Token> tokenList) {
        while (tokenListIndex < tokenList.size() - 1) {
            tokenListIndex++;
            if (tokenList.get(tokenListIndex).getSym().equals("IDENT")) {
                // 新建一个常量声明
                String identName = tokenList.get(tokenListIndex).getId();
                Declaration constDeclaration = new Declaration(identName, "CONSTANT", "", "", "", -1, -1);

                // 检测是否对常量进行了赋值
                checkAndAddDeclaration(tokenList, constDeclaration);

                // 若读取到分号，则结束，若读取到逗号，则什么都不做，若读取到其他，则抛异常
                if (tokenList.get(tokenListIndex).getSym().equals("SYM_;")) {
                    break;
                } else if (!tokenList.get(tokenListIndex).getSym().equals("SYM_,")) {
                    PL0Error.log(2);
                    break;
                }
            } else {
                PL0Error.log(1);
                break;
            }
        }
    }

    /**
     * 对var进行语法翻译
     *
     * @param tokenList token列表
     */
    private static void variableDefinitionParser(ArrayList<Token> tokenList, int level, int adr) {
        while (tokenListIndex < tokenList.size() - 1) {
            tokenListIndex++;
            if (tokenList.get(tokenListIndex).getSym().equals("IDENT")) {
                // 新建一个变量声明
                String identName = tokenList.get(tokenListIndex).getId();
                Declaration varDeclaration = new Declaration(identName, "VARIABLE", "", level + "", (adr++) + "", -1, -1);

                // 检测是否对变量进行了赋值
                checkAndAddDeclaration(tokenList, varDeclaration);

                // 若读取到分号，则结束，若读取到逗号，则什么都不做，若读取到其他，则抛异常
                if (tokenList.get(tokenListIndex).getSym().equals("SYM_;")) {
                    break;
                } else if (!tokenList.get(tokenListIndex).getSym().equals("SYM_,")) {
                    PL0Error.log(4);
                    break;
                }
            } else {
                PL0Error.log(3);
                break;
            }
        }
    }

    /**
     * 对procedure进行语法翻译
     *
     * @param tokenList token列表
     */
    private static void procedureDefinitionParser(ArrayList<Token> tokenList, int level) {
        if (tokenListIndex < tokenList.size() - 1) {
            tokenListIndex++;
            if (tokenList.get(tokenListIndex).getSym().equals("IDENT")) {
                String identName = tokenList.get(tokenListIndex).getId();
                Declaration procedureDeclaration = new Declaration(identName, "PROCEDURE", "", level + "", "", tokenListIndex - 1, -1);
                declarationList.add(procedureDeclaration);
                tokenListIndex++;

                label:
                while (tokenListIndex < tokenList.size() - 1) {
                    // 如果在函数中还有var或者procedure，则递归继续执行
                    switch (tokenList.get(tokenListIndex).getSym()) {
                        case "VARSYM":
                            variableDefinitionParser(tokenList, level + 1, initAdr);
                            break;
                        case "PROCEDURESYM":
                            if (level < 3) {
                                procedureDefinitionParser(tokenList, level + 1);
                            } else {
                                PL0Error.log(6);
                            }
                            break;
                        case "ENDSYM":
                            procedureDeclaration.setEnd(tokenListIndex);
                            break label;
                    }
                    tokenListIndex++;
                }
            } else {
                PL0Error.log(7);
            }
        }
    }

    /**
     * 检查是否对某个变量/常量进行了赋值，即判断x := 20;
     * @param tokenList     token列表
     * @param declaration   声明
     */
    private static void checkAndAddDeclaration(ArrayList<Token> tokenList, Declaration declaration) {
        tokenListIndex++;
        if (tokenList.get(tokenListIndex).getSym().equals("SYM_=") && tokenList.get(tokenListIndex + 1).getSym().equals("NUMBER")) {
            declaration.setVal(tokenList.get(tokenListIndex + 1).getNum());
            declarationList.add(declaration);
            tokenListIndex += 2;
        } else {
            declarationList.add(declaration);
        }
    }
}
