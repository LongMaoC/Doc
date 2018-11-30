package com.cxy.doc.util;


import java.util.Iterator;


/**
 * Created by CXY on 2016/11/8.
 */

public class L<T> {
    private static String TAG = "CXY";
    static {

    }

    public synchronized static <T> void e(T msg) {
        StringBuffer sb = new StringBuffer();
        if (msg == null) {
            sb.append("null!!!");
            System.out.println(sb.toString());
            return;
        }

        sb.append("------------------------------------------------------------------------");
        sb.append("\n" + getTargetStackTraceElement().toString() + "\n");
        if (msg instanceof Number || msg instanceof Boolean || msg instanceof String) {
            sb.append(String.valueOf(msg) + "\n");
        } else if (msg instanceof Iterable) {
            Iterator iterator = ((Iterable) msg).iterator();
            int index = 0 ;
            while (iterator.hasNext()) {
                sb.append(iterator.next() + "\n");
                index++ ;
            }
            sb.append("共"+index+"条数据\n");
        } else {
            sb.append(msg.toString() + "\n");
        }
        sb.append("------------------------------------------------------------------------\n");
        System.out.println(sb.toString());
    }

    public synchronized static <T> void ee(T msg) {
        StringBuffer sb = new StringBuffer();
        if (msg == null) {
            sb.append("null!!!");
            print(sb);
            return;
        }
        if (msg instanceof Number || msg instanceof Boolean || msg instanceof String) {
            sb.append(String.valueOf(msg) + "\n");
        } else if (msg instanceof Iterable) {
            Iterator iterator = ((Iterable) msg).iterator();
            int index = 0 ;
            while (iterator.hasNext()) {
                sb.append(iterator.next() + "\n");
                index++ ;
            }
            sb.append("共"+index+"条数据\n");
        } else {
            sb.append(msg.toString() + "\n");
        }
        print(sb);
    }
    private static void print(StringBuffer sb){
        System.out.println(sb.toString());
    }

    private static StackTraceElement getTargetStackTraceElement() {
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
