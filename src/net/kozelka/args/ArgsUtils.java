package net.kozelka.args;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;

public final class ArgsUtils {
    /**
     * Enables diagnostic output.
     * Mostly useful for debugging args-inject itself or the commandline application.
     */
    public static boolean debugMode = Boolean.getBoolean(System.getProperty("args.debug"));

    public static void debug(String format, Object... objects) {
        if (debugMode) {
            System.err.println("args.debug: " + String.format(format, objects));
        }
    }

    private ArgsUtils() {
        // not instantiable
    }

    /**
     * Elementary string conversion to a given type
     *
     * @param stringValue what to convert
     * @param paramType   the desired type
     * @return value as instance of desired type
     * @throws ParseException if not possible
     */
    public static Object stringToType(String stringValue, Class<?> paramType) throws ParseException {
        if (stringValue == null) {
            return null;
        }

        if (paramType.equals(String.class)) {
            return stringValue;
        }

        if (paramType.equals(boolean.class)) {
            return Boolean.valueOf(stringValue);
        }

        if (paramType.equals(short.class)) {
            return Short.valueOf(stringValue);
        }

        if (paramType.equals(int.class)) {
            return Integer.valueOf(stringValue);
        }

        if (paramType.equals(long.class)) {
            return Long.valueOf(stringValue);
        }

        if (paramType.equals(float.class)) {
            return Float.valueOf(stringValue);
        }

        if (paramType.equals(double.class)) {
            return Double.valueOf(stringValue);
        }

        if (paramType.equals(byte.class)) {
            return Byte.valueOf(stringValue);
        }

        if (paramType.equals(char.class)) {
            if (stringValue.length() != 1) {
                throw new ParseException("cannot convert string '" + stringValue + "' to char", stringValue.length() - 1);
            }
            return stringValue.charAt(0);
        }


        // try constructor with single string param
        try {
            final Constructor<?> strCon = paramType.getConstructor(String.class);
            return strCon.newInstance(stringValue);
        } catch (NoSuchMethodException e) {
            // no string constructor
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            throw new ParseException(cause.getClass().getName() + ": " + cause.getMessage(), 0);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }

        // try factory method "valueOf" with single string param
        try {
            final Method m = paramType.getMethod("valueOf", String.class);
            if (Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(paramType)) {
                return m.invoke(null, stringValue);
            }
            // ignore non-static valueOf method
        } catch (NoSuchMethodException e) {
            // no such valueOf method
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            throw new ParseException(cause.getClass().getName() + ": " + cause.getMessage(), 0);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        throw new IllegalStateException("cannot convert " + stringValue + " to " + paramType.getName());
    }

}
