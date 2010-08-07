package net.sf.buildbox.args.zer;

import net.sf.buildbox.args.annotation.Option;

public class LogLevelCliOptions {
    private String logLevel;

    @Option(longName = "--log-level")
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Option(shortName = "-vv")
    public void setLogLevelTRACE() {
        this.logLevel = "TRACE";
    }

    @Option(shortName = "-v", longName = "--verbose")
    public void setLogLevelDEBUG() {
        this.logLevel = "DEBUG";
    }

    @Option(shortName = "-q", longName = "--quiet")
    public void setLogLevelWARNING() {
        this.logLevel = "WARNING";
    }

    @Option(shortName = "-qq")
    public void setLogLevelERROR() {
        this.logLevel = "ERROR";
    }

    public String getLogLevel() {
        return logLevel;
    }
}
