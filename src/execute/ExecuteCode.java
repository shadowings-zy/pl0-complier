package execute;

import parsing.Code;

import java.util.ArrayList;

/**
 * 执行生成的目标代码类
 */
public class ExecuteCode {
    private static final int MAX = 1000;
    private static int[] stack = new int[MAX];  // 执行栈
    private static int top = 3;  // 栈顶元素位置
    private static int base = 1; // program, base, and top-stack register
    private static int programCounter = 0; // 程序计数器


    /**
     * 执行中间代码
     *
     * @param codeList 中间代码列表
     */
    public static void execute(ArrayList<Code> codeList) {
        stack[1] = 0;
        stack[2] = 0;
        stack[3] = 0;

        do {
            Code code = codeList.get(programCounter++);
            int levelDifference = Integer.parseInt(code.getLevelDifference());
            int addressOffset = Integer.parseInt(code.getAddressOffset());
            switch (code.getFunction()) {
                case "LIT":
                    stack[++top] = addressOffset;
                    break;
                case "LOD":
                    stack[++top] = stack[calculateBase(base, levelDifference) + addressOffset];
                    break;
                case "STO":
                    stack[calculateBase(base, levelDifference) + addressOffset] = stack[top];
                    System.out.println(stack[top]);
                    top--;
                    break;
                case "CAL":
                    stack[top + 1] = calculateBase(base, levelDifference);
                    stack[top + 2] = base;
                    stack[top + 3] = programCounter;
                    base = top + 1;
                    programCounter = addressOffset;
                    break;
                case "INT":
                    top += addressOffset;
                    break;
                case "JMP":
                    programCounter = addressOffset;
                    break;
                case "JPC":
                    if (stack[top] == 0) {
                        programCounter = addressOffset;
                    }
                    top--;
                    break;
                case "OPR":
                    handleOperator(code);
                    break;
            }
        } while (programCounter > 0 && programCounter < codeList.size());
    }

    /**
     * 计算偏移量
     *
     * @param currentLevel 当前层级
     * @param levelDiff    层极差
     * @return 正确的地址
     */
    private static int calculateBase(int currentLevel, int levelDiff) {
        int address = currentLevel;
        for (int a = levelDiff; a > 0; a--){
            address = stack[address];
        }
        return address;
    }

    /**
     * 处理operator的方法
     * 0: return
     * 1: 相反数
     * 2: +
     * 3: -
     * 4: *
     * 5: /
     * 6: #
     * 7: ==
     * 8: <>
     * 9: <
     * 10: <=
     * 11: >
     * 12: >=
     *
     * @param code 中间代码
     */
    private static void handleOperator(Code code) {
        int addressOffset = Integer.parseInt(code.getAddressOffset());
        switch (addressOffset) // operator
        {
            case 0:
                top = base - 1;
                programCounter = stack[top + 3];
                base = stack[top + 2];
                break;
            case 1:
                stack[top] = -stack[top];
                break;
            case 2:
                top--;
                stack[top] += stack[top + 1];
                break;
            case 3:
                top--;
                stack[top] -= stack[top + 1];
                break;
            case 4:
                top--;
                stack[top] *= stack[top + 1];
                break;
            case 5:
                top--;
                if (stack[top + 1] == 0) {
                    System.out.println("Runtime Error: Divided by zero. Fix it as 1");
                    stack[top + 1] = 1;
                    stack[top] /= stack[top + 1];
                } else {
                    stack[top] /= stack[top + 1];
                }
                break;
            case 6:
                stack[top] %= 2;
                break;
            case 7:
                top--;
                stack[top] = (stack[top] == stack[top + 1]) ? 1 : 0;
                break;
            case 8:
                top--;
                stack[top] = (stack[top] != stack[top + 1]) ? 1 : 0;
            case 9:
                top--;
                stack[top] = (stack[top] < stack[top + 1]) ? 1 : 0;
                break;
            case 10:
                top--;
                stack[top] = (stack[top] <= stack[top + 1]) ? 1 : 0;
            case 11:
                top--;
                stack[top] = (stack[top] > stack[top + 1]) ? 1 : 0;
                break;
            case 12:
                top--;
                stack[top] = (stack[top] >= stack[top + 1]) ? 1 : 0;
        }
    }
}
