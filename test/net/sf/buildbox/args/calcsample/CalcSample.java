package net.sf.buildbox.args.calcsample;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.annotation.Param;
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

    @SubCommand(name = "timeoffset")
    public static class TimeOffsetCommand implements ExecutableCommand {
        private final Date basetime;
        private final long offset;
        private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        public TimeOffsetCommand(@Param(format = "") Date basetime, long offset) {
            this.basetime = basetime;
            this.offset = offset;
        }

        public void call() throws Exception {
            final String result = timeFormat.format(new Date(basetime.getTime() + offset));
            System.out.println(result);
        }

        @Option(shortName = "-f", longName = "--time-format")
        public void setTimeFormat(SimpleDateFormat timeFormat) {
            this.timeFormat = timeFormat;
        }
    }

    static boolean run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("calcsample");
        setup.addSubCommand(DefaultHelpCommand.class);
        setup.addSubCommand(PlusCommand.class);
        setup.addSubCommand(MinusCommand.class);
        setup.addSubCommand(TimeOffsetCommand.class);
        return BasicArgsParser.process(setup, args);
    }

    public static void main(String[] args) throws Exception {
        if (!run(args)) {
            System.exit(-1);
        }
    }
}
