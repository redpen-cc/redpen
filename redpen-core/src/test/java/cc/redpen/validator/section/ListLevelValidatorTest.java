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
package cc.redpen.validator.section;

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListLevelValidatorTest {

    private ListLevelValidator validator;

    @BeforeEach
    void setUp() throws RedPenException {
        validator = new ListLevelValidator();
        validator.preInit(new ValidatorConfiguration("ListLevel").addProperty("max_level", 3), Configuration.builder().build());
    }

    @Test
    void testValid() throws Exception {
        Section section = new Section(0, "header");
        section.appendListBlock();
        section.appendListElement(1, Arrays.asList(new Sentence("item1", 1)));
        section.appendListElement(2, Arrays.asList(new Sentence("item2", 1)));
        section.appendListElement(3, Arrays.asList(new Sentence("item3", 1)));
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(section);
        assertEquals(0, errors.size());
    }

    @Test
    void testInvalid() throws Exception {
        Section section = new Section(0, "header");
        section.appendListBlock();
        section.appendListElement(1, Arrays.asList(new Sentence("item1", 1)));
        section.appendListElement(2, Arrays.asList(new Sentence("item2", 1)));
        section.appendListElement(3, Arrays.asList(new Sentence("item3", 1)));
        section.appendListElement(4, Arrays.asList(new Sentence("item4", 1)));
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(section);
        assertEquals(1, errors.size());
    }

}
