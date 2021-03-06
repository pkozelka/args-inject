package net.kozelka.args.model;

import java.lang.reflect.Method;
import java.util.*;
import net.kozelka.args.api.ArgsCommand;

public final class SubCommandDeclaration {
    private String name;
    private final List<String> alternateNames = new ArrayList<String>();
    private final Class<? extends ArgsCommand> commandClass;
    private final List<ParamDeclaration> paramDeclarations = new ArrayList<ParamDeclaration>();
    private final Map<Method, OptionDeclaration> optionDeclarations = new HashMap<Method, OptionDeclaration>();
    private String description;

    public SubCommandDeclaration(Class<? extends ArgsCommand> commandClass) {
        this.commandClass = commandClass;
    }

    public Class<? extends ArgsCommand> getCommandClass() {
        return commandClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAlternateNames() {
        return alternateNames;
    }

    public void addAlternateNames(List<String> alternateNames) {
        this.alternateNames.addAll(alternateNames);
    }

    public Set<OptionDeclaration> getOptionDeclarations() {
        return new HashSet<OptionDeclaration>(optionDeclarations.values());
    }

    public List<ParamDeclaration> getParamDeclarations() {
        return paramDeclarations;
    }

    public void addParamDeclaration(ParamDeclaration paramDeclaration) {
        paramDeclarations.add(paramDeclaration);
    }

    public void addOptionDeclaration(OptionDeclaration optionDeclaration) {
        optionDeclarations.put(optionDeclaration.getOptionMethod(), optionDeclaration);
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
