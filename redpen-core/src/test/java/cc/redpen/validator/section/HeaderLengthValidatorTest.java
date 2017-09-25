/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import cc.redpen.validator.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderLengthValidatorTest {
    @Test
    void testSectionWithLongHeader() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("HeaderLength")).build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        Section section = new Section(0, "This is a long long long long" +
                " long long long long long long long long header");
        List<ValidationError> errors = new ArrayList<>();

        validator.setErrorList(errors);
        validator.validate(section);
        assertEquals(1, errors.size());
    }

    @Test
    void testSpecifyTheMaxLength() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("HeaderLength").addProperty("max_len", "10")).build();
        HeaderLengthValidator validator = (HeaderLengthValidator) ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Section(1, "This is not a header"));
        assertEquals(1, errors.size());
    }

    @Test
    void testSectionWithLongHeaderInLowLevelSection() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("HeaderLength")).build();
        Validator validator = ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        Section section = new Section(5, "This is a long long long long" +
                " long long long long long long long long header");
        List<ValidationError> errors = new ArrayList<>();

        validator.setErrorList(errors);
        validator.validate(section);
        assertEquals(0, errors.size());
    }

    @Test
    void testSpecifyTheMinimumLevel() throws RedPenException {
        Configuration config = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("HeaderLength").addProperty("min_level", "2")).build();
        HeaderLengthValidator validator = (HeaderLengthValidator) ValidatorFactory.getInstance(config.getValidatorConfigs().get(0), config);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(new Section(5, "This is a long long long long" +
                " long long long long long long long long header"));
        assertEquals(0, errors.size());
    }
}
