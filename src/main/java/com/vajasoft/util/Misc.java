package com.vajasoft.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author PUp
 */
public final class Misc {
    public static String buildToString(Object from) {
        StringBuilder buf = new StringBuilder(1024);
        Class cl = from.getClass();
        buf.append('[').append(cl.getSimpleName());
        ArrayList<Field> fields = new ArrayList<>();
        findFields(cl, fields);
        boolean firstField = true;
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (firstField) {
                    buf.append(": ");
                } else {
                    buf.append(", ");
                }
                try {
                    field.setAccessible(true);
                    Object val = field.get(from);
                    buf.append(field.getName()).append("=").append(val);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // Ignore
                }
                firstField = false;
            }
        }

        buf.append(']');
        return buf.toString();
    }

    private static void findFields(Class from, ArrayList<Field> to) {
        Class parent = from.getSuperclass();
        if (isValueObjectMarker(parent)) {
            findFields(parent, to);
        }
        to.addAll(Arrays.asList(from.getDeclaredFields()));
    }

    private static boolean isValueObjectMarker(Class cl) {
        for (Class intf : cl.getInterfaces()) {
            if (intf == ValueObjectMarker.class) {
                return true;
            }
        }
        return false;
    }
}
