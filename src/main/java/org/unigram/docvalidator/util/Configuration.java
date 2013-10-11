package org.unigram.docvalidator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Configuration for Validators.
 */
public final class Configuration {
  /**
   * constructor.
   * @param configurationName name configuration settings
   */
  public Configuration(String name) {
    this.configurationName = name;
    this.attributes = new HashMap<String, String>();
    this.childConfigurations = new ArrayList<Configuration>(0);
    this.parentConfiguration = null;
  }

  /**
   * constructor.
   * @param configurationName name configuration settings
   * @param parent parent Configuration object
   */
  public Configuration(String name, Configuration parent) {
    this.configurationName = name;
    this.attributes = new HashMap<String, String>();
    this.childConfigurations = new ArrayList<Configuration>(0);
    this.parentConfiguration = parent;
  }

  public boolean hasAttribute(String name) {
    if (this.attributes.containsKey(name)) {
      return true;
    }
    return false;
  }

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

  public String getConfigurationName() {
    return configurationName;
  }

  public boolean setAttribute(String name, String value) {
    if (attributes.containsKey(name)) {
        return false;
    }
    this.attributes.put(name, value);
    return true;
  }

  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }

  public Iterator<Configuration> getChildren() {
    return childConfigurations.iterator();
  }

  public Set<String> getAttributes() {
      return attributes.keySet();
  }

  public int getChildrenNumber() {
    return childConfigurations.size();
  }

  public void setParent(Configuration parent) {
    this.parentConfiguration = parent;
  }

  public Configuration getParent() {
    return this.parentConfiguration;
  }

  public void addChild(Configuration childConfig) {
    this.childConfigurations.add(childConfig);
  }

  private String configurationName;

  private HashMap<String, String> attributes;

  private ArrayList<Configuration> childConfigurations;

  private Configuration parentConfiguration;
}
