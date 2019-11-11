package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SourceCodeFileReader {
	/**
	 * 读取源程序文件的方法，配合charAt，就成为了作业中的GETCH方法。
	 * @param fileName 源程序文件路径
	 * @param lineFeed 指定换行字符
	 * @return 返回读入的文件内容字符串
	 */
	public static String readFileContent(String fileName, String lineFeed) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer fileStringBuffer = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempStr;
			while ((tempStr = reader.readLine()) != null) {
				fileStringBuffer.append(tempStr + lineFeed);
			}
			reader.close();
			return fileStringBuffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return fileStringBuffer.toString();
	}
}
