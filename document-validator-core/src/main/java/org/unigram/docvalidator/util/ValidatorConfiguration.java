/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    this.attributes = new HashMap<String, String>();
    this.childConfigurations = new ArrayList<ValidatorConfiguration>(0);
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
    this.attributes = new HashMap<String, String>();
    this.childConfigurations = new ArrayList<ValidatorConfiguration>(0);
    this.parentConfiguration = parent;
  }

  /**
   * Check if the configuration has the settings for the specified attribute.
   *
   * @param name attribute name
   * @return true when the configuration contains the specified attribute, false otherwise
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
   * Add an attribute
   *
   * @param name  attribute name
   * @param value attribute value
   */
  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }

  /**
   * Get sub configurations
   *
   * @return sub-configuration list
   */
  public Iterator<ValidatorConfiguration> getChildren() {
    return childConfigurations.iterator();
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
