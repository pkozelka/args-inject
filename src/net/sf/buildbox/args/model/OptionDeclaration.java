package net.sf.buildbox.args.model;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents option declaration.
 */
public final class OptionDeclaration {
    private final String shortName;
    private final String longName;
    private final Method optionMethod;
    private final List<ParamDeclaration> paramDeclarations = new ArrayList<ParamDeclaration>();
    private Object globalObject;
    private String description;
    private boolean global;

    public OptionDeclaration(String shortName, String longName, Method optionMethod) throws ParseException {
        this.shortName = "".equals(shortName) ? null : shortName;
        this.longName = "".equals(longName) ? null : longName;
        this.optionMethod = optionMethod;
        if (shortName == null && longName == null) {
            throw new ParseException("Neither short nor long name declared for option method " + optionMethod, 0);
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public Method getOptionMethod() {
        return optionMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getGlobalObject() {
        return globalObject;
    }

    public void setGlobalObject(Object globalObject) {
        this.globalObject = globalObject;
    }

    public List<ParamDeclaration> getParamDeclarations() {
        return paramDeclarations;
    }

    public void addParamDeclaration(ParamDeclaration paramDeclaration) {
        paramDeclarations.add(paramDeclaration);
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (shortName != null) {
            sb.append(shortName);
            if (longName != null) {
                sb.append(" [");
                sb.append(longName);
                sb.append("]");
            }
        } else {
            sb.append(longName);
        }
        return sb.toString();
    }
}
