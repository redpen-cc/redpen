package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.config.SymbolTable;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JavaScriptLoader extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptLoader.class);
    private final String name;
    private final Invocable invocable;
    private final String message;
    private static final ScriptEngineManager manager = new ScriptEngineManager();

    private static final String[] methodsToBeExposedToJS = {"getInt", "getFloat", "getString", "getBoolean", "getSet",
            "getConfigAttribute", "getSymbolTable", "addError", "addErrorWithPosition",
            "addLocalizedError", "addLocalizedErrorFromToken", "addLocalizedErrorWithPosition"};

    JavaScriptLoader(String name, String script) throws RedPenException {
        this.name = name;
        setValidatorName(name);
        ScriptEngine engine = manager.getEngineByName("nashorn");
        try {
            engine.put("redpenToBeBound", this);


            for (String methodToBeExposed : methodsToBeExposedToJS) {
                engine.eval(String.format(
                        "var %s = Function.prototype.bind.call(redpenToBeBound.%s, redpenToBeBound);",
                        methodToBeExposed, methodToBeExposed));
            }
            try {
                engine.eval("var _JavaScriptValidatorTest = Java.type('cc.redpen.validator.JavaScriptValidatorTest');");
            } catch (RuntimeException e) {
                if (!(e.getCause() instanceof ClassNotFoundException)) {
                    throw e;
                }
            }

            engine.eval("java = undefined; javax = undefined; Java = undefined; load = undefined; redpenToBeBound = undefined;");

            CompiledScript compiledScript = ((Compilable) engine).compile(script);
            compiledScript.eval();
            this.message = (String) engine.get("message");
            this.invocable = (Invocable) engine;
        } catch (ScriptException e) {
            throw new RedPenException(e);
        }
    }

    @Override
    public void preValidate(Sentence sentence) {
        call("preValidateSentence", sentence);
    }

    @Override
    public void preValidate(Section section) {
        call("preValidateSection", section);
    }

    @Override
    public void validate(Document document) {
        call("validateDocument", document);
    }

    @Override
    public void validate(Sentence sentence) {
        call("validateSentence", sentence);
    }

    @Override
    public void validate(Section section) {
        call("validateSection", section);
    }

    private Map<String, Boolean> functionExistenceMap = new HashMap<>();

    private void call(String functionName, Object... args) {
        Boolean functionExists = functionExistenceMap.getOrDefault(functionName, true);
        if (functionExists) {
            try {
                invocable.invokeFunction(functionName, args);
            } catch (ScriptException e) {
                LOG.error("failed to invoke {}", functionName, e);
            } catch (NoSuchMethodException ignore) {
                functionExistenceMap.put(functionName, false);
            }
        }
    }

    // give Validator methods public access so that they can be bound with JavaScript
    @Override
    public int getInt(String name) {
        return super.getInt(name);
    }

    @Override
    public float getFloat(String name) {
        return super.getFloat(name);
    }

    @Override
    public String getString(String name) {
        return super.getString(name);
    }

    @Override
    public boolean getBoolean(String name) {
        return super.getBoolean(name);
    }

    @Override
    public Set<String> getSet(String name) {
        return super.getSet(name);
    }

    @Override
    public Optional<String> getConfigAttribute(String name) {
        return super.getConfigAttribute(name);
    }

    @Override
    public SymbolTable getSymbolTable() {
        return super.getSymbolTable();
    }

    @Override
    public void addError(String message, Sentence sentenceWithError) {
        super.addError(message, sentenceWithError);
    }

    @Override
    Object getOrDefault(String name) {
        // script specific parameter wins
        Object value = super.getOrDefault(this.name.replaceAll("\\.js$", "") + "-" + name);
        if (value == null) {
            // fallback to normal parameter
            value = super.getOrDefault(name);
        }
        return value;
    }


    @Override
    public void addErrorWithPosition(String message, Sentence sentenceWithError,
                                     int start, int end) {
        super.addLocalizedErrorWithPosition(message, sentenceWithError, start, end);
    }

    @Override
    public void addLocalizedError(Sentence sentenceWithError, Object... args) {
        super.addLocalizedError(sentenceWithError, args);
    }

    @Override
    public void addLocalizedError(String messageKey, Sentence sentenceWithError, Object... args) {
        super.addLocalizedError(messageKey, sentenceWithError, args);
    }

    @Override
    public void addLocalizedErrorFromToken(Sentence sentenceWithError, TokenElement token, Object... args) {
        super.addLocalizedErrorFromToken(sentenceWithError, token, args);
    }

    @Override
    public void addLocalizedErrorWithPosition(Sentence sentenceWithError,
                                              int start, int end, Object... args) {
        super.addLocalizedErrorWithPosition(sentenceWithError, start, end, args);
    }

    @Override
    protected String getLocalizedErrorMessage(String key, Object... args) {
        if (message != null) {
            return MessageFormat.format(message, args);
        } else {
            return super.getLocalizedErrorMessage(key, args);
        }
    }
}
