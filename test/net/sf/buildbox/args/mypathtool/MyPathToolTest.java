package net.sf.buildbox.args.mypathtool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import net.sf.buildbox.args.ArgsTestUtils;
import org.junit.Assert;
import org.junit.Test;

public class MyPathToolTest {

    @Test
    public void testEnvPath() throws Exception {
        final int exitCode = MyPathTool.run(System.getenv("PATH"));
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testByLines() throws Exception {
        final int exitCode = MyPathTool.run("-l", System.getProperty("java.class.path"));
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testExcludeJava() throws Exception {
        final int exitCode = MyPathTool.run("-l", System.getenv("PATH"), "--exclude", "bin/java,bin/java.exe,reboot");
        Assert.assertEquals(0, exitCode);
    }

    @Test
    public void testDedup() throws Exception {
        final File a1 = new File("/a/1").getAbsoluteFile();
        final File a2 = new File("/a/2").getAbsoluteFile();
        final File a3 = new File("/a/3").getAbsoluteFile();
        final String S = File.pathSeparator;
        final String stdout = mpt(a1 + S + a2 + S + a1 + S + a3, "--keep-missing-paths", "true");
        Assert.assertEquals(a1 + S + a2 + S + a3, stdout);
    }

    @Test
    public void testHelp() throws Exception {
        System.setProperty("args.xml", ArgsTestUtils.getTestClassesDir() + "/mpt.xml");
        final int exitCode = MyPathTool.run("help");
        System.getProperties().remove("args.xml");
        Assert.assertEquals(0, exitCode);
    }

    /**
     * Runs {@link net.sf.buildbox.args.timmy.Timmy#run} and returns its standard output as a string.
     * It imitates execution from commandline but actually does not leave JVM.
     *
     * @param args arguments to pass
     * @return trapped System.out
     * @throws Exception -
     */
    private String mpt(String... args) throws Exception {
//        ArgsUtils.debugMode = true;
        // trap stdout to a string
        System.err.println("mpt " + Arrays.toString(args));
        final PrintStream origOut = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setOut(stream);
        try {
            Assert.assertEquals(0, MyPathTool.run(args));
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
    private String mptErr(String... args) throws Exception {
//        ArgsUtils.debugMode = true;
        // trap stdout to a string
        System.err.println("mpt " + Arrays.toString(args));
        final PrintStream origErr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        System.setErr(stream);
        String result = null;
        try {
            Assert.assertEquals(1, MyPathTool.run(args));
            baos.flush();
            result = baos.toString().trim();
            return result;
        } finally {
            System.setErr(origErr);
            System.err.println("  ==> " + result);
        }
    }
}
