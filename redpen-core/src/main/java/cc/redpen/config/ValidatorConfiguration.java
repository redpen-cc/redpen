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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration for Validators.
 */
public class ValidatorConfiguration implements Serializable, Cloneable {
    /**
     * Define how severe the validation errors are.
     */
    public enum SEVERITY {
        INFO, WARN, ERROR;

        Map<String, String> mapping = new HashMap<String, String>() {
            {
                put("INFO", "Info");
                put("WARN", "Warn");
                put("ERROR", "Error");
            }
        };

        @Override
        public String toString() {
            return mapping.get(name());
        }
    }

    private final String configurationName;
    private Map<String, String> properties;
    private SEVERITY severity = SEVERITY.ERROR;

    /**
     * @param name name configuration settings
     */
    public ValidatorConfiguration(String name) {
        this(name, new HashMap<>());
    }

    /**
     * @param name name configuration settings
     * @param properties validator properties
     */
    public ValidatorConfiguration(String name, Map<String, String> properties) { this(name, properties, SEVERITY.ERROR); }

    /**
     * @param name name configuration settings
     * @param properties validator properties
     * @param severity error level
     */
    public ValidatorConfiguration(String name, Map<String, String> properties, SEVERITY severity) {
        this.configurationName = name;
        this.properties = properties;
        this.severity = severity;
    }

    /**
     * Return the properties map
     *
     * @return a map of the configuration properties to their values
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Get property value.
     *
     * @param name property name
     * @return value of the specified property
     */
    public String getProperty(String name) {
        return this.properties.get(name);
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
     * Get error level.
     * @return error level
     */
    public SEVERITY getSeverity() {
        return severity;
    }

    /**
     * Set error level.
     * @param severity error level
     */
    public ValidatorConfiguration setSeverity(String severity) {
        try {
            setSeverity(SEVERITY.valueOf(severity));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("No such a error level as " + severity, e);
        }
        return this;
    }

    public ValidatorConfiguration setSeverity(SEVERITY severity) {
        this.severity = severity;
        return this;
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
     * Add an property.
     *
     * @param name  property name
     * @param value property value
     * @return this object
     */
    public ValidatorConfiguration addProperty(String name, Object value) {
        properties.put(name, String.valueOf(value));
        return this;
    }

    @Deprecated
    public ValidatorConfiguration addAttribute(String name, Object value) {
        return addProperty(name, value);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidatorConfiguration)) return false;
        ValidatorConfiguration that = (ValidatorConfiguration)o;
        return Objects.equals(configurationName, that.configurationName) &&
                Objects.equals(properties, that.properties) &&
                Objects.equals(severity, that.severity);
    }

    @Override public int hashCode() {
        return Objects.hash(configurationName);
    }

    @Override public String toString() {
        return configurationName;
    }

    /**
     * @return a copy of ValidatorConfiguration
     */
    @Override public ValidatorConfiguration clone() {
        try {
            ValidatorConfiguration clone = (ValidatorConfiguration)super.clone();
            clone.properties = new HashMap<>(properties);
            clone.severity = severity;
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
