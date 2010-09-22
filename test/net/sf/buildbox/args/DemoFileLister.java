package net.sf.buildbox.args;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import net.sf.buildbox.args.annotation.AnnottationAwareSetup;
import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.ArgsCommand;

@SubCommand(name = "ls", description = "list files (demo)")
public class DemoFileLister implements ArgsCommand {
    private final boolean deep;
    private final int maxCount;
    final File[] files;
    private boolean colorful;
    private final Properties properties = new Properties();
    private TimeUnit timeunit = TimeUnit.NANOSECONDS;
    private File[] locations;
    private int[] numbers;

    public DemoFileLister(boolean deep, int maxCount, File... files) {
        this.deep = deep;
        this.maxCount = maxCount;
        this.files = files;
    }

    @Option(longName = "--locations", description = "shows that default separator for files is File.pathSeparator")
    public void setLocations(File[] locations) {
        this.locations = locations;
    }

    @Option(longName = "--numbers", description = "shows that default separator is comma")
    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    @Option(longName = "--time-unit", shortName = "-u")
    public void setTimeunit(TimeUnit timeunit) {
        this.timeunit = timeunit;
    }

    @Option(longName = "--property", shortName = "-D")
    public void addProperty(String propertyName, String propertyValue) {
        properties.put(propertyName, propertyValue);
    }

    @Option(longName = "--color", shortName = "-C", description = "set output colors")
    public void setColorful(boolean colorful) {
        this.colorful = colorful;
    }

    public Integer call() throws Exception {
        System.out.println("colorful = " + colorful);
        System.out.println("deep = " + deep);
        System.out.println("maxCount = " + maxCount);
        System.out.println("files = " + Arrays.asList(files));
        System.out.println("properties = " + properties);
        System.out.println("timeunit = " + timeunit);
        return 0;
    }

    static int run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("DemoFileLister");
        setup.setDefaultSubCommand(DemoFileLister.class);
        return BasicArgsParser.process(setup, args);
    }
}
