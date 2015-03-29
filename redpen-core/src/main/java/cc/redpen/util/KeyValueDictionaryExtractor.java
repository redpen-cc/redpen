/**
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
package cc.redpen.util;

import cc.redpen.RedPenException;

import java.util.HashMap;
import java.util.Map;

/**
 * An ResourceExtractor implementation for KeyValue input data.
 */
public class KeyValueDictionaryExtractor extends ResourceExtractor<Map<String, String>> {

    /**
     * Constructor.
     */
    public KeyValueDictionaryExtractor() {
        super();
        this.data = new HashMap<>();
    }

    /**
     * Load input file. The input file TSV with two columns.
     *
     * @param line line in a file
     * @throws RedPenException when the input line is invalid
     */
    @Override
    protected void load(String line) throws RedPenException {
        String[] result = line.split("\t");
        if (result.length != 2) {
            throw new RedPenException("Invalid line: " +  line);
        }
        data.put(result[0], result[1]);
    }

}
