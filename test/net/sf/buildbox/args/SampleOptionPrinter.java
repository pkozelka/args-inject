package net.sf.buildbox.args;

import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.api.ArgsCommand;

public class SampleOptionPrinter implements ArgsCommand {
    private int sampleInt;
    private boolean sampleBoolean;
    private String sampleString;

    @Option(longName = "--sample-int", shortName = "-i", description = "sample integer option")
    public void setSampleInt(int sampleInt) {
        this.sampleInt = sampleInt;
    }

    @Option(longName = "--sample-boolean", shortName = "-b", description = "sample boolean option")
    public void setSampleBoolean(boolean sampleBoolean) {
        this.sampleBoolean = sampleBoolean;
    }

    @Option(longName = "--sample-string", shortName = "-s", description = "sample string option")
    public void setSampleString(String sampleString) {
        this.sampleString = sampleString;
    }

    public Integer call() throws Exception {
        System.out.println(String.format("%d:%s:%s", sampleInt, sampleBoolean, sampleString));
        return 0;
    }

    static int run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("DemoFileLister");
        setup.setDefaultSubCommand(SampleOptionPrinter.class);
        return BasicArgsParser.process(setup, args);
    }
}
