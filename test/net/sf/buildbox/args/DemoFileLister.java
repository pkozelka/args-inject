package net.sf.buildbox.args;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.ExecutableCommand;

@SubCommand(name = "ls")
public class DemoFileLister implements ExecutableCommand {
    private final boolean deep;
    private final int maxCount;
    final File[] files;
    private boolean colorful;
    private final Properties properties = new Properties();

    public DemoFileLister(boolean deep, int maxCount, File... files) {
        this.deep = deep;
        this.maxCount = maxCount;
        this.files = files;
    }

    @Option(longName = "--property", shortName = "-D")
    public void addProperty(String propertyName, String propertyValue) {
        properties.put(propertyName, propertyValue);
    }

    @Option(longName = "--color", shortName = "-C")
    public void setColorful(boolean colorful) {
        this.colorful = colorful;
    }

    public Integer call() throws Exception {
        System.out.println("colorful = " + colorful);
        System.out.println("deep = " + deep);
        System.out.println("maxCount = " + maxCount);
        System.out.println("files = " + Arrays.asList(files));
        System.out.println("properties = " + properties);
        return 0;
    }
}
