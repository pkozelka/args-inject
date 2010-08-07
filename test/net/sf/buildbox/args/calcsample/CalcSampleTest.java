package net.sf.buildbox.args.calcsample;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import net.sf.buildbox.args.ArgsUtils;
import org.junit.Assert;
import org.junit.Test;

public class CalcSampleTest {

    @Test
    public void testPlus() throws Exception {
        final String stdout = calcsample("plus", "5", "10", "3", "2");
        Assert.assertEquals("20", stdout);
    }

    @Test
    public void testMinus() throws Exception {
        final String stdout = calcsample("minus", "37", "25");
        Assert.assertEquals("12", stdout);
    }

    @Test
    public void testTimeOffset() throws Exception {
        final String stdout = calcsample("timeoffset", "2010-10-31T14:55:01.000-0200", "768000");
        Assert.assertEquals("12", stdout);
    }

    @Test
    public void testHelp() throws Exception {
        final boolean succeeds = CalcSample.run("help");
        Assert.assertTrue(succeeds);
    }

    @Test
    public void testInvalidCommand() throws Exception {
        final boolean succeeds = CalcSample.run("sqrt", "7");
        Assert.assertFalse(succeeds);
    }

    /**
     * Runs {@link CalcSample#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     *
     * @param args arguments to pass
     * @return trapped System.out
     * @throws Exception -
     */
    private String calcsample(String... args) throws Exception {
        ArgsUtils.debugMode = true;
        // trap stdout to a string
        System.err.println("calcsample " + Arrays.toString(args));
        final PrintStream origOut = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setOut(stream);
        try {
            Assert.assertTrue(CalcSample.run(args));
            baos.flush();
            final String result = baos.toString().trim();
            System.err.println("  ==> " + result);
            return result;
        } finally {
            System.setOut(origOut);
        }
    }
}
