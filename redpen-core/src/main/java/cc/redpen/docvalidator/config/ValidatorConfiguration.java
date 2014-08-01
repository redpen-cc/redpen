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
package cc.redpen.docvalidator.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Configuration for Validators.
 */
public final class ValidatorConfiguration {
  /**
   * Constructor.
   *
   * @param name name configuration settings
   */
  public ValidatorConfiguration(String name) {
    this.configurationName = name;
    this.attributes = new HashMap<>();
    this.childConfigurations = new ArrayList<>();
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
    this.childConfigurations = new ArrayList<>();
    this.parentConfiguration = parent;
  }

  /**
   * Check if the configuration has the settings for the specified attribute.
   *
   * @param name attribute name
   * @return true when the configuration contains the specified attribute,
   * false otherwise
   */
  public boolean hasAttribute(String name) {
    return this.attributes.containsKey(name);
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
   * Set an attribute.
   *
   * @param name  attribute name
   * @param value attribute value
   * @return true when successfully set attribute,
   * false failed to set attribute or a value is set for the attribute
   * name (in such case the value is not overridden).
   */
  public boolean setAttribute(String name, String value) {
    if (attributes.containsKey(name)) {
      return false;
    }
    this.attributes.put(name, value);
    return true;
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
   * Get sub-configurations.
   *
   * @return sub-configuration list
   */
  public List<ValidatorConfiguration> getChildren() {
    return childConfigurations;
  }

  /**
   * Get all the attribute names.
   *
   * @return attribute names in the configuration.
   */
  public Set<String> getAttributes() {
    return attributes.keySet();
  }

  /**
   * Get the number of sub-configurations.
   *
   * @return sub configuration number
   */
  public int getChildrenNumber() {
    return childConfigurations.size();
  }

  /**
   * Set the parent configuration.
   *
   * @param parent parent Configuration object
   */
  public void setParent(ValidatorConfiguration parent) {
    this.parentConfiguration = parent;
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
   * Add a sub-configuration configuration.
   *
   * @param childConfig sub-configuration
   */
  public void addChild(ValidatorConfiguration childConfig) {
    this.childConfigurations.add(childConfig);
  }

  private final String configurationName;

  private final HashMap<String, String> attributes;

  private final ArrayList<ValidatorConfiguration> childConfigurations;

  private ValidatorConfiguration parentConfiguration;
}
