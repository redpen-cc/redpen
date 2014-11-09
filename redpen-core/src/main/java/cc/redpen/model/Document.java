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
package cc.redpen.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Document represents a file with many elements
 * such as sentences, lists and headers.
 */
public class Document implements Iterable<Section> {
    private final List<Section> sections;
    private Optional<String> fileName;

    /**
     * Constructor.
     */
    public Document() {
        super();
        sections = new ArrayList<>();
        fileName = Optional.empty();
    }

    /**
     * Add a Section.
     *
     * @param section a section in file content
     */
    public void appendSection(Section section) {
        sections.add(section);
    }

    /**
     * Get last Section.
     *
     * @return last section in the Document
     */
    public Section getLastSection() {
        Section section = null;
        if (sections.size() > 0) {
            section = sections.get(sections.size() - 1);
        }
        return section;
    }

    /**
     * Get the size of sections in the file.
     *
     * @return size of sections
     */
    public int getNumberOfSections() {
        return sections.size();
    }

    /**
     * Get the specified section.
     *
     * @param id section id
     * @return a section with specified id
     */
    public Section getSection(int id) {
        return sections.get(id);
    }

    /**
     * Get file name.
     *
     * @return file name
     */
    public Optional<String> getFileName() {
        return fileName;
    }

    /**
     * Set file name.
     *
     * @param name file name
     */
    public void setFileName(String name) {
        this.fileName = Optional.of(name);
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }
}
