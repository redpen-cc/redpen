/*
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

package org.bigram.docvalidator.server.util;

import org.bigram.docvalidator.ConfigurationLoader;
import org.bigram.docvalidator.config.CharacterTable;
import org.bigram.docvalidator.config.CharacterTableLoader;
import org.bigram.docvalidator.config.Configuration;
import org.bigram.docvalidator.config.ValidationConfigurationLoader;
import org.bigram.docvalidator.config.ValidatorConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

/**
 * Loads the configuration files as resource streams.
 */
public class ServerConfigurationLoader extends ConfigurationLoader {

  @Override
  public Configuration loadConfiguration(InputStream stream) {
    Document doc = parseConfigurationString(stream);

    doc.getDocumentElement().normalize();
    NodeList rootConfig = doc.getElementsByTagName("configuration");
    Element root = (Element) rootConfig.item(0);

    NodeList validatorConfig = root.getElementsByTagName("validator");

    ValidatorConfiguration vc = ValidationConfigurationLoader.loadConfiguration(
      getClass()
        .getClassLoader()
        .getResourceAsStream("/" + validatorConfig.item(0).getTextContent())
    );

    Node langConfig = root.getElementsByTagName("lang").item(0);
    String language = langConfig.getTextContent();

    NamedNodeMap attributes = langConfig.getAttributes();
    String characterTablePath = attributes.getNamedItem("char-conf")
      .getNodeValue();

    CharacterTable characterTable = CharacterTableLoader.load(
      getClass()
        .getClassLoader()
        .getResourceAsStream("/" + characterTablePath),
      language);

    return new Configuration.Builder()
        .addRootValidatorConfig(vc)
        .setCharacterTable(characterTable).build();
  }
}
