package net.sf.buildbox.args;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;

public final class ParsedOption {
    private final String usedName;
    private final OptionDeclaration optionDecl;
    private List<Object> unmarshalledValues;

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

    public List<Object> getValues() {
        return unmarshalledValues;
    }

    public void parse(LinkedList<String> actualValues) throws ParseException {
        unmarshalledValues = ParamDeclaration.parseParamList("option " + usedName, optionDecl.getParams(), actualValues);
    }

    @Override
    public String toString() {
        return optionDecl.getShortName() + "[" + optionDecl.getLongName() + "]";
    }
}
