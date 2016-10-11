/*
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * <p>A Validator implementation load JavaScript dynamically.</p>
 * <p>files which name end with &quot;.js&quot; and located in &quot;js&quot; (can be specified with &quot;script-path&quot; property) directory will be treated as JavaScript validator implementation. Functions with the following signature will be called upon validation time:</p>
 * <pre>
 *     function preValidateSentence(sentence) {
 *     }
 *     function preValidateSection(section) {
 *     }
 *     function validateDocument(document) {
 *       // your validation logic for document here
 *     }
 *     function validateSentence(sentence) {
 *       // if(your validation logic for sentence here) {
 *       //   addError('validation error message', sentence);
 *       // }
 *     }
 *     function validateSection(section) {
 *       // your validation logic for section here
 *     }
 * </pre>
 */
public class JavaScriptValidator extends Validator {
    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptValidator.class);
    final List<JavaScriptLoader> scripts = new ArrayList<>();

    public JavaScriptValidator() {
        super("script-path", "js");
    }

    @Override
    protected void init() throws RedPenException {
        try {
            String jsValidatorsPath = getString("script-path");
            File jsDirectory = findFile(jsValidatorsPath);
            LOG.info("JavaScript validators directory: {}", jsValidatorsPath);
            File[] jsValidatorFiles = jsDirectory.listFiles();
            if (jsValidatorFiles != null) {
                for (File file : jsValidatorFiles) {
                    if (file.isFile() && file.getName().endsWith(".js")) {
                        try {
                            JavaScriptLoader valid = new JavaScriptLoader(file.getName(), loadCached(file));
                            scripts.add(valid);
                        } catch (IOException e) {
                            LOG.error("Exception while reading js file", e);
                        }
                    }
                }
            }
        } catch (RedPenException e) {
            LOG.warn("JavaScript validators directory is missing: {}", e.toString());
        }
    }

    @Override
    public void preInit(ValidatorConfiguration config, Configuration globalConfig) throws RedPenException {
        super.preInit(config, globalConfig);
        for (JavaScriptLoader js : scripts) {
            js.preInit(config, globalConfig);
        }
    }

    private static final Map<File, String> fileCache = new HashMap<>();
    private static final Map<File, Long> loadTime = new HashMap<>();

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

    List<ValidationError> errors;

    @Override
    public void setErrorList(List<ValidationError> errors) {
        this.errors = errors;
    }

    @Override
    public void preValidate(Sentence sentence) {
        for (JavaScriptLoader js : scripts) {
            js.setErrorList(errors);
            js.preValidate(sentence);
        }
    }

    @Override
    public void preValidate(Section section) {
        for (JavaScriptLoader js : scripts) {
            js.setErrorList(errors);
            js.preValidate(section);
        }
    }

    @Override
    public void validate(Document document) {
        for (JavaScriptLoader js : scripts) {
            js.setErrorList(errors);
            js.validate(document);
        }
    }

    @Override
    public void validate(Sentence sentence) {
        for (JavaScriptLoader js : scripts) {
            js.setErrorList(errors);
            js.validate(sentence);
        }
    }

    @Override
    public void validate(Section section) {
        for (JavaScriptLoader js : scripts) {
            js.setErrorList(errors);
            js.validate(section);
        }
    }
}
