package net.sf.buildbox.args.timmy;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.buildbox.args.BasicArgsParser;
import net.sf.buildbox.args.DefaultHelpCommand;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.ExecutableCommand;

/**
 * This sample code demonstrates working with input arguments of {@link Date} type.
 * Please note that processing dates is still in experimental state; the exact way of declaring such parameters can be changed in future versions.
 * This sample should serve as a reference reflecting up-to-date declaration style.
 */
public class Timmy {

    @SubCommand(name = "shift")
    public static class TimeShiftCommand implements ExecutableCommand {
        //TODO: option "unit" for offset
        private final Date basetime;
        private final long offset;
        private SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        public TimeShiftCommand(Date basetime, long offset) {
            this.basetime = basetime;
            this.offset = offset;
        }

        @Option(shortName = "-f", longName = "--output-time-format")
        public void setOutputTimeFormat(SimpleDateFormat outputTimeFormat) {
            this.outputTimeFormat = outputTimeFormat;
        }

        public void call() throws Exception {
            final String result = outputTimeFormat.format(new Date(basetime.getTime() + offset));
            System.out.println(result);
        }
    }

    @SubCommand(name = "delta")
    public static class TimeDeltaCommand implements ExecutableCommand {
        //TODO: option "unit" for output (output as decimal)
        private final Date basetime;
        private final Date otherTime;

        public TimeDeltaCommand(Date basetime, Date otherTime) {
            this.basetime = basetime;
            this.otherTime = otherTime;
        }

        public void call() throws Exception {
            final long delta = otherTime.getTime() - basetime.getTime();
            System.out.println(delta);
        }
    }

    /**
     * Highest-possible level of invocation usable both from {@link #main} and from unit tests.
     *
     * @param args commandline arguments
     * @return true if successful
     * @throws Exception -
     */
    static boolean run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("minicalc");
        setup.addSubCommand(DefaultHelpCommand.class);
        setup.addSubCommand(TimeShiftCommand.class);
        setup.addSubCommand(TimeDeltaCommand.class);
//TODO        setup.addSubCommand(LocalToUtcCommand.class);
//TODO        setup.addSubCommand(UtcToLocalCommand.class);
        return BasicArgsParser.process(setup, args);
    }

    public static void main(String[] args) throws Exception {
        if (!run(args)) {
            System.exit(-1);
        }
    }
}