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
cd /D %~dp0
REM 我也不知道为什么,必须要指定当前目录为CLASSPATH,才不会报错IllegalAccessError
REM 好像IllegalAccessError是跟ClassLoader前后不一样之类的有关的异常.
java -classpath "." LoadAnyClasses %~dnp1.class
REM cd %~dp1
REM java %~n1
)