package tk.dcmmc.fundamentals.Exercises;

import java.io.File;
import java.lang.reflect.*;

/**
 * class comment : an instance of Generic Type Stack
 * @author DCMMC
 * Created by DCMMC on 2017/2/28.
 */
public class StackInstance<E> {
    private int size = -1;
    private final static int MAXSIZE = 10;
    private Object[] elementData = new Object[MAXSIZE];
    StackInstance() {
        System.out.println("I don't know what the type is!");
    }
    StackInstance(Class<E> cls,Object... initargs) {
        try {
            Class<?>[] parameterTypes = new Class[initargs.length];
            for(int index = 0;index < initargs.length;index++) {
                String parametertype = initargs[index].getClass().toString();
                if(parametertype.contains("java.lang"))
                    parametertype = parametertype.replaceAll("[\\W\\w]+\\.","");
                Class<?> tmpcls;
                switch (parametertype) {
                    case "Integer"   : tmpcls = int.class;break;
                    case "Double"    : tmpcls = double.class;break;
                    case "Short"     : tmpcls = short.class;break;
                    case "Byte"      : tmpcls = byte.class;break;
                    case "Character" : tmpcls = char.class;break;
                    case "Boolean"   : tmpcls = boolean.class;break;
                    case "Float"     : tmpcls = float.class;break;
                    case "Long"      : tmpcls = long.class;break;
                    default          : tmpcls = initargs[index].getClass();
                }
                parameterTypes[index] = tmpcls;
            }
            Constructor<E> constructor = cls.getConstructor(parameterTypes);
            if(!constructor.isAccessible())
                constructor.setAccessible(true);
            int i = -1;
            while(++i < MAXSIZE)
                this.elementData[i] = constructor.newInstance(initargs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void push(E item) {
        if(getSize() < MAXSIZE)
        elementData[++size] = item;
    }

    public int getSize() {
        return size + 1;
    }

    public boolean isEmpty() {
        return size == -1;
    }

    public boolean isFull() {
        return getSize() > MAXSIZE;
    }

    @SuppressWarnings("unchecked")
    public E pop() {
        if (getSize() == -1)
            return null;
        else {
            Object tmp = elementData[size];
            elementData[size--] = null;
            return (E)tmp;
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        StackInstance stack = new StackInstance(Integer.class,0);
        for(int i = 0;i < 10;i++) {
            stack.push(i);
        }
        while(!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
        System.out.println(new Integer(0).getClass().getName());
        System.out.println(new Integer(0).TYPE.getName());
        System.out.println(Integer.class.getName());
        System.out.println(int.class.getName());
        Method me = StackInstance.class.getMethods()[0];
        System.out.println(me.toString()+" return type "+me.getGenericReturnType());

        Package[] pkgs = Package.getPackages();
        try {
            for(Package pkg : pkgs) {
                System.out.println("package : "+pkg.getName());
                Method[] methods = pkg.getClass().getDeclaredMethods();
                for(Method method : methods){
                    if(!method.isAccessible())
                        method.setAccessible(true);
                    if((method.getReturnType() == String.class || method.getReturnType().isPrimitive())
                            && method.getParameterCount() == 0)
                        System.out.println(method+" : "+method.invoke(pkg));
                }
                System.out.println("----------------------------");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}///:~
