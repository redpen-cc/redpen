/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
package cc.redpen.util;

import cc.redpen.RedPenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Load file from input file name or stream.
 */
public class FileLoader {
    private final ResourceExtractor resourceExtractor;
    private static final Logger LOG =
            LoggerFactory.getLogger(ResourceExtractor.class);

    /**
     * Constructor.
     *
     * @param ex ResourceExtractor
     */
    public FileLoader(ResourceExtractor ex) {
        this.resourceExtractor = ex;
    }

    /**
     * Load input file.
     *
     * @param fileName input file name.
     */
    public void loadFile(String fileName) throws IOException {
        try (InputStream inputStream = new FileInputStream(fileName)) {
            loadFile(inputStream);
        }
    }

    /**
     * Given a input stream, load the contents.
     *
     * @param inputStream input stream
     */
    public void loadFile(InputStream inputStream) throws IOException {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            try {
                this.resourceExtractor.load(line);
            } catch (RedPenException e) {
                LOG.error(e.getMessage()); // just skip to load the line
                LOG.error("Skip to load line...");
            }

        }
    }
}
