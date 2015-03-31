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
package cc.redpen.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Validators.
 */
public final class ValidatorConfiguration {
    private final String configurationName;
    private final Map<String, String> attributes;

    /**
     * Constructor.
     *
     * @param name name configuration settings
     */
    public ValidatorConfiguration(String name) {
        this.configurationName = name;
        this.attributes = new HashMap<>();
    }

    /**
     * Return the attributes map
     *
     * @return a map of the configuration attributes to their values
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Get attribute value.
     *
     * @param name attribute name
     * @return value of the specified attribute
     */
    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
     * Get configuration name.
     *
     * @return configuration name
     */
    public String getConfigurationName() {
        return configurationName;
    }

    /**
     * Get validator class name
     *
     * @return validator class name
     */
    public String getValidatorClassName() {
        return configurationName + "Validator";
    }

    /**
     * Add an attribute.
     *
     * @param name  attribute name
     * @param value attribute value
     * @return this object
     */
    public ValidatorConfiguration addAttribute(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    /**
     * Add an attribute.
     *
     * @param name  attribute name
     * @param value attribute value
     * @return this object
     */
    public ValidatorConfiguration addAttribute(String name, boolean value) {
        attributes.put(name, String.valueOf(value));
        return this;
    }

}
