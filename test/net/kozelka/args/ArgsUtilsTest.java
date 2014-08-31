package net.kozelka.args;

import org.junit.Test;
import org.junit.Assert;
import java.text.ParseException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class ArgsUtilsTest {
    @Test
    public void testStringToType() throws ParseException {
        Assert.assertNull("null", ArgsUtils.stringToType(null, Class.class));
        Assert.assertEquals("char", 'A', ArgsUtils.stringToType("A", char.class));
        Assert.assertEquals("boolean/false", false, ArgsUtils.stringToType("false", boolean.class));
        Assert.assertEquals("boolean/true", true, ArgsUtils.stringToType("true", boolean.class));
        Assert.assertEquals("byte", (byte) 123, ArgsUtils.stringToType("123", byte.class));
        Assert.assertEquals("short", (short) 12345, ArgsUtils.stringToType("12345", short.class));
        Assert.assertEquals("int", 1234567890, ArgsUtils.stringToType("1234567890", int.class));
        Assert.assertEquals("long", 1234567890123456789L, ArgsUtils.stringToType("1234567890123456789", long.class));
        Assert.assertEquals("float", (float) 3.141592653, ArgsUtils.stringToType("3.141592653", float.class));
        Assert.assertEquals("double", 3.141592653, ArgsUtils.stringToType("3.141592653", double.class));
        Assert.assertEquals("BigDecimal", new BigDecimal("3.14159265379"), ArgsUtils.stringToType("3.14159265379", BigDecimal.class));
        Assert.assertEquals("TimeUnit", TimeUnit.NANOSECONDS, ArgsUtils.stringToType("NANOSECONDS", TimeUnit.class));
    }

    @Test(expected = ParseException.class)
    public void testLongStringToChar() throws ParseException {
        ArgsUtils.stringToType("AB", char.class);
    }

    @Test(expected = ParseException.class)
    public void testEmptyStringToChar() throws ParseException {
        ArgsUtils.stringToType("", char.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticValueOf() throws ParseException {
        ArgsUtils.stringToType("Hello", NonStaticValueOf.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticBadTypeValueOf() throws ParseException {
        ArgsUtils.stringToType("Hello", StaticBadTypeValueOf.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testNoValueOf() throws ParseException {
        ArgsUtils.stringToType("Hello", Object.class);
    }

    @Test(expected = ParseException.class)
    public void testFailingValueOf() throws ParseException {
        ArgsUtils.stringToType("Hello", FailingValueOf.class);
    }

    @Test(expected = ParseException.class)
    public void testFailingConstructor() throws ParseException {
        ArgsUtils.stringToType("Hello", FailingConstructor.class);
    }

    public static class NonStaticValueOf {
        public NonStaticValueOf valueOf(String s) {
            throw new UnsupportedOperationException(s);
        }
    }

    public static class StaticBadTypeValueOf {
        public static String valueOf(String s) {
            throw new UnsupportedOperationException(s);
        }
    }

    public static class FailingValueOf {
        public static FailingValueOf valueOf(String s) {
            throw new UnsupportedOperationException(s);
        }
    }

    public static class FailingConstructor {
        public FailingConstructor(String s) {
            throw new UnsupportedOperationException(s);
        }
    }
}
