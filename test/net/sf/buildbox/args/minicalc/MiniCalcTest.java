package net.sf.buildbox.args.minicalc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import net.sf.buildbox.args.ArgsTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class MiniCalcTest {

    @Test
    public void testPlus() throws Exception {
        final String stdout = minicalc("plus", "5", "10", "3", "-2");
        Assert.assertEquals("16", stdout);
    }

    @Test
    public void testMinus() throws Exception {
        final String stdout = minicalc("minus", "37", "25");
        Assert.assertEquals("12", stdout);
    }

    @Test
    public void testLogarithm() throws Exception {
        final String stdout = minicalc("log", "--base", "10", "100");
        Assert.assertEquals("2.0", stdout);
    }

    @Test
    public void testHelp() throws Exception {
        System.setProperty("args.xml", ArgsTestUtils.getTestClassesDir() + "/minicalc.xml");
        String stderr = minicalcTrapStderr(0, "help");
        System.getProperties().remove("args.xml");
        ArgsTestUtils.filewrite("minicalc-help.txt", stderr);

        stderr = minicalcTrapStderr(0, "help", "log");
        ArgsTestUtils.filewrite("minicalc-help-log.txt", stderr);

        stderr = minicalcTrapStderr(0, "help", "plus");
        ArgsTestUtils.filewrite("minicalc-help-plus.txt", stderr);

        stderr = minicalcTrapStderr(0, "help", "minus");
        ArgsTestUtils.filewrite("minicalc-help-minus.txt", stderr);
    }

    @Test
    public void testInvalidCommandErr() throws Exception {
        final String stderr = minicalcErr("sqrt", "7");
        Assert.assertEquals("ERROR: unknown subcommand: sqrt", stderr);
    }

    @Test
    public void testNoSubcommandErr() throws Exception {
        final String stderr = minicalcErr();
        Assert.assertEquals("ERROR: no subcommand specified", stderr);
    }

    @Test
    public void testNotEnoughParamsErr() throws Exception {
        final String stderr = minicalcErr("minus", "7");
        Assert.assertEquals("ERROR: subcommand minus: not enough parameters provided", stderr);
    }

    @Test
    public void testUnparsedTokensErr() throws Exception {
        final String stderr = minicalcErr("minus", "7", "3", "extratoken");
        Assert.assertEquals("ERROR: unparsed tokens: [extratoken]", stderr);
    }

    @Test
    public void testInvalidOptionErr() throws Exception {
        final String stderr = minicalcErr("minus", "--badoption=7");
        Assert.assertEquals("ERROR: invalid option: --badoption=7", stderr);
    }

    @Test
    public void testSyntaxErr() throws Exception {
        final String stderr = minicalcErr("plus", "4", "wheel");
        Assert.assertEquals("ERROR: NumberFormatException - For input string: \"wheel\"", stderr);
    }

    /**
     * Runs {@link MiniCalc#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     * Intended for test that expect successful execution.
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
            // START SNIPPET: use-in-junit
            Assert.assertEquals(0, MiniCalc.run(args));
            // END SNIPPET: use-in-junit
            baos.flush();
            final String result = baos.toString().trim();
            System.err.println("  ==> " + result);
            return result;
        } finally {
            System.setOut(origOut);
        }
    }

    /**
     * Runs {@link MiniCalc#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     * Intended for test that expect failing execution.
     *
     * @param args arguments to pass
     * @return trapped System.err
     * @throws Exception -
     */
    private String minicalcErr(String... args) throws Exception {
        return minicalcTrapStderr(1, args);
    }

    private String minicalcTrapStderr(int requiredExitCode, String... args) throws Exception {
        // trap stdout to a string
        System.err.println("minicalc " + Arrays.toString(args));
        final PrintStream origErr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setErr(stream);
        try {
            Assert.assertEquals(requiredExitCode, MiniCalc.run(args));
            baos.flush();
            final String result = baos.toString().trim();
            System.err.println("  ==> " + result);
            return result;
        } finally {
            System.setErr(origErr);
        }
    }
}
