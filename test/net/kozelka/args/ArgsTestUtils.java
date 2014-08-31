package net.kozelka.args;

import java.io.*;
import java.net.URL;
import java.util.concurrent.Callable;
import org.junit.Assert;

public class ArgsTestUtils {
    public static void filewrite(String filename, String text) throws IOException {
        final File dir = getTestClassesDir();
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

    public static File getTestClassesDir() {
        final URL url = ArgsTestUtils.class.getResource("/ref.txt");
        Assert.assertNotNull("Failed to find resource ref.txt", url);
        return new File(url.getPath()).getParentFile();
    }

    public static int trapStandardOutputs(StringBuilder stdout, StringBuilder stderr, Callable<Integer> callable) throws Exception {
        final PrintStream origOut = System.out;
        final PrintStream origErr = System.err;
        final ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
        final ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
        if (stdout != null) {
            System.setOut(new PrintStream(outBaos));
        }
        if (stderr != null) {
            System.setErr(new PrintStream(errBaos));
        }
        try {
            return callable.call();
        } finally {
            if (stdout != null) {
                outBaos.flush();
                stdout.append(outBaos.toString().trim());
                System.setOut(origOut);
            }
            if (stderr != null) {
                errBaos.flush();
                stderr.append(errBaos.toString().trim());
                System.setErr(origErr);
            }
        }
    }

}
