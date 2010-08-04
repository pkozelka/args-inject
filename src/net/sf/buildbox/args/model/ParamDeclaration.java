package net.sf.buildbox.args.model;

import java.io.File;

public final class ParamDeclaration {
    private Class<?> type;
    private String format;
    private String timeZone;
    private String listSeparator = File.pathSeparator;

    public ParamDeclaration(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getListSeparator() {
        return listSeparator;
    }

    public void setListSeparator(String listSeparator) {
        this.listSeparator = listSeparator;
    }
}
