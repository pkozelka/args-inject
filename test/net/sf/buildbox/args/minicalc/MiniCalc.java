package net.sf.buildbox.args.minicalc;

import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.ExecutableCommand;

public class MiniCalc {

    @SubCommand(name = "plus")
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

    @SubCommand(name = "minus")
    public static class MinusCommand implements ExecutableCommand {
        private final int x;
        private final int y;

        public MinusCommand(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Integer call() throws Exception {
            System.out.println(x - y);
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
        return BasicArgsParser.process(setup, args);
    }

    public static void main(String[] args) throws Exception {
        if (run(args) != 0) {
            System.exit(-1);
        }
    }
}
