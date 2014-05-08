package groovyx.cli;

import com.google.common.collect.ImmutableList
import io.airlift.airline.Cli;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException

/**
 * Created with IntelliJ IDEA.
 * User: jim
 * Date: 3/7/14
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */

@Command(name="placeholder")
abstract public class CommandScriptX extends Script {
    abstract Object runScript();

    @Override
    public Object run() {
        Cli<CommandScriptX> cliParser = getCommandScriptCli();
        try {
            JCommanderScript s = cliParser.parse((String[]) getProperty("args"));
            println(s);
            return s.runScriptBody();
        } catch (ParseException pe) {
            return help(cliParser, pe);
        }
    }

    protected Cli<CommandScriptX> getCommandScriptCli() {
        return Cli.<CommandScriptX>builder("parser").withCommand(this.getClass()).build();
    }

    protected Object help(Cli<JCommanderScript> cliParser, ParseException pe) {
        StringBuilder out = new StringBuilder();
        Help.help(cliParser.getMetadata(), ImmutableList.<String>of(), out);
        println(pe.getMessage());
        println(out);
        return -1;
    }

}
