/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
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
 */
package cc.redpen.util;

import cc.redpen.RedPenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * load dictionary data from input source
 */
public final class DictionaryLoader<E> {
    private static final Logger LOG = LoggerFactory.getLogger(DictionaryLoader.class);

    private final Supplier<E> supplier;
    private final BiConsumer<E, String> loader;

    public DictionaryLoader(Supplier<E> supplier, BiConsumer<E, String> loader) {
        this.supplier = supplier;
        this.loader = loader;
    }

    /**
     * Given a input stream, load the contents.
     *
     * @param inputStream input stream
     * @throws IOException when failed to create reader from the specified input stream
     */
    E load(InputStream inputStream) throws IOException {
        E e = supplier.get();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.UTF_8))) {
            bufferedReader.lines().forEach(line -> loader.accept(e, line));
        }
        return e;
    }

    /**
     * Load a given input file combined with jar package.
     *
     * @param resourcePath resource path
     * @throws IOException when resource is not found
     */
    private E loadFromResource(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Failed to load input " + resourcePath);
            }
            return load(inputStream);
        }
    }

    /**
     * Load a given input file combined with jar package.
     *
     * @param file file to load
     * @throws IOException when input stream is null
     */
    private E loadFromFile(File file) throws IOException {
        return load(new FileInputStream(file));
    }

    private final Map<String, E> resourceCache = new HashMap<>();

    /**
     * returns word list loaded from resource
     *
     * @param path           resource path
     * @param dictionaryName name of the resource
     * @return word list
     * @throws RedPenException
     */
    public E loadCachedFromResource(String path, String dictionaryName) throws RedPenException {
        E strings = resourceCache.computeIfAbsent(path, e -> {
            try {
                return loadFromResource(path);
            } catch (IOException ioe) {
                LOG.error(ioe.getMessage());
                return null;
            }
        });
        if (strings == null) {
            throw new RedPenException("Failed to load " + dictionaryName + ":" + path);
        }
        LOG.info("Succeeded to load " + dictionaryName + ".");
        return strings;
    }


    private final Map<String, E> fileCache = new HashMap<>();
    private final Map<String, Long> fileNameTimestampMap = new HashMap<>();

    /**
     * returns word list loaded from file
     *
     * @param file           file to load
     * @param dictionaryName name of the file
     * @return word list
     * @throws RedPenException
     */
    public E loadCachedFromFile(File file, String dictionaryName) throws RedPenException {
        String path = file.getAbsolutePath();
        if (!file.exists()) {
            throw new RedPenException("File not found: " + file);
        }
        long currentModified = file.lastModified();
        fileNameTimestampMap.computeIfPresent(path, (key, lastModified) -> {
            if (lastModified != currentModified) {
                // the file has been modified since last load
                // clear the cache and load the file in the latter block
                fileCache.remove(key);
                return null;
            } else {
                return lastModified;
            }
        });

        E loaded = fileCache.computeIfAbsent(path, e -> {
            try {
                E newlyLoaded = loadFromFile(file);
                fileNameTimestampMap.put(path, file.lastModified());
                return newlyLoaded;
            } catch (IOException ioe) {
                LOG.error(ioe.getMessage());
                return null;
            }
        });
        if (loaded == null) {
            throw new RedPenException("Failed to load " + dictionaryName + ":" + path);
        }
        LOG.info("Succeeded to load " + dictionaryName + ".");
        return loaded;
    }
}
