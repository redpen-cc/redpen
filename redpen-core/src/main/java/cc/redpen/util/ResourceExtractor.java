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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * ResourceExtractor is called from FileLoader. To support a file format,
 * we create a class implementing ResourceExtractor.
 */
public abstract class ResourceExtractor<E> {
    protected E data;

    /**
     * load line.
     *
     * @param line line in a file
     */
    abstract protected void load(String line);

    /**
     * Given a input stream, load the contents.
     *
     * @param inputStream input stream
     * @throws IOException when failed to create reader from the specified input stream
     */
    public E load(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                load(line);
            }
        }
        return data;
    }

    /**
     * Load a given input file combined with jar package.
     *
     * @param inputFile a file included in the jar file
     * @throws IOException when input stream is null
     */
    public E loadFromResource(String inputFile) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(inputFile)) {
            if (inputStream == null) {
                throw new IOException("Failed to load input " + inputFile);
            }
            load(inputStream);
        }
        return data;
    }

}
