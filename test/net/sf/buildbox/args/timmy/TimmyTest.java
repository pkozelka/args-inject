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
        final int exitCode = Timmy.run("help");
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testHelpShift() throws Exception {
        final int exitCode = Timmy.run("help", "shift");
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testInvalidCommand() throws Exception {
        final String stderr = timmyErr("travelto", "2048-01-01T10:15:21.000");
        Assert.assertEquals("ERROR: unknown subcommand: travelto", stderr);
    }

    @Test
    public void testOptionNotAcceptedErr() throws Exception {
        final String stderr = timmyErr("delta", "2048-01-01T10:15:21.000", "--output-time-format", "yyyy", "2048-01-21T10:15:21.000");
        Assert.assertEquals("ERROR: subcommand delta does not accept option '--output-time-format'", stderr);
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
            Assert.assertEquals(0, Timmy.run(args));
            baos.flush();
            final String result = baos.toString().trim();
            System.err.println("  ==> " + result);
            return result;
        } finally {
            System.setOut(origOut);
        }
    }

    /**
     * Runs {@link net.sf.buildbox.args.timmy.Timmy#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     * Intended for test that expect failing execution.
     *
     * @param args arguments to pass
     * @return trapped System.err
     * @throws Exception -
     */
    private String timmyErr(String... args) throws Exception {
//        ArgsUtils.debugMode = true;
        // trap stdout to a string
        System.err.println("minicalc " + Arrays.toString(args));
        final PrintStream origErr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setErr(stream);
        String result = null;
        try {
            Assert.assertEquals(1, Timmy.run(args));
            baos.flush();
            result = baos.toString().trim();
            return result;
        } finally {
            System.setErr(origErr);
            System.err.println("  ==> " + result);
        }
    }
}
