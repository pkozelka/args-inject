package net.sf.buildbox.args.zer;

import net.sf.buildbox.args.annotation.Option;
import net.sf.buildbox.args.api.ExecutableCommand;

public abstract class ZerXmlfulCommand implements ExecutableCommand {
    private boolean xmlOutput;

    @Option(longName = "--xml")
    public void setXmlOutput() {
        this.xmlOutput = true;
    }
}
