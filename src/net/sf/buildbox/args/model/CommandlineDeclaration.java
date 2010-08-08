package net.sf.buildbox.args.model;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CommandlineDeclaration {
    private final Map<String, OptionDeclaration> optionsByShortName = new HashMap<String, OptionDeclaration>();
    private final Map<String, OptionDeclaration> optionsByLongName = new HashMap<String, OptionDeclaration>();

    private String programName;
    private Object[] globalOptionsObjects;
    private SubCommandDeclaration defaultSubCommand;
    private final Set<SubCommandDeclaration> subCommandDeclarations = new HashSet<SubCommandDeclaration>();
    private final Set<OptionDeclaration> optionDeclarations = new HashSet<OptionDeclaration>();

    public Object[] getGlobalOptionsObjects() {
        return globalOptionsObjects;
    }

    public void setGlobalOptions(Object... globalOptionsObjects) throws ParseException {
        this.globalOptionsObjects = globalOptionsObjects;
    }

    public SubCommandDeclaration getDefaultSubCommand() {
        return defaultSubCommand;
    }

    public void setDefaultCommand(SubCommandDeclaration defaultSubCommand) {
        this.defaultSubCommand = defaultSubCommand;
    }

    public void addSubCommand(SubCommandDeclaration cmdDecl) {
        subCommandDeclarations.add(cmdDecl);
    }

    public void addOption(OptionDeclaration optionDeclaration) throws ParseException {
        boolean isnew = true;
        final String shortName = optionDeclaration.getShortName();
        if (shortName != null) {
            final OptionDeclaration oldDecl = optionsByLongName.get(shortName);
            final Method method = optionDeclaration.getOptionMethod();
            if (oldDecl != null) {
                if (!oldDecl.getOptionMethod().equals(method)) {
                    throw new ParseException(String.format("%s: short option name declared twice - first in %s and also in %s",
                            shortName, oldDecl.getOptionMethod(), method), 0);
                }
                isnew = false;
            }
            optionsByShortName.put(shortName, optionDeclaration);
        }
        final String longName = optionDeclaration.getLongName();
        if (longName != null) {
            final OptionDeclaration oldDecl = optionsByLongName.get(longName);
            final Method method = optionDeclaration.getOptionMethod();
            if (oldDecl != null) {
                if (!oldDecl.getOptionMethod().equals(method)) {
                    throw new ParseException(String.format("%s: long option name declared twice - first in %s and also in %s",
                            longName, oldDecl.getOptionMethod(), method), 0);
                }
                isnew = false;
            }
            optionsByLongName.put(longName, optionDeclaration);
        }
        if (isnew) {
            optionDeclarations.add(optionDeclaration);
        }
    }

    public SubCommandDeclaration lookupCommand(String command, boolean useDefault) {
        for (SubCommandDeclaration decl : subCommandDeclarations) {
            if (command.equals(decl.getName())) {
                return decl;
            }
            if (decl.getAlternateNames().contains(command)) {
                return decl;
            }
        }
        return useDefault ? defaultSubCommand : null;
    }

    public OptionDeclaration lookupShortOption(String option) {
        //TODO: support value with/next to option name
        return optionsByShortName.get(option);
    }

    public OptionDeclaration lookupLongOption(String option) {
        //TODO: support value assignment/next to option name
        return optionsByLongName.get(option);
    }

    public Set<SubCommandDeclaration> getCommandDeclarations() {
        return subCommandDeclarations;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
