package site.felixkimmerle.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {

    private final String name;
    private final Expr.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    LoxFunction(String name,Expr.Function declaration, Environment closure, boolean isInitializer) {
        this.name = name;
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        if(declaration.params != null) {
            for (int i = 0; i < declaration.params.size(); i++) {
                environment.define(declaration.params.get(i).lexeme,
                        arguments.get(i));
            }
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }
            return returnValue.value;
        }
        if (isInitializer) {
            return closure.getAt(0, "this");
        }
        return null;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(name,declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
        if(name == null){
            return "<lambda fn>";
        }
        return "<fn " + name + ">";
    }

    public boolean isGetter(){
        return declaration.params == null;
    }
}
