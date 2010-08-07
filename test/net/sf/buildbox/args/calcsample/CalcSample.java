package net.sf.buildbox.args.calcsample;

import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.SingleCommandBuilder;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.ExecutableCommand;

public class CalcSample {

    @SubCommand(name = "plus")
    public static class PlusCommand implements ExecutableCommand {
        private final int[] numbers;

        public PlusCommand(int... numbers) {
            this.numbers = numbers;
        }

        public void call() throws Exception {
            int sum = 0;
            for (int number : numbers) {
                sum += number;
            }
            System.out.println(sum);
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

        public void call() throws Exception {
            System.out.println(x - y);
        }
    }

    static boolean run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("calcsample");
        setup.setSubCommands(DefaultHelpCommand.class,
                PlusCommand.class,
                MinusCommand.class);
        return SingleCommandBuilder.main(setup, args);
    }

    public static void main(String[] args) throws Exception {
        if (!run(args)) {
            System.exit(-1);
        }
    }
}
