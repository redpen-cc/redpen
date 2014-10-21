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
package cc.redpen.config;

import java.util.HashMap;

/**
 * Configuration for Validators.
 */
public final class ValidatorConfiguration {
    private final String configurationName;
    private final HashMap<String, String> attributes;
    private ValidatorConfiguration parentConfiguration;

    /**
     * Constructor.
     *
     * @param name name configuration settings
     */
    public ValidatorConfiguration(String name) {
        this.configurationName = name;
        this.attributes = new HashMap<>();
        this.parentConfiguration = null;
    }

    /**
     * Constructor.
     *
     * @param name   name configuration settings
     * @param parent parent Configuration object
     */
    public ValidatorConfiguration(String name, ValidatorConfiguration parent) {
        this.configurationName = name;
        this.attributes = new HashMap<>();
        this.parentConfiguration = parent;
    }

    /**
     * Get attribute value.
     *
     * @param name attribute name
     * @return value of the specified attribute
     */
    public String getAttribute(String name) {
        if (!this.attributes.containsKey(name)) {
            if (this.parentConfiguration != null) {
                return parentConfiguration.getAttribute(name);
            } else {
                return null;
            }
        }
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
     * Add an attribute.
     *
     * @param name  attribute name
     * @param value attribute value
     * @retrun this object
     */
    public ValidatorConfiguration addAttribute(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    /**
     * Get Parent configuration.
     *
     * @return Parent configuration
     */
    public ValidatorConfiguration getParent() {
        return this.parentConfiguration;
    }

    /**
     * Set the parent configuration.
     *
     * @param parent parent Configuration object
     */
    public void setParent(ValidatorConfiguration parent) {
        this.parentConfiguration = parent;
    }
}
