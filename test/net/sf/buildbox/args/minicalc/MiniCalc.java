package net.sf.buildbox.args.minicalc;

import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.annotation.Param;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.ExecutableCommand;

public class MiniCalc {

    @SubCommand(name = "plus", description = "computes sum of all given numbers")
    public static class PlusCommand implements ExecutableCommand {
        private final int[] numbers;

        public PlusCommand(int... numbers) {
            this.numbers = numbers;
        }

        public Integer call() throws Exception {
            int sum = 0;
            for (int number : numbers) {
                sum += number;
            }
            System.out.println(sum);
            return 0;
        }
    }

    @SubCommand(name = "minus", description = "computes operand1 - operand2")
    public static class MinusCommand implements ExecutableCommand {
        private final int x;
        private final int y;

        public MinusCommand(@Param("operand1") int x, @Param("operand2") int y) {
            this.x = x;
            this.y = y;
        }

        public Integer call() throws Exception {
            System.out.println(x - y);
            return 0;
        }
    }

    @SubCommand(name = "log", description = "computes logarithm of given argument")
    public static class Logarithm implements ExecutableCommand {
        private final double x;
        private double base = 0;

        public Logarithm(@Param("arg") double x) {
            this.x = x;
        }

        @Option(longName = "--base", shortName = "-b", description = "the base of the logarithm")
        public void setBase(@Param("base") double base) {
            this.base = base;
        }

        public Integer call() throws Exception {
            final double rv;
            if (base == 0) {
                // natural logarithm
                rv = Math.log(x);
            } else if (base == 10) {
                rv = Math.log10(x);
            } else {
                rv = Math.log(x) / Math.log(base);
            }
            System.out.println(rv);
            return 0;
        }
    }

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
        setup.addSubCommand(PlusCommand.class);
        setup.addSubCommand(MinusCommand.class);
        setup.addSubCommand(Logarithm.class);
        return BasicArgsParser.process(setup, args);
    }

    public static void main(String[] args) throws Exception {
        if (run(args) != 0) {
            System.exit(-1);
        }
    }
}
