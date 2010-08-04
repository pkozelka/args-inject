package net.sf.buildbox.args.model;

import java.util.ArrayList;
import java.util.List;
import net.sf.buildbox.args.api.ExecutableCommand;

public final class CommandDeclaration {
    private String name;
    private final List<String> alternateNames = new ArrayList<String>();
    private final Class<? extends ExecutableCommand> commandClass;
    private final List<ParamDeclaration> params = new ArrayList<ParamDeclaration>();
    private final List<OptionDeclaration> options = new ArrayList<OptionDeclaration>();

    public CommandDeclaration(Class<? extends ExecutableCommand> commandClass) {
        this.commandClass = commandClass;
    }

    public Class<? extends ExecutableCommand> getCommandClass() {
        return commandClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAlternateNames() {
        return alternateNames;
    }

    public void addAlternateNames(List<String> alternateNames) {
        this.alternateNames.addAll(alternateNames);
    }

    public List<OptionDeclaration> getOptions() {
        return options;
    }

    public List<ParamDeclaration> getParams() {
        return params;
    }

    public void addParam(ParamDeclaration param) {
        params.add(param);
    }

    public void addOption(OptionDeclaration optionDeclaration) {
        options.add(optionDeclaration);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (!alternateNames.isEmpty()) {
            sb.append(" (");

            boolean first = true;
            for (String altName : alternateNames) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(altName);
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
