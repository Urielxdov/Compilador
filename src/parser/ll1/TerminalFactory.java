package parser.ll1;

import parser.grammar.Terminal;

public class TerminalFactory {

    public Terminal terminalFactory (String name) {
        switch (name) {
            case "id":
                return new Terminal("id");
            default:
                return null;
        }
    }
}
