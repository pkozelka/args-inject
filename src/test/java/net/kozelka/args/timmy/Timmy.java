package net.kozelka.args.timmy;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.kozelka.args.BasicArgsParser;
import net.kozelka.args.DefaultHelpCommand;
import net.kozelka.args.annotation.AnnottationAwareSetup;
import net.kozelka.args.annotation.Option;
import net.kozelka.args.annotation.Param;
import net.kozelka.args.annotation.SubCommand;
import net.kozelka.args.api.ArgsCommand;

/**
 * This sample code demonstrates working with input arguments of {@link Date} type.
 * Please note that processing dates is still in experimental state; the exact way of declaring such parameters can be changed in future versions.
 * This sample should serve as a reference reflecting up-to-date declaration style.
 */
public class Timmy {

    @SubCommand(name = "shift", description = "shifts time by given milliseconds")
    public static class TimeShiftCommand implements ArgsCommand {
        //TODO: option "unit" for offset
        private final Date basetime;
        private final long offset;
        private SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        public TimeShiftCommand(@Param("basetime") Date basetime, @Param("offset") long offset) {
            this.basetime = basetime;
            this.offset = offset;
        }

        @Option(shortName = "-f", longName = "--output-time-format", description = "result format spec. (SimpleDateFormat)")
        public void setOutputTimeFormat(@Param("format") SimpleDateFormat outputTimeFormat) {
            this.outputTimeFormat = outputTimeFormat;
        }

        public Integer call() throws Exception {
            final String result = outputTimeFormat.format(new Date(basetime.getTime() + offset));
            System.out.println(result);
            return 0;
        }
    }

    @SubCommand(name = "delta", description = "computes difference in millis between two given times")
    public static class TimeDeltaCommand implements ArgsCommand {
        //TODO: option "unit" for output (output as decimal)
        private final Date basetime;
        private final Date otherTime;

        public TimeDeltaCommand(Date basetime, Date otherTime) {
            this.basetime = basetime;
            this.otherTime = otherTime;
        }

        public Integer call() throws Exception {
            final long delta = otherTime.getTime() - basetime.getTime();
            System.out.println(delta);
            return 0;
        }
    }

    @SubCommand(name = "timeconvert", description = "converts given times to millis since epoch; uses current time if none specified")
    public static class TimeConvertCommand implements ArgsCommand {
        private final Date[] times;

        public TimeConvertCommand(@Param("times") Date... times) {
            this.times = times.length == 0 ? new Date[]{new Date()} : times;
        }

        public Integer call() throws Exception {
            for (Date time : times) {
                System.out.println(time.getTime());
            }
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
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("timmy");
        setup.setDefaultSubCommand(TimeConvertCommand.class);
        setup.addSubCommand(DefaultHelpCommand.class);
        setup.addSubCommand(TimeShiftCommand.class);
        setup.addSubCommand(TimeDeltaCommand.class);
//TODO        setup.addSubCommand(LocalToUtcCommand.class);
//TODO        setup.addSubCommand(UtcToLocalCommand.class);
        return BasicArgsParser.process(setup, args);
    }

    public static void main(String[] args) throws Exception {
        final int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}