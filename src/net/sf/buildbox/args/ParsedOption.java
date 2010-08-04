package net.sf.buildbox.args;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;

public final class ParsedOption {
    private final String usedName;
    private final OptionDeclaration optionDecl;
    private Object[] values;

    ParsedOption(String usedName, OptionDeclaration optionDecl) {
        this.usedName = usedName;
        this.optionDecl = optionDecl;
    }

    public String getUsedName() {
        return usedName;
    }

    public OptionDeclaration getOptionDecl() {
        return optionDecl;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public void parse(LinkedList<String> argsList) throws ParseException {
        final List<Object> paramValues = new ArrayList<Object>();
        for (ParamDeclaration paramDeclaration : optionDecl.getParams()) {
            final Class<?> paramType = paramDeclaration.getType();
            if (argsList.isEmpty()) {
                throw new ParseException("option " + usedName + ": not enough parameters provided", 0);
            }
            final String stringValue = argsList.removeFirst();
            paramValues.add(ArgsUtils.stringToType(stringValue, paramType));
        }
        values = paramValues.toArray(new Object[paramValues.size()]);
    }

    @Override
    public String toString() {
        return optionDecl.getShortName() + "[" + optionDecl.getLongName() + "]";
    }
}
