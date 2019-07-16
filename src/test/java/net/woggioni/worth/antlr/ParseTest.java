package net.woggioni.worth.antlr;

import lombok.SneakyThrows;
import net.woggioni.worth.serialization.json.JSONDumper;
import net.woggioni.worth.xface.Value;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.io.InputStreamReader;

public class ParseTest {

    @Test
    @SneakyThrows
    public void test(){

        CodePointCharStream inputStream = CharStreams.fromReader(new InputStreamReader(getClass().getResourceAsStream("/test.json")));
        JSONLexer lexer = new JSONLexer(inputStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(commonTokenStream);
        JSONListenerImpl listener = new JSONListenerImpl();
        ParseTreeWalker walker = new ParseTreeWalker();
 		walker.walk(listener, parser.json());
        Value result = listener.result;
        new JSONDumper().dump(result, System.out);
    }
}
