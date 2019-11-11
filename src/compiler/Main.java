package compiler;

import execute.ExecuteCode;
import lexical.LexicalAnalyzer;
import lexical.Token;
import parsing.Code;
import parsing.Declaration;
import parsing.DeclarationTableParser;
import parsing.Parser;

import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		String sourceCodePath = "/Users/zy/workspace-for-Java/PL0/src/code/test.pl0";  // 源代码地址
		System.out.println("\n==========源代码==========");
		String sourceCode = SourceCodeFileReader.readFileContent(sourceCodePath, "\n");
		System.out.println(sourceCode);

		System.out.println("\n==========词法分析后的Token==========");
		ArrayList<Token> wordsToken = LexicalAnalyzer.getsym(sourceCode);
		for (int a = 0; a < wordsToken.size(); a++) {
			System.out.println(a + " " + wordsToken.get(a).toString());
		}

		System.out.println("\n==========声明的引用table表==========");
		ArrayList<Declaration> declarationList = DeclarationTableParser.parse(wordsToken, 0, 3);
		for (Declaration declaration : declarationList) {
			System.out.println(declaration.toString());
		}

		System.out.println("\n==========翻译成的中间代码==========");
//		ArrayList<Code> codeList = importTest(); // 使用测试中间代码
		Parser.init(wordsToken, declarationList);
		ArrayList<Code> codeList = Parser.getCode();
		for (int a = 0; a < codeList.size(); a++) {
			System.out.println(a + " " + codeList.get(a).toString());
		}



		System.out.println("\n==========执行代码==========");
		ExecuteCode.execute(codeList);
	}

	/**
	 * 导入测试中间代码
	 * @return code list
	 */
	public static ArrayList<Code> importTest() {
		String testCodePath = "/Users/zy/workspace-for-Java/PL0/src/code/code.pl0code";  // 测试中间代码地址
		String testCode = SourceCodeFileReader.readFileContent(testCodePath, "\n");
		String[] testCodeLine = testCode.split("\n");
		ArrayList<Code> codeList = new ArrayList<Code>();
		for (String code : testCodeLine) {
			String[] detail = code.split(" ");
			Code temp = new Code(detail[0], detail[1], detail[2]);
			codeList.add(temp);
		}
		return codeList;
	}
}
