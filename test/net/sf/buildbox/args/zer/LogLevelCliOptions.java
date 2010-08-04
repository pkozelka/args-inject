package net.sf.buildbox.args.zer;

import net.sf.buildbox.args.annotation.CliOption;

public class LogLevelCliOptions {
    private String logLevel;

    @CliOption(longName = "--log-level")
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @CliOption(shortName = "-vv")
    public void setLogLevelTRACE() {
        this.logLevel = "TRACE";
    }

    @CliOption(shortName = "-v", longName = "--verbose")
    public void setLogLevelDEBUG() {
        this.logLevel = "DEBUG";
    }

    @CliOption(shortName = "-q", longName = "--quiet")
    public void setLogLevelWARNING() {
        this.logLevel = "WARNING";
    }

    @CliOption(shortName = "-qq")
    public void setLogLevelERROR() {
        this.logLevel = "ERROR";
    }

    public String getLogLevel() {
        return logLevel;
    }
}
