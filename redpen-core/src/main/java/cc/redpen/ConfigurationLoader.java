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
package cc.redpen;

import cc.redpen.config.Configuration;
import cc.redpen.config.Symbol;
import cc.redpen.config.SymbolType;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.util.SAXErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static cc.redpen.config.Configuration.ConfigurationBuilder;

/**
 * Load the central configuration of {@link cc.redpen.RedPen}.
 */
public final class ConfigurationLoader {
    private static final Logger LOG =
            LoggerFactory.getLogger(ConfigurationLoader.class);
    private ConfigurationBuilder configBuilder = new ConfigurationBuilder();

    private static Symbol createSymbol(Element element) throws RedPenException {
        if (!element.hasAttribute("name") || !element.hasAttribute("value")) {
            throw new IllegalStateException("Found element does not have name and value attribute...");
        }
        String value = element.getAttribute("value");
        if (value.length() != 1) {
            throw new RedPenException("value should be one character, specified: " + value);
        }
        char charValue = value.charAt(0);
        return new Symbol(
                SymbolType.valueOf(element.getAttribute("name")),
                charValue,
                element.getAttribute("invalid-chars"),
                Boolean.parseBoolean(element.getAttribute("before-space")),
                Boolean.parseBoolean(element.getAttribute("after-space")));
    }


    /**
     * parse the input stream. stream will be closed.
     *
     * @param input stream
     * @return document object
     * @throws RedPenException when failed to parse
     */
    private static Document toDocument(InputStream input) throws RedPenException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try (BufferedInputStream bis = new BufferedInputStream(input)) {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.setErrorHandler(new SAXErrorHandler());
            return dBuilder.parse(bis);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RedPenException(e);
        }
    }

    /**
     * load {@link cc.redpen.RedPen} settings.
     *
     * @param configFile input configuration file
     * @return Validator configuration resources
     * @throws cc.redpen.RedPenException when failed to load configuration from specified configuration file
     */
    public Configuration load(File configFile) throws RedPenException {
        LOG.info("Loading config from specified config file: \"{}\"", configFile.getAbsolutePath());
        try (InputStream fis = new FileInputStream(configFile)) {
            return this.load(fis);
        } catch (IOException e) {
            throw new RedPenException(e);
        }
    }

    /**
     * load {@link cc.redpen.RedPen} settings.
     *
     * @param resourcePath input configuration path
     * @return Validator configuration resources
     * @throws cc.redpen.RedPenException when failed to load configuration from specified resource
     */
    public Configuration loadFromResource(String resourcePath) throws RedPenException {
        InputStream inputConfigStream = Configuration.class.getResourceAsStream(resourcePath);
        return load(inputConfigStream);
    }

    /**
     * load {@link cc.redpen.RedPen} settings.
     *
     * @param configString configuration as String
     * @return Validator configuration resources
     * @throws cc.redpen.RedPenException when failed to load Configuration from specified string
     */
    public Configuration loadFromString(String configString) throws RedPenException {
        return load(new ByteArrayInputStream(configString.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * load {@link cc.redpen.RedPen} configuration.
     * Provided stream will be closed.
     *
     * @param stream input configuration settings
     * @return Configuration loaded from input stream
     * @throws cc.redpen.RedPenException when failed to load configuration from specified stream
     */
    public Configuration load(InputStream stream) throws RedPenException {
        Document doc = toDocument(stream);

        configBuilder = new ConfigurationBuilder();
        Element rootElement = getRootNode(doc, "redpen-conf");

        Node langNode = rootElement.getAttributes().getNamedItem("lang");
        String language = "en";
        if (langNode != null) {
            language = langNode.getNodeValue();
            LOG.info("Language is set to \"{}\"", language);
        } else {
            LOG.warn("No language configuration...");
            LOG.info("Set language to en");
        }

        Node typeNode = rootElement.getAttributes().getNamedItem("type");
        Optional<String> type = Optional.empty();
        if (typeNode != null) {
            type = Optional.of(typeNode.getNodeValue());
            LOG.info("Type is set to \"{}\"", type.get());
        } else {
            LOG.warn("No type configuration...");
        }

        // extract validator configurations
        NodeList validatorConfigElementList =
                getSpecifiedNodeList(rootElement, "validators");
        if (validatorConfigElementList == null) {
            LOG.error("There is no validator-list block");
            return null;
        }
        NodeList validatorElementList =
                validatorConfigElementList.item(0).getChildNodes();
        if (validatorElementList == null) {
            LOG.error("There is no validator block");
            return null;
        }
        extractValidatorConfigurations(validatorElementList);

        // extract symbol configurations
        NodeList symbolTableConfigElementList =
                getSpecifiedNodeList(rootElement, "symbols");
        configBuilder.setLanguage(language);
        type.ifPresent(configBuilder::setType);

        if (symbolTableConfigElementList != null) {
            extractSymbolConfig(symbolTableConfigElementList, language);
        }
        return configBuilder.build();
    }

    private void extractValidatorConfigurations(NodeList validatorElementList) {
        ValidatorConfiguration currentConfiguration;
        for (int i = 0; i < validatorElementList.getLength(); i++) {
            Node nNode = validatorElementList.item(i);
            if (nNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) nNode;
            if (element.getNodeName().equals("validator")) {
                currentConfiguration =
                        new ValidatorConfiguration(element.getAttribute("name"));
                configBuilder.addValidatorConfig(currentConfiguration);
                NodeList propertyElementList = nNode.getChildNodes();
                extractProperties(currentConfiguration, propertyElementList);
            } else {
                LOG.warn("Invalid block: \"" + element.getNodeName() + "\"");
                LOG.warn("Skip this block ...");
            }
        }
    }

    private void extractProperties(ValidatorConfiguration currentConfiguration,
                                   NodeList propertyElementList) {
        for (int j = 0; j < propertyElementList.getLength(); j++) {
            Node pNode = propertyElementList.item(j);
            if (pNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element propertyElement = (Element) pNode;
            if (propertyElement.getNodeName().equals("property")
                    && currentConfiguration != null) {
                currentConfiguration.addAttribute(
                        propertyElement.getAttribute("name"),
                        propertyElement.getAttribute("value"));
            }
        }
    }

    private void extractSymbolConfig(NodeList symbolTableConfigElementList, String language) throws RedPenException {
        configBuilder.setLanguage(language);

        NodeList symbolTableElementList =
                getSpecifiedNodeList((Element)
                        symbolTableConfigElementList.item(0), "symbol");
        if (symbolTableElementList == null) {
            LOG.warn("there is no character block");
            return;
        }
        for (int temp = 0; temp < symbolTableElementList.getLength(); temp++) {
            Node nNode = symbolTableElementList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                Symbol currentSymbol = createSymbol(element);
                configBuilder.setSymbol(currentSymbol);
            }
        }
    }

    private NodeList getSpecifiedNodeList(Element rootElement, String elementName) {
        NodeList elementList =
                rootElement.getElementsByTagName(elementName);
        if (elementList.getLength() == 0) {
            LOG.info("No \"" +
                    elementName + "\" block found in the configuration");
            return null;
        } else if (elementList.getLength() > 1) {
            LOG.info("More than one \"" + elementName + " \" blocks in the configuration");
        }
        return elementList;
    }

    private Element getRootNode(Document doc, String rootTag) {
        doc.getDocumentElement().normalize();
        NodeList rootConfigElementList =
                doc.getElementsByTagName(rootTag);
        if (rootConfigElementList.getLength() == 0) {
            throw new IllegalStateException("No \"" + rootTag
                    + "\" block found in the configuration");
        } else if (rootConfigElementList.getLength() > 1) {
            LOG.warn("More than one \"" +
                    rootTag + "\" blocks in the configuration");
        }
        Node root = rootConfigElementList.item(0);
        Element rootElement = (Element) root;
        LOG.info("Succeeded to load configuration file");
        return rootElement;
    }
}
