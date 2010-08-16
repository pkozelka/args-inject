package net.sf.buildbox.args;

import java.io.*;
import java.net.URL;
import java.util.concurrent.Callable;
import org.junit.Assert;

public class ArgsTestUtils {
    public static void filewrite(String filename, String text) throws IOException {
        final URL url = ArgsTestUtils.class.getResource("/ref.txt");
        Assert.assertNotNull("Failed to find resource ref.txt", url);
        final File dir = new File(url.getPath()).getParentFile();
        final File of = new File(dir, filename);
        final Writer w = new FileWriter(of);
        System.out.println("Writing to " + of);
        try {
            w.write(text);
            System.out.println(text);
        } finally {
            w.close();
        }
    }

    public static int trapStandardOutputs(StringBuilder stdout, StringBuilder stderr, Callable<Integer> callable) throws Exception {
        final PrintStream origOut = System.out;
        final ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBaos));
        final PrintStream origErr = System.err;
        final ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errBaos));
        try {
            final int exitCode = callable.call();
            outBaos.flush();
            stdout.append(outBaos.toString().trim());
            errBaos.flush();
            stderr.append(errBaos.toString().trim());
            return exitCode;
        } finally {
            System.setOut(origOut);
            System.setErr(origErr);
        }
    }

}
