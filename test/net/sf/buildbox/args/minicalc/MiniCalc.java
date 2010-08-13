package net.sf.buildbox.args.minicalc;

import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;

/**
 * Sample application for <a href="http://buildbox.sf.net/args-inject">args-inject</a>
 * @author <a href="mailto:pkozelka@gmail.com">Petr Kozelka</a>
 */
public class MiniCalc {

    // START SNIPPET: run
    /**
     * Highest-possible level of invocation usable both from {@link #main} and from unit tests.
     *
     * @param args commandline arguments
     * @return true if successful
     * @throws Exception -
     */
    static int run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("minicalc");
        setup.addSubCommand(DefaultHelpCommand.class);
        setup.addSubCommand(Plus.class);
        setup.addSubCommand(Minus.class);
        setup.addSubCommand(Logarithm.class);
        return BasicArgsParser.process(setup, args);
    }
    // END SNIPPET: run

    // START SNIPPET: main
    public static void main(String[] args) throws Exception {
        final int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
    // END SNIPPET: main
}
