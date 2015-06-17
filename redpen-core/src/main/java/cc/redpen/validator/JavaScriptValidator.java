package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.LineOffset;
import cc.redpen.tokenizer.TokenElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by yusuke on 6/11/15.
 * <p>
 * Copyright 2015 yusuke
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
public class JavaScriptValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptValidator.class);
    public final String DEFAULT_JS_VALIDATORS_PATH = "js";
    private final List<Invocable> jsValidatorImpls = new ArrayList<>();


    @Override
    protected void init() throws RedPenException {
        String jsValidatorsPath = getConfigAttribute("validator-path").orElse(DEFAULT_JS_VALIDATORS_PATH);
        LOG.info("JavaScript validators directory: {}", jsValidatorsPath);
        File[] jsValidatorFiles = new File(jsValidatorsPath).listFiles();
        if (jsValidatorFiles != null) {
            for (File file : jsValidatorFiles) {
                if (file.isFile() && file.getName().endsWith(".js")) {
                    try {
                        loadScript(loadCached(file));
                    } catch (IOException e) {
                        LOG.error("Exception while reading js file", e);
                    }
                }
            }
        }
    }

    void loadScript(String js) throws RedPenException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        try {
            engine.put("redpenToBeBound", this);
            engine.eval("var createValidationError = Function.prototype.bind.call(redpenToBeBound.createValidationError, redpenToBeBound);" +
                    "var createValidationErrorFromToken = Function.prototype.bind.call(redpenToBeBound.createValidationErrorFromToken, redpenToBeBound);" +
                    "var createValidationErrorWithPosition = Function.prototype.bind.call(redpenToBeBound.createValidationErrorWithPosition, redpenToBeBound);");

            CompiledScript compiledScript = ((Compilable) engine).compile(js);
            compiledScript.eval();
            jsValidatorImpls.add((Invocable) engine);
        } catch (ScriptException e) {
            throw new RedPenException(e);
        }

    }

    static final Map<File, String> fileCache = new HashMap<>();
    static final Map<File, Long> loadTime = new HashMap<>();

    /**
     * Load file content. Returns cached content if the last modified date is same as previous.
     *
     * @param file file to be loaded
     * @return file content
     * @throws IOException when failed to load the file
     */
    static String loadCached(File file) throws IOException {
        Objects.requireNonNull(file);
        Long storedTimestamp = loadTime.get(file);
        if (storedTimestamp != null && storedTimestamp == file.lastModified()) {
            return fileCache.get(file);
        }
        // file has been updated or has never been loaded
        String read = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), Charset.forName("UTF-8"));
        fileCache.put(file, read);
        loadTime.put(file, file.lastModified());
        return read;
    }

    @Override
    public void preValidate(Sentence sentence) {
        for (Invocable invocable : jsValidatorImpls) {
            call(invocable, "preValidateSentence", sentence);
        }
    }

    @Override
    public void preValidate(Section section) {
        for (Invocable invocable : jsValidatorImpls) {
            call(invocable, "preValidateSection", section);
        }
    }

    @Override
    public void validate(List<ValidationError> errors, Document document) {
        for (Invocable invocable : jsValidatorImpls) {
            call(invocable, "validateDocument", errors, document);
        }
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        for (Invocable invocable : jsValidatorImpls) {
            call(invocable, "validateSentence", errors, sentence);
        }
    }

    @Override
    public void validate(List<ValidationError> errors, Section section) {
        for (Invocable invocable : jsValidatorImpls) {
            call(invocable, "validateSection", errors, section);
        }
    }

    private Map<Invocable, Map<String, Boolean>> functionExistenceMap = new HashMap<>();

    void call(Invocable invocable, String functionName, Object... args) {
        Map<String, Boolean> map = functionExistenceMap.computeIfAbsent(invocable, e -> new HashMap<>());
        Boolean functionExists = map
                .getOrDefault(functionName, true);
        if (functionExists) {
            try {
                invocable.invokeFunction(functionName, args);
            } catch (ScriptException e) {
                LOG.error("failed to invoke {}", functionName, e);
            } catch (NoSuchMethodException ignore) {
                map.put(functionName, false);
            }
        }
    }

    @Override
    public ValidationError createValidationError(Sentence sentenceWithError, Object... args) {
        return super.createValidationError(sentenceWithError, args);
    }

    @Override
    public ValidationError createValidationError(String messageKey, Sentence sentenceWithError, Object... args) {
        return super.createValidationError(messageKey, sentenceWithError, args);
    }

    @Override
    public ValidationError createValidationErrorFromToken(Sentence sentenceWithError, TokenElement token) {
        return super.createValidationErrorFromToken(sentenceWithError, token);
    }

    @Override
    public ValidationError createValidationErrorWithPosition(Sentence sentenceWithError,
                                                             Optional<LineOffset> start, Optional<LineOffset> end, Object... args) {
        return super.createValidationErrorWithPosition(sentenceWithError, start, end, args);
    }

}
