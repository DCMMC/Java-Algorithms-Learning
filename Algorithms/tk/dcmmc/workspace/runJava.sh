# Linux/macOS下的$1都是包括了扩展名了
[ -f "$1.class" ] && rm $1.class
# fullDir=$(pwd)
# fullPath=${fullDir}"/$1.class"
# for file in $1.java
for file in $1
do
# echo "Compiling "${fullPath}"/$file......"
  echo "Compiling $file......"
  javac -encoding "utf-8" $file
done
var=$1
name=${var%%.java}
#if [ -f "$1.class" ]
if [ -f ${name}".class" ]
then
  chmod 755 ${name}".class"
  echo "------Output------"
# 进入到脚本所在的目录
  cd `dirname $0`
# java -classpath "." LoadAnyClasses ${fullPath}
  java -classpath "." LoadAnyClasses ${name}".class"
else
  echo " "
fi