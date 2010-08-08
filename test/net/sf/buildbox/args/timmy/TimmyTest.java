package net.sf.buildbox.args.timmy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class TimmyTest {

    @Test
    public void testTimeOffset() throws Exception {
        final String stdout = timmy("shift", "2010-10-21T14:55:01.000", "1000");
        Assert.assertEquals("2010-10-21T14:55:02.000", stdout);
    }

    @Test
    public void testTimeDelta() throws Exception {
        final String stdout = timmy("delta", "2010-10-20T14:55:01.000", "2010-10-21T14:55:01.000");
        Assert.assertEquals("86400000", stdout);
    }

    @Test
    public void testHelp() throws Exception {
        final boolean succeeds = Timmy.run("help");
        Assert.assertTrue(succeeds);
    }

    @Test
    public void testInvalidCommand() throws Exception {
        final boolean succeeds = Timmy.run("travelto", "2048-01-01T10:15:21.000");
        Assert.assertFalse(succeeds);
    }

    /**
     * Runs {@link net.sf.buildbox.args.timmy.Timmy#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     *
     * @param args arguments to pass
     * @return trapped System.out
     * @throws Exception -
     */
    private String timmy(String... args) throws Exception {
//        ArgsUtils.debugMode = true;
        // trap stdout to a string
        System.err.println("timmy " + Arrays.toString(args));
        final PrintStream origOut = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setOut(stream);
        try {
            Assert.assertTrue(Timmy.run(args));
            baos.flush();
            final String result = baos.toString().trim();
            System.err.println("  ==> " + result);
            return result;
        } finally {
            System.setOut(origOut);
        }
    }
}