# pl0-complier

## 写在前面
这是一个使用Java编写的简易的pl0语言编译器，所有的源代码均在src文件中。

## 目录结构
- code：pl0样例代码、pl0样例目标代码、pl0语言翻译规则
- compiler：主程序、文件IO操作
- error：编译错误类
- execute：执行目标代码类
- lexical：词法分析相关类
- parsing：语法分析以及生成目标代码相关类

## 运行相关
注意，运行之前需要先确认Main类中源文件路径是否正确。