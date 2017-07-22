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
ECHO .
PAUSE