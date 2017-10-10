//package tk.dcmmc.workspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


/**
 * 目前存在一个问题: 用LoadAnyClasses得到的Class对象是没有包名的, 也就是getPackage()返回null
 * 解决部分: 虽然首次通过LoadAnyClasses加载会丢失包名信息, 不过如果在运行中继续加载和LoadAnyClasses
 * 首次加载的那个Class处于相同的CLASSPATH(前提是首次加载的class位于系统CLASSPATH目录树下), 那么以后
 * 加载出来的就能有包名信息了.
 * 会判断要加载的class是否位于系统CLASSPATH目录树下, 如果位于的话就用URLClassLoader加载出有包名信息的类.
 *
 * 已修复Bugs: 只要是直接java LoadAnyClasses就会出现IllegalAccessError: tried to access... 的问题,
 * 解决方式就是必须要在java命令后面指定一个classpath为".", i.e., java -classpath "." LoadAnyClasses
 * 解决: 原来是不同ClassLoader加载的类之间都是相当于不在相同包下的两个不同的类, 这样就会出现权限问题...
 * 具体参考: https://github.com/raphw/byte-buddy/issues/1
 *
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
        /*******************************************
         * Fields                                  *
         *******************************************/

        //最大容纳一个100K的class文件
        private final static int MAXSIZE = 100_000;
        //把调用本Loader的load的参数filePath记录下来
        private String lastClassPath = null;

        /*******************************************
         * Constructors                            *
         *******************************************/
        /**
         * 超类的默认构造器, 必须显式的写出来, 不然就报错
         */
        Loader() {
            super();
        }
        /**
         * 为第一次使用该ClassLoader没有lastClassPath的情况添加一个可以指定lastClassPath的构造器
         * @param lastClassPath
         *          指定的lastClassPath目录, 这里不做检验路径是否合法
         */
        Loader(String lastClassPath) {
            //调用超类默认构造器
            super();
            this.lastClassPath = lastClassPath;
        }


        /*******************************************
         * Methods                                  *
         *******************************************/

        /**
         *  根据filePath指定的文件绝对路径加载class文件
         * @param filePath
         *                  !!!NotNull!!!
         *                  class文件的完整路径, 必须在调用前确保路径存在且有权访问
         * @param className
         *                  !!!Nullable!!!
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
                this.lastClassPath = filePath;


                return defineClass(className, classByte, 0, readSize);
            } catch (IOException e) {
                throw new ClassNotFoundException("Class文件" + filePath + "导入失败, 请检查文件是否损坏", e);
            }

        }

        /**
         * load重载版本, 添加包名信息
         * @param filePath
         *                  !!!NotNull!!!
         *                  class文件的完整路径, 必须在调用前确保路径存在且有权访问
         * @param className
         *                  !!!Nullable!!!
         *                  class类的名字, 这个一般情况下直接给null就好, ClassLoader会自动根据class字节码的内容设定
         *                  className, 说过一定要手动赋予, 必须是类的全限定名称, i.e., 包名.类名
         *                  否则抛出ClassNotFoundException
         * @param packageName
         *                  包名信息, 格式类似于 tk.dcmmcc.workspace
         * @return
         *                  如果能够成功加载这个class文件, 就返回对应的Class对象引用
         * @throws ClassNotFoundException
         *                  如果导入失败, 就抛出Class
         */
        Class<?> load(String filePath, String className, String packageName) throws ClassNotFoundException {
            try {
                if (packageName != null) {
                    definePackage(packageName, null, null, null,
                            null, null, null, null);
                }
            } catch (IllegalArgumentException e) {
                //说明已经定义了该packageName的Package
            }

            return load(filePath, className);
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

            Class<?> result = null;


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
            //class的短名, e.g., tk.dcmmc.Demo 就得出 Demo, 去除最后一个.以及之前的所有字符
            String classShortName = className.replaceAll("(?m)^.+\\.", "");


            //按照本Class Loader中加载的class所在的目录在查找class
            String lastClassDir;
            try {
                if( lastClassPath != null) {
                    //获取父级Class Loader中加载的class所在的目录
                    //注意Windows下的\用正则表达式应为\\\\
                    //匹配中文英文字母数字下划线还有$的class文件名

                    lastClassDir = lastClassPath.replaceAll("(?m)"
                                    + ("\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + ""))
                                    + "[a-zA-Z0-9_\\u4e00-\\u9fa5$]+\\.class$"
                            , ("\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + "")));

                    //确定文件存在却有权访问
                    File tmpClassFile = new File(lastClassDir + classShortName+ ".class");

                    if (tmpClassFile.exists() && tmpClassFile.canRead() && tmpClassFile.canExecute()) {
                        //尝试加载并返回
                        if (className.contains(".")) {
                            String pkgName = className.replaceAll("(?m)\\.[a-zA-Z0-9_\\u4e00-\\u9fa5$]+$", "");

                            result = load(tmpClassFile.getAbsolutePath(), className, pkgName);
                        } else
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
            String classpath;

            //md要是没找到的是直接抛异常算了
            if ((classpath = locatedClasspath(lastClassPath)) == null)
                throw new ClassNotFoundException(className);

            File tmpClassFile;

            //获得CLASSPATH后, 先尝试直接从当前找到的CLASSPATH + classPathByName加载
            try {
                //先判断是不是有这个文件存在.
                tmpClassFile = new File(classpath + File.separatorChar + classPathByName + ".class");

                //检查权限和是否存在
                if (tmpClassFile.exists() && tmpClassFile.canRead() && tmpClassFile.canExecute()) {
                    //注意不同ClassLoader就算加载相同的class, 这两个class都是互相视为不在一个包的权限, 也就是
                    //只能范围public的东西.... 别看了,下面注释的都是辣鸡代码

                    //URL作者推荐先toURI再toURL
                    //URL classpathURL = new File(classpath).toURI().toURL();
                    //获得当前Launcher调用的AppClassLoader
                    //URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                    //Class urlClass = URLClassLoader.class;

                    //因为URLClassLoader中的addURL是protected的, 所以需要用到反射获取权限
                    //@SuppressWarnings("unchecked")
                    //Method method = urlClass.getDeclaredMethod("addURL", URL.class);
                    //设置权限
                    //method.setAccessible(true);
                    //method.invoke(urlClassLoader, classpathURL);

                    //如果className使用了全限定名称, 就尝试用当前获取到的URLClassLoader加载class(使用全限定名称)
                    //if (!className.equals(classShortName)) {
                    //    //Debug...
                    //    System.out.print("Debug... 使用URLClassLoader加载");
                    //    result = urlClassLoader.loadClass(className);

                    //    if (result != null) {
                    //       return result;
                    //   }
                    //}

                    //如果不行的话, 还是继续调用load()


                    result = load(tmpClassFile.getAbsolutePath(), null,
                            className.replace(classShortName, "").replaceAll("\\.", ""));
                }

                //看看是否成功得到了result.
                if (result != null)
                    return result;

            } catch (Exception e) {
                //那就是还是没找到呗... =,=
            }

            try {
                //在从别的CLASSPATH中试试
                //获取当前系统下的所有CLASSPATH
                String classpathAll = System.getenv().get("CLASSPATH");
                //解析CLASSPATH
                classpathAll = classpathAll.replaceFirst("(?m)^.", "")
                        .replaceAll(";\\.;", ";")
                        .replaceFirst("(?m);.$", ";");

                String[] classpathAllInArr = classpathAll.split(";");

                for (String s : classpathAllInArr) {
                    if (!s.equals(classpath)) {
                        tmpClassFile = new File(s + File.separatorChar + classPathByName + ".class");

                        //检查权限和是否存在
                        if (tmpClassFile.exists() && tmpClassFile.canRead() && tmpClassFile.canExecute()) {
                            //Add CLASSPATH
                            //method.invoke(urlClassLoader, new File(s).toURI().toURL());
                            //尝试用URLClassLoader加载, 再尝试用load加载
                            //if ((result = urlClassLoader.loadClass(className)) != null)
                            //    return result;
                            //else
                            if ((result = load(tmpClassFile.getAbsolutePath(), null,
                                    className.replace(classShortName, "").replaceAll("\\.", ""))) != null)
                                return result;
                        }
                    }
                }
            } catch (Exception e) {
                //那就是从别的classpath通过className给定的全限定名没有找到, 这说明使用者对class文件的包名和位置放置不规范
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
                                                .equals(classShortName + ".class"))
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

        /**
         * 由DCMMC覆盖实现Finds the resource with the given name.
         * 这样用LoadAnyClass加载的class文件才能找到Resource目录.
         *
         * @param  name
         *         The resource name
         *
         * @return  A <tt>URL</tt> object for reading the resource, or
         *          <tt>null</tt> if the resource could not be found
         *
         * @since  1.2
         */
        @Override
        protected URL findResource(String name) {
            //先找上一个被加载的class所在的CLASSPATH
            String classpath = locatedClasspath(lastClassPath);


            //从最近的那一个被加载的class所在的classpath找找
            if (classpath != null) {
                File fileInCurrentClasspath = new File(
                        classpath.charAt(classpath.length() - 1) == File.separatorChar
                                ? (classpath + name) : (classpath + File.separator + name));

                //如果所在的classpath+name找不到该文件, 就从lastClass所在的目录找该文件
                if (!fileInCurrentClasspath.exists())
                    fileInCurrentClasspath = new File(lastClassPath.replaceAll("(?m)"
                                    + ("\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + ""))
                                    + "[a-zA-Z0-9_\\u4e00-\\u9fa5$]+\\.class$"
                            , ("\\".equals(File.separatorChar + "") ? "\\\\" : (File.separatorChar + ""))) + name);

                try {
                    if (fileInCurrentClasspath.exists())
                        return fileInCurrentClasspath.toURI().toURL();
                } catch (MalformedURLException me) {
                    //MalformedURLException...
                    //无视它?.
                }

            }

            //在从别的CLASSPATH中试试
            //获取当前系统下的所有CLASSPATH
            String classpathAll = System.getenv().get("CLASSPATH");
            //解析CLASSPATH
            classpathAll = classpathAll.replaceFirst("(?m)^\\.;", "")
                    .replaceAll(";\\.;", ";")
                    .replaceFirst("(?m);\\.$", ";");

            String[] classpathAllInArr = classpathAll.split(";");

            for (String s : classpathAllInArr) {
                if (!s.equals(classpath)) {
                    File tmpNameFile;

                    try {
                        if ((tmpNameFile = new File( s.charAt(s.length() - 1) == File.separatorChar
                                ? (s + name) : (s + File.separator + name))).exists())
                            return tmpNameFile.toURI().toURL();
                    } catch (MalformedURLException me) {
                        //MalformedURLException...
                        //无视它?.
                    }

                }
            }

            return null;
        }

        /**
         * 判断目录是否属于系统CLASSPATH下, 如果是就返回所在的那个CLASSPATH
         * @param filePath
         *          一个存在的路径
         * @return 所在的系统CLASSPATH, 不存在就返回null
         */
        private static String locatedClasspath(String filePath) {
            //判断下有没有这个路径存在
            if (! (new File(filePath).exists()))
                return null;

            //获取当前系统下的所有CLASSPATH
            String classpathAll = System.getenv().get("CLASSPATH");
            //解析CLASSPATH
            classpathAll = classpathAll.replaceFirst("(?m)^.", "")
                    .replaceAll(";\\.;", ";")
                    .replaceFirst("(?m);.$", ";");

            String[] classpathAllInArr = classpathAll.split(";");
            String classpath = null;
            //遍历找到匹配当前classLoaded所在的的CLASSPATH
            for (String singlePath : classpathAllInArr) {
                //Debug...
                //String path = getResource("").toURI().getPath().substring(1);

                //记得去掉getPath之后得到的路径前面出了个/号的情况

                if (!singlePath.equals("") &&
                        filePath.contains(singlePath)) {
                    classpath = singlePath;
                    //找到这个CLASSPATH就跳出循环
                    break;
                }


            }

            return classpath;
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

                //校验文件是否存在以及权限
                File fileFromArg = new File(args[0]);
                if (!fileFromArg.exists() || !fileFromArg.canRead() || !fileFromArg.canExecute())
                    throw new FileNotFoundException(args[0]);

                //目标Class
                Class<?> cls;

                //判断是否位于系统CLASSPATH下
                String classpath = null;
                try {
                    classpath = Loader.locatedClasspath(args[0]);
                } catch (NullPointerException ne) {
                    //...
                    //就是空的classpath吧
                }

                if (classpath != null) {
                    String classFullName =
                            args[0].replace(classpath.charAt(classpath.length() - 1) == File.separatorChar
                                    ? classpath : classpath.concat(File.separator), "");
                    classFullName = classFullName.replaceAll(File.separatorChar == '\\'
                            ? "\\\\" : File.separator, ".");
                    classFullName = classFullName.replaceAll("(?m)\\.class$", "");

                    try {
                        cls = new Loader(args[0]).findClass(classFullName);
                    } catch (ClassNotFoundException | NoClassDefFoundError nce) {
                        //那就是在Classpath下不过没有包名的class文件
                        //第二个参数直接为null就好了, ClassLoader自动得出className
                        cls = new Loader().load(args[0], null);
                    }

                } else {
                    //第二个参数直接为null就好了, ClassLoader自动得出className
                    cls = new Loader().load(args[0], null);
                }

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
                throw new RuntimeException("找不到指定的Class：" + args[0]);
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
                "E:\\DCMMC\\Java\\Java\\Algorithms\\tk\\dcmmc\\fundamentals\\Exercises\\AnalysisOfAlgorithms.class";

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

