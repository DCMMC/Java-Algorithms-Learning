package tk.dcmmc.sorting.Algorithms;

import edu.princeton.cs.algs4.StdDraw;
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Stack;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 凸包
 * 一个对Stack, Sorting的综合应用的计算几何问题.
 * 从一系列二维点中找出由其中几个点形成的多边形正好能包围所有的点, 而且形成这个凸包需要的点最少的情况.
 * 如果多个外围点构成直线, 只取端点的两个点
 * Created on 2017/8/6
 * @author DCMMC
 * @since 1.5
 */
class ConvexHull {
    /**************************************
     * Inner Class                        *
     **************************************/
    private static class Point2D {
        double x;
        double y;
        double polar;

        Point2D() {
            this.x = 0;
            this.y = 0;
        }

        Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getPolar() {
            return polar;
        }
    }

    /**************************************
     * Methods                            *
     **************************************/
    /**
     * 判断向量b->c是在向量a->b的那个方向上面
     * 使用线代的方法, 理论分析
     * @param a
     *		Point a
     * @param b
     *		Point b
     * @param c
     *		Point c
     * @return
     * 如果向量b->c是在向量a->b的逆时针反向上面就返回+1
     * 顺时针反向上: -1
     * 正好在同一直线上: 0
     */
    public static int ccw(Point2D a, Point2D b, Point2D c) {
        double area2 = (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x);

        if (area2 < 0)
            return -1;//clockwise
        else if (area2 > 0)
            return +1;//conunter-clockwise
        else
            return 0;//collinear
    }

    /**
     * 采用 the Graham scan(葛里恒扫描法)解决凸包问题
     * @param args
     *			commandline arguments
     */
    public static void main(String[] args) {
        //凸包测试样本生成器
        String outputName = "convexHullSample.txt";

        System.out.println("请在程序中长按回车停止样本生成器并开始查找凸包的点.");

        StringBuilder outputPoints = new StringBuilder();

        StdDraw.setPenRadius(0.015);
        StdDraw.setPenColor(StdDraw.RED);

        while (true) {
            if (StdDraw.mousePressed()) {
                Double x = StdDraw.mouseX();
                Double y = StdDraw.mouseY();

                outputPoints.append(x.toString()).append(" ").append(y.toString()).append("\n");
                StdDraw.point(x, y);

            }

            if (StdDraw.hasNextKeyTyped() && StdDraw.nextKeyTyped() == KeyEvent.VK_ENTER)
                break;

            //不停一下, 就会收到一堆重复的值
            StdDraw.pause(100);

        }

        StdDraw.clear();

        //输出到txt文件中
        try {
            File inFile = new File(ConvexHull.class.getResource("").toURI().getPath() + outputName);
            if (inFile.exists()) {
                if (!inFile.delete())
                    throw new IOException("createNewFile Failed.");
            }
            if (!inFile.createNewFile())
                throw new IOException("createNewFile Failed.");
        } catch (IOException ioe) {
            //IOException
            throw new RuntimeException(ioe);
        } catch (URISyntaxException ue) {
            //...
        }


        try(FileOutputStream fOut = new FileOutputStream(ConvexHull.class.getResource("")
                .toURI()
                .getPath() + outputName)) {
            for (char element : outputPoints.toString().toCharArray()) {
                fOut.write((int)element);
            }

        } catch (IOException fe) {
            //IOException
            System.err.println(fe);
        } catch (URISyntaxException ue) {
            //...
        }

        //从文件读取说有点到Point2D数组
        try {
            File pointsFile = new File(ConvexHull.class.getResource(outputName).toURI());

            if (pointsFile.exists() && pointsFile.canRead()) {
                Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(pointsFile)), "UTF-8");

                //以回车符, 空白符分隔
                sc.useDelimiter(Pattern.compile("[\\s\r\n]+"));

                DoubleLinkedList<Point2D> pointsList = new DoubleLinkedList<>();

                int cnt = 0;

                Point2D tmp = new Point2D();
                while ( sc.hasNext() ) {

                    if (++cnt == 1 || cnt % 2 != 0)
                        tmp.x = sc.nextDouble();
                    else {
                        tmp.y = sc.nextDouble();
                        pointsList.addLast(tmp);
                        tmp = new Point2D();
                    }
                }

                if (cnt % 2 != 0) {
                    System.out.println("Loading Error! 输入的数不是偶数!");
                    return;
                }

                if (pointsList.getSize() < 3) {
                    System.out.println("输入的点小于三个, 不能构二维平面!");
                    return;
                }

                Point2D[] points = pointsList.toArray();

                //找到y值最小的点, 也就是点p
                Point2D p = points[0];

                //为画板确定x, y的长度
                double scaleX = points[0].x;
                double scaleY = points[0].y;

                for (int i = 1; i < points.length; i++) {
                    if (points[i].y < p.y)
                        p = points[i];

                    if (points[i].x > scaleX)
                        scaleX = points[i].x;

                    if (points[i].y > scaleY)
                        scaleY = points[i].y;
                }


                //p在polar的排序当中一定要是最小的那一个, 所以设置为负数
                //因为别的都不可能为负数
                p.polar = -1.0;
                int index = 0;

                //计算其他所有点与p的连线以水平向右为极轴的极角
                for (Point2D i : points) {
                    if (p == i)
                        continue;

                    double polar = Math.atan( (i.y - p.y) / (i.x - p.x) );

                    i.polar = polar < 0 ? polar + Math.PI : polar;
                }

                //将这些点按照极角由小到大进行排序
                Arrays.sort(points, ((o1, o2) -> {
                    double delta = o1.polar - o2.polar;

                    if (delta < 0)
                        return -1;
                    else if (delta > 0)
                        return +1;
                    else
                        return 0;
                }));

                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setPenRadius(0.019);

                for (Point2D i : points) {
                    StdDraw.point(i.x, i.y);
                }

                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.setPenRadius(0.015);
                StdDraw.line(points[0].x, points[0].y, points[1].x, points[1].y);

                //组成凸包的点
                Stack<Point2D> hull = new Stack<>();

                //先把p和与p的连线的极角最小的点放进hull
                hull.push(points[0]);
                hull.push(points[1]);

                //用于保存最后一个凸包的点, 在最后和p点连起来
                //默认值取points[2]是为了总共只有三个点的情况
                Point2D last = points[2];

                //按照排序之后的顺序遍历后面的所有的点
                //按照逆时针反向选取三个点a, b, c, 如果矢量b->c的方向是矢量a->b的顺时针方向或者在同一直线上,
                //就把b点去掉, 然后把a点作为b点, 在从hull中pop出一个点作为a点继续遍历.
                for (int i = 2; i < points.length; i++) {
                    //三点中的中间那一个点
                    Point2D mid = hull.pop();

                    StdDraw.setPenColor(StdDraw.GRAY);
                    StdDraw.line(mid.x, mid.y, points[i].x, points[i].y);
                    StdDraw.pause(1000);

                    while (ccw(hull.peek(), mid, points[i]) <= 0) {
                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.setPenRadius(0.017);
                        StdDraw.line(mid.x, mid.y, points[i].x, points[i].y);
                        StdDraw.setPenRadius(0.015);
                        StdDraw.pause(500);

                        StdDraw.setPenColor(StdDraw.BLACK);
                        StdDraw.setPenRadius(0.019);
                        StdDraw.point(points[i].x, points[i].y);
                        StdDraw.pause(500);

                        Point2D oldMid = mid;

                        mid = hull.pop();

                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.setPenRadius(0.017);
                        StdDraw.line(mid.x, mid.y, oldMid.x, oldMid.y);
                        StdDraw.setPenRadius(0.015);
                        StdDraw.pause(500);

                        StdDraw.setPenColor(StdDraw.GRAY);
                        StdDraw.setPenRadius(0.019);
                        StdDraw.point(oldMid.x ,oldMid.y);
                        StdDraw.pause(500);

                        StdDraw.setPenColor(StdDraw.BLACK);
                        StdDraw.point(mid.x, mid.y);
                        StdDraw.pause(500);

                        StdDraw.setPenColor(StdDraw.RED);
                        StdDraw.setPenRadius(0.015);
                        StdDraw.line(mid.x, mid.y, points[i].x, points[i].y);
                        StdDraw.pause(1000);
                    }

                    hull.push(mid);
                    hull.push(points[i]);

                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.setPenRadius(0.015);
                    StdDraw.line(mid.x, mid.y, points[i].x, points[i].y);
                    StdDraw.pause(1000);
                    last = points[i];
                }

                //最后将last和p连起来
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(last.x, last.y, p.x, p.y);
                StdDraw.pause(1000);

                //输出结果
                System.out.println("答案: ");

                for (Point2D i : hull) {
                	System.out.println("(" + i.x + ", " + i.y + ")");

                }
            } else {
                //Debug...
                System.out.println("File not found....");
            }
        } catch (Exception e) {
            //Exception
            throw new RuntimeException(e);
        }
    }
}///~
