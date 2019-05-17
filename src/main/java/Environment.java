import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;

    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        if (enclosing != null) return enclosing.get(name);

        //TODO: Pass in ctx, so error messages with line numbers can be generated
        throw new RuntimeError("Undefined variable.",  name);
    }

    void assign(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError("Attempted to assign to undefined variable.", name);
    }

    void define(String name, Object value) {
        values.put(name, value);
    }
}
