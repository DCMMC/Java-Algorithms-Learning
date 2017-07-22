package com.dcmmc.workspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


/**
 * 尝试从任意给定的class文件加载Class
 * 这样就可以用java command-line运行class的时候, 还要cd到类的一级包名的上层目录
 * 在用java指定类的全限定名称来运行类了
 * Created by DCMMC on 2017/7/21.
 * @since 1.7
 */
public class LoadAnyClasses {
    /**********************************************************************
     * 我自己Custom的一个ClassLoader, 可以用任意文件的class加载Class对象     *
     * 并且对有import或外部依赖的class, 自动搜索依赖他的那个class文件所在的   *
     * CLASSPATH目录, 然后自动加载.                                        *
     * 这里的原理就是当ClassLoader加载class的时候, 如果用系统加载器没有找到的 *
     * 话, 最终会调用findClass(String name), 只需要我自己实现一个findClass就 *
     * 可以实现从任意位置(本地文件系统, 外置硬盘, 互联网)上加载字节码, 并且还可 *
     * 实现对字节码的校验.                                                  *
     * ********************************************************************/
    /**
     * @author DCMMC
     * Create on 2017/7/21
     */
    private static class Loader extends ClassLoader {

        //最大容纳一个100K的class文件
        private final static int MAXSIZE = 100_000;
        //把调用本Loader的load的参数filePath记录下来
        private String classPath = null;

        /**
         *  根据filePath指定的文件绝对路径加载class文件
         * 注意: 必须在运行LoadAnyClasses时指定classpath为".", 否则可能运行的时候会抛出
         * IllegalAccessException, 暂时我也不知道为什么.
         * i.e., java -classpath "." LoadAnyClasses
         * @param filePath
         *                  !!!NotNull!!!
         *                  class文件的完整路径, 必须在调用前确保路径存在且有权访问
         * @param className
         *                  Nullable
         *                  class类的名字, 这个一般情况下直接给null就好, ClassLoader会自动根据class字节码的内容设定
         *                  className, 说过一定要手动赋予, 必须是类的全限定名称, i.e., 包名.类名
         *                  否则抛出ClassNotFoundException
         * @return
         *                  如果能够成功加载这个class文件, 就返回对应的Class对象引用
         * @throws ClassNotFoundException
         *                  如果导入失败, 就抛出Class
         */
        Class<?> load(String filePath, String className) throws ClassNotFoundException {

            Class<?> cTmp ;
            //查看class是否已经被加载了
            if((cTmp = this.findLoadedClass(className)) != null){
                //System.out.println("class文件已经被加载了");
                return cTmp;
            }

            //读取class文件到字节数组中
            try (FileInputStream in = new FileInputStream(filePath)) {
                byte[] classByte;
                int readSize;
                classByte = new byte[MAXSIZE];

                readSize = in.read(classByte);
                //System.out.println("Class文件" + filePath + "导入成功, readSize: " + readSize + "bytes.");
                in.close();

                //找到class文件成功, 记录下来
                this.classPath = filePath;


                return defineClass(className, classByte, 0, readSize);
            } catch (IOException e) {
                throw new ClassNotFoundException("Class文件" + filePath + "导入失败, 请检查文件是否损坏", e);
            }

        }

        /**
         *  由DCMMC实现的findClass方法
         * @param className
         *          class文件的名字
         * @return
         *          如果能够成功加载这个class文件, 就返回对应的Class对象引用
         * @throws ClassNotFoundException
         *          如果导入失败, 就抛出Class
         */
        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {

            Class<?> result;


            //尝试从system class loader中获取class
            try {
                if ( (result = findSystemClass(className)) != null) {
                    return result;
                }
            } catch (Exception e) {
                //不管他
                //System.out.println("在SystemClass中没有找到" + className);
            }

            //尝试用Application ClassLoader加载, 这里的className要是全限定名称
            try {
                //Debug...
                //String resourcePath = ClassLoader.getSystemClassLoader().getResource("").toURI().getPath();

                result = ClassLoader.getSystemClassLoader().loadClass(className);

                if (result != null)
                    return result;
                else
                    throw new ClassNotFoundException(className);
            } catch (Exception e) {
                //用系统ClassLoader加载失败

                //Debug...
                //System.out.println(e);
            }

            /*******************************************
             * 从这里开始, 都是不需要全限定className了,   *
             * 只需要类名就行.                           *
             *******************************************/

            //首先整理一下className, 把所有的类名里面的.换成当前文件系统的路径分隔符,
            // e.g., tk.dcmmc.Demo -> tk\dcmmc\Demo (in Windows)
            String classPathByName = className.replaceAll("\\.",
                    "\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + ""));
            //class的短名, e.g., tk.dcmmc.Demo 就得出 Demo, 去除最后一个.以及之前的所有字符(中文英文下划线数字)
            String classShortName = className.replaceAll("(?m)^([a-zA-Z0-9_\\u4e00-\\u9fa5]+\\.)*", "");



            //按照本Class Loader中加载的class所在的目录在查找class
            String lastClassDir;
            try {
                if( classPath != null) {
                    //获取父级Class Loader中加载的class所在的目录
                    //注意Windows下的\用正则表达式应为\\\\
                    //匹配中文英文字母数字下划线
                    lastClassDir = classPath.replaceAll("(?m)"
                                    + ("\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + ""))
                                    + "[a-zA-Z0-9_\\u4e00-\\u9fa5]+\\.class$"
                            , ("\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + "")));

                    //确定文件存在却有权访问
                    File tmpClassFile = new File(lastClassDir + classShortName+ ".class");

                    if (tmpClassFile.exists() && tmpClassFile.canRead() && tmpClassFile.canExecute()) {
                        //尝试加载并返回
                        result = load(tmpClassFile.getAbsolutePath(), className);

                        if (result != null)
                            return result;
                    } else
                        throw new FileNotFoundException(tmpClassFile.getAbsolutePath() + "不存在或无权访问!");

                }

            } catch (Exception e) {
                //说明只是没在这里找到嘛, 不是啥大事

                //Debug...
                //System.out.println(lastClassDir + "下没有找到或没有成功加载" + className );
                //System.out.println(e);
            }


            //先找上一个被加载的class所在的CLASSPATH
            //获取当前系统下的所有CLASSPATH
            String classpathAll = System.getenv().get("CLASSPATH");
            //解析CLASSPATH
            classpathAll = classpathAll.replaceFirst("(?m)^.", "");
            classpathAll = classpathAll.replaceAll(";.;", ";");
            classpathAll = classpathAll.replaceFirst("(?m);.$", ";");

            String[] classpathAllInArr = classpathAll.split(";");
            String classpath = null;
            //遍历找到匹配当前classLoaded所在的的CLASSPATH
            for (String singlePath : classpathAllInArr) {
                //Debug...
                //String path = getResource("").toURI().getPath().substring(1);

                //记得去掉getPath之后得到的路径前面出了个/号的情况

                if (!singlePath.equals("") &&
                        classPath.contains(singlePath))
                {
                    classpath = singlePath;
                    //找到这个CLASSPATH就跳出循环
                    break;
                }

            }

            //md要是没找到的是直接抛异常算了
            if (classpath == null)
                throw new ClassNotFoundException(className);


            //获得CLASSPATH后, 先尝试直接从当前找到的CLASSPATH + classPathByName加载
            try {
                //先判断是不是有这个文件存在.
                File tmpClassFile = new File(classpath + File.separatorChar + classPathByName + ".class");

                //检查权限和是否存在
                if (tmpClassFile.exists() && tmpClassFile.canRead() && tmpClassFile.canExecute()) {
                    result = load(tmpClassFile.getAbsolutePath(), null);

                    //看看是否成功得到了result.
                    if (result != null)
                        return result;
                } else
                    throw new FileNotFoundException(tmpClassFile.getAbsolutePath() + "不存在或无权访问!");
            } catch (Exception e) {
                //那就是还是没找到呗... =,=
            }



            //尝试从依赖这个要加载的class的class的CLASSPATH中遍历(使用NIO.2的特性)找到class
            try {

                //Debug...
                //System.out.println("找到classpath: " + classpath);

                //不用这个了......
                //获取当前Class Loader的CLASSPATH, getResource返回的路径莫名其妙前面多了个'/', 要记得去掉
                //直接getFile或者toPath会导致空格转义成"%20", 需要在前面加一个toURI()
                //String classpath = classLoaded.getResource("").toURI().getPath().substring(1);


                //设置一个存储匿名内部类中生成的Class的内部类, 只能用成员内部类实现, 因为下面遍历使用的匿名内部类
                //不允许更改外部变量
                class StoredClass {
                    //初始化为null
                    Class<?> storedCls = null;
                }
                StoredClass storedClass = new StoredClass();

                //Debug...
                //classpath = "E:\\DCMMC\\Java\\Java\\Algorithms\\tk\\dcmmc\\fundamentals\\Exercises";
                //$在正则表达式中需要转义一下
                //String classNameNew = className.replace("$", "\\\\$");

                Files.walkFileTree(Paths.get(classpath),
                        //A simple anonymous implement of FileVisit to view file tree
                        new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes)
                                    throws IOException
                            {
                                //Debug...
                                //System.out.println(path);


                                //如果文件匹配, 就尝试load, 成功就退出遍历, 失败就继续遍历
                                String currentClassPath;
                                if ( path.getFileName() != null &&
                                        path.toFile()
                                                .getName()
                                                .contains(classShortName + ".class"))
                                {
                                    //Debug...
                                    //System.out.println("找到文件! " + path);

                                    currentClassPath = path.toFile().getAbsolutePath();
                                    try {
                                        Class<?> tmpResultCls = load(currentClassPath, null);
                                        if ( tmpResultCls != null) {
                                            //如果成功加载了class, 就存储在storedClass中
                                            storedClass.storedCls = tmpResultCls;
                                            return FileVisitResult.TERMINATE;
                                        }

                                    } catch (ClassNotFoundException e) {
                                        //这个失败了就继续
                                        return FileVisitResult.CONTINUE;
                                    }

                                }

                                //没找到名为className的文件, 继续遍历
                                return FileVisitResult.CONTINUE;
                            }
                        });

                //判断storedClass是否已经取得了Class对象, 否则抛出异常, 这是最后一个尝试了
                if (storedClass.storedCls == null)
                    throw new ClassNotFoundException(className);
                else
                    return storedClass.storedCls;

            } catch (NullPointerException ne) {
               //getResource()竟然都抛出了NullPointerException, 重抛吧... =,=
                throw ne;
            }  catch (Exception e) {
                //都不成功, 只能抛出异常了 =,=
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Test Client.
     * 记住: 不能自己加载自己, 这样就是个死循环了...
     * @param args
     *              command-line arguments.
     *              无参数时为Test程序, 有参数是第一个参数作为要运行的class的文件路径, 后面的参数为调用main
     *              方法的参数.
     * @throws RuntimeException
     *              如果参数指定的文件不存在或者权限不足, 就抛出异常
     *
     */
    public static void main(String[] args) throws RuntimeException, NoSuchMethodException {
        //如果无参数或者参数不为1, 就执行Test
        if (args.length == 0) {
            whenNoneArgs();
        } else {
            //先判断是不是就是加载LoadAnyClasses...
            if(args[0].contains("LoadAnyClasses")) {
                System.out.println("这样会导致LoadAnyClass循环自我加载!!! 已拦截...");
                whenNoneArgs();
                //退出
                return;
            }
            System.out.println("正在尝试从" + args[0] + "中加载主类...");

            //当参数>=1时, 从args[0]指定的文件路径载入class
            //从args[0]获取文件名
            try {
                //检查是否是是class文件
                if (!args[0].contains(".class"))
                    throw new ClassNotFoundException(args[0]);

                //校验文件是否存在
                File fileFromArg = new File(args[0]);
                if (!fileFromArg.exists() || !fileFromArg.canRead() || !fileFromArg.canExecute())
                    throw new FileNotFoundException(args[0]);

                //第二个参数直接为null就好了, ClassLoader自动得出className
                Class<?> cls = new Loader().load(args[0], null);
                Method mainMethod = cls.getMethod("main", String[].class);

                //尝试获取权限
                if (!mainMethod.isAccessible()) {
                    mainMethod.setAccessible(true);
                }
                if (args.length == 1)
                    mainMethod.invoke(null, new Object[]{new String[]{}});
                else {
                    String[] instanceArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, instanceArgs, 0, args.length - 1);
                    mainMethod.invoke(null, (Object)instanceArgs);
                }

            } catch (IOException ioe) {
                throw new RuntimeException("参数指定的文件不存在或者权限不足!", ioe);
            } catch (ClassNotFoundException ce) {
                throw new RuntimeException("找不到指定的Class!");
            } catch (Exception e) {
                //重抛
                System.out.println("发生了一些意想不到的异常...");
                throw new RuntimeException(e);
            }
        }




    }

    /**
     * 当main方法的参数为0个的时候执行
     */
    private static void whenNoneArgs() {
        String classFilePath =
                "E:\\DCMMC\\Java\\Java\\Algorithms\\tk\\dcmmc\\fundamentals\\Exercises\\BasicProgModel.class";

        Class<?> cls;
        try {
            //className为null, 让ClassLoader自己通过class字节码确定className, 如果一定要给className
            //的话, className必须用全限定名称 i.e., 包名.类名
            cls = new Loader().load(classFilePath, null);

            //获得cls的main方法(Entry Point)
            //可变长参数其实就是一个数组, 所以直接用String[]数组的class就能表示String
            Method mainMethod = cls.getMethod("main", String[].class);

            //尝试调用main方法
            /******************************************************************
             * 因为invoke(obj, Object... args)是可变长参数, 如果直接传递null,  *
             * 就相当于传递了0个参数过去, 可是main接收的是String[]作为参数.     *
             * 或者如果直接传递new String[]{} (这样new出来的是空的), 向上转型到 *
             * Object[]之后, 这个Object[]也是空的, 也就是length为0, 这样表示的是*
             * 无参数.                                                        *
             * 上述两种情况都会报错 wrong numbers of arguments.                *
             *****************************************************************/
            //尝试获取权限
            if (!mainMethod.isAccessible()) {
                mainMethod.setAccessible(true);
            }
            mainMethod.invoke(null, new Object[]{new String[]{}});

            /******************************************************************
             * 对于可变长参数, 也是有坑的.                                      *
             * 首先可变长参数确实是用(引用)数组实现的, 所以可以直接传递类似于      *
             * String[] args的引用数组来充当args.length个参数, 然而对于基本类型  *
             * 数组, e.g., int[], java会把int[]是为一个对象, 毕竟整个数组是直接用 *
             * new开辟出来的, 所以传递过去只相当于一个参数.                       *
             * 其次就是因为反射是java在Runtime时期的行为, 又在Runtime时期, 可变长 *
             * 已经包装为了引用数组, 所以只相当于一个参数, 而不是在Compile时期的多 *
             * 个参数了, 所以invoke(obj, Object... args)中的args只能接收一个元素 *
             * 的引用数组, 所以如果在源码中直接将一个引用数组strs作为args传递过去, *
             * invoke就会当作strs.length个参数, 结果只把strs[0]传递到invoke目标  *
             * 方法的第一个参数, 而strs其他元素则被视为多出来的参数而在Runtime期,  *
             * invoke的目标方法中的可变长参数类型早就已经变成了一个参数(i.e. 一个  *
             * 引用数组), 这时候报错 wrong numbers of arguments.                *
             * 解决方法: 用new Object[]()包装起来, 或者将数组用(Object)强制转化为 *
             * 一个只有一个元素的对象.                                           *
             *******************************************************************/


            //只有通过getDeclaredMethods()才能看到private的Methods.
            //不能直接用Class.newInstance()获得对象, 这样会报错:
            //IllegalAccessException: Can not call newInstance() on the Class for java.lang.Class
            //最好用Class.getConstructor(parameters)来获得构造器, 然后获得权限,
            //最后用Constructor.newInstance()获得实例对象

            Constructor<LoadAnyClasses> constructor = LoadAnyClasses.class.getConstructor();
            if(!constructor.isAccessible())
                constructor.setAccessible(true);

            LoadAnyClasses.class.getDeclaredMethod("varargsFoo", String[].class)
                .invoke(constructor.newInstance(),
                            new Object[]{new String[]{"Hello", "World"}});


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用来测试可变长参数的invoke
     * 因为反射在运行时才生效, 所以对于反射来说String... args和String[] args一模一样
     * 所以要尽量避免方法重载只有诸如String... args和String[] args的区别这样的情况
     * @param args
     *             可变长参数
     */
    private void varargsFoo(String... args) {
        System.out.println("收到了" + args.length + "个参数");
    }

}///~
