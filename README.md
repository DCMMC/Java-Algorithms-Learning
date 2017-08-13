# Java 学习

![npm](https://img.shields.io/npm/l/express.svg)
![StillCoding](https://img.shields.io/badge/Still-Coding-green.svg)
![Java入门](https://img.shields.io/badge/Java-%E5%85%A5%E9%97%A8-blue.svg)

> 欢迎想和我讨论关于一下书籍的内容的人给我提Issues, 当然也可以和我qq交流(97294597).
>
> Welcome to discuss with me about the contents of the books below, you can add an issue or chat with me in QQ(97294597). :)

---

## 参考书籍:

*Thinking to Java 4th Edition* //里面涉及了很多设计模式相关的知识, 用作入门书还行, 不过书中设计模式相关的东西看起来就会有点吃力

*Algorithm 4th Edition* //普林斯顿的算法课用的教材, 很适合用作算法的入门书. 不过 里面的练习较多而且有一些练习不是很有用. [视频地址](https://www.coursera.org/learn/algorithms-part1/lecture/xaxyP/analysis-of-algorithms-introduction)

*Java The Complete Reference Ninth Edition* //不建议用作入门书, 里面的内容简直是多而全, 虽然有一点详略, 不过确实只适合用作一本字典

---

# 工作环境

## IDE

JetBrain IDEA

---

Sublime Text3:

对于小型项目或者单文件程序, 直接在Sublime Text3上面打代码就好了, 编译运行需要自己搭建一下:
### 创建执行编译运行的小脚本

Windows:

runJava.bat: 编译加运行(不能自定义参数)

``` bat
@ECHO OFF
cd %~dp1
ECHO Compiling %~nx1.......
IF EXIST %~dnp1.class (
DEL %~dnp1.class
)
javac -encoding UTF-8 %~f1
IF EXIST %~dnp1.class (
ECHO -----------OUTPUT-----------
REM 用我编写的LoadAnyClass程序来加载class文件,这样就可以解决原来用java只能直接运行不含有package语句的程序
cd %~dp0
REM 我也不知道为什么,必须要指定当前目录为CLASSPATH,才不会报错IllegalAccessError
REM 好像IllegalAccessError是跟ClassLoader前后不一样之类的有关的异常.
java -classpath "." LoadAnyClasses %~dnp1.class
REM cd %~dp1
REM java %~n1
)
```

runJavaCmd.bat: 编译然后保留cmd窗口让用户自己运行

``` bat
@echo off
Rem 不显示@后面的命令，echo off表示关闭命令的回显
cd %~dp1
ECHO Compiling %~nx1.......
IF EXIST %~n1.class (
DEL %~n1.class
)
javac -encoding UTF-8 %~nx1
IF EXIST %~n1.class (
start cmd.exe dir
)
```
> bat中目录变量的意思: [10 DOS Batch tips](https://weblogs.asp.net/jongalloway/top-10-dos-batch-tips-yes-dos-batch)
Linux/macOS:

runJava.sh:

``` bash
[ -f "$1.class" ] && rm $1.class
for file in $1.java
do
  echo "Compiling $file......"
  javac $file
done
if [ -f "$1.class"]
then
  echo "------Output------"
# 进入到脚本所在的目录
  cd `dirname $0`
   java LoadAnyClasses $1
else
  echo " "
fi
```

> P.S. 如果你想编译所有的Java文件，需要将第二行的$1.java替换成*.java

`LoadAnyClasses`是我自己用Java写的一个能够加载任意class字节码文件的小程序
[程序源码](./Algorithms/tk/dcmmc/workspace/LoadAnyClasses.java)

### 将上述小脚本放入JDK的bin目录下(或者其他放入$Path的目录)

### 创建sublime-build
用notepad++之类的文本编辑器在在%Sublime Text 安装目录%/package/或者Sublime Text用户目录中创建一下两个文件:

这里我为了不修改Sublime Text自带的JavaC.sublime-package, 我选择在Sublime Text用户目录中创建.

这里以Windows下为例:
在C:\Users\DCMMC\AppData\Roaming\Sublime Text 3\Packages\User下创建:

MyJava_cmdline.sublime-build:
```
{
  "shell" : true,
  "cmd": "start cmd /c runJavaCmd.bat \"$file\"",
  "file_regex": "^(...?):([0-9]):?([0-9])",
  "selector": "source.java",
  "encoding": "GBK"
}
```

MyJava_SublimeConsole.sublime-build:
```
{
  "shell_cmd": "runJava.bat \"$file\"",
  "file_regex": "^(...?):([0-9]):?([0-9])",
  "selector": "source.java",
  "encoding": "GBK"
}
```

> 因为Sublime Text3保存源码的默认格式是UTF-8，所以需要将"encoding": 设置为GBK

### 重新打开Sublime Text3, 在Tools -> Build System中选择要用的编译脚本, 按Ctril+B编译运行.

> 上述所有文件均可在[Workspace](https://github.com/DCMMC/Java/tree/master/Algorithms/tk/dcmmc/workspace)中找到

## 笔记

我用的Markdown格式来记笔记, 编辑器用的是[小书匠](http://markdown.xiaoshujiang.com/)

笔记的[Github地址](https://github.com/DCMMC/Markdown_Notes)

## License

MIT
