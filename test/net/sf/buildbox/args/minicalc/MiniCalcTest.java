package net.sf.buildbox.args.minicalc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class MiniCalcTest {

    @Test
    public void testPlus() throws Exception {
        final String stdout = minicalc("plus", "5", "10", "3", "2");
        Assert.assertEquals("20", stdout);
    }

    @Test
    public void testMinus() throws Exception {
        final String stdout = minicalc("minus", "37", "25");
        Assert.assertEquals("12", stdout);
    }

    @Test
    public void testTimeOffset() throws Exception {
        final String stdout = minicalc("timeshift", "2010-10-31T14:55:01.000", "1000");
        Assert.assertEquals("2010-10-31T14:55:02.000", stdout);
    }

    @Test
    public void testHelp() throws Exception {
        final boolean succeeds = MiniCalc.run("help");
        Assert.assertTrue(succeeds);
    }

    @Test
    public void testInvalidCommand() throws Exception {
        final boolean succeeds = MiniCalc.run("sqrt", "7");
        Assert.assertFalse(succeeds);
    }

    /**
     * Runs {@link MiniCalc#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     *
     * @param args arguments to pass
     * @return trapped System.out
     * @throws Exception -
     */
    private String minicalc(String... args) throws Exception {
//        ArgsUtils.debugMode = true;
        // trap stdout to a string
        System.err.println("minicalc " + Arrays.toString(args));
        final PrintStream origOut = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setOut(stream);
        try {
            Assert.assertTrue(MiniCalc.run(args));
            baos.flush();
            final String result = baos.toString().trim();
            System.err.println("  ==> " + result);
            return result;
        } finally {
            System.setOut(origOut);
        }
    }
}
