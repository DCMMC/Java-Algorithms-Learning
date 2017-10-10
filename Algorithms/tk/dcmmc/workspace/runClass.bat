@ECHO OFF

IF EXIST %~f1 (
REM 用我编写的LoadAnyClass程序来加载class文件,这样就可以解决原来用java只能直接运行不含有package语句的程序
REM 我也不知道为什么,必须要指定当前目录为CLASSPATH,才不会报错IllegalAccessError
REM 好像IllegalAccessError是跟ClassLoader前后不一样之类的有关的异常.
cd /D %~dp0
java -classpath "." LoadAnyClasses %~f1
)