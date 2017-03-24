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

import cc.redpen.model.Section;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class HeaderLengthValidatorTest {
    private HeaderLengthValidator validator = new HeaderLengthValidator();
    @Test
    public void testSectionWithLongHeader() {
        Section section = new Section(0, "This is a long long long long" +
                " long long long long long long long long header");
        List<ValidationError> errors = new ArrayList<>();

        validator.setErrorList(errors);
        validator.validate(section);
        assertEquals(1, errors.size());
    }

    @Test
    public void testSectionWithLongHeaderInLowLevelSection() {
        Section section = new Section(5, "This is a long long long long" +
                " long long long long long long long long header");
        List<ValidationError> errors = new ArrayList<>();

        validator.setErrorList(errors);
        validator.validate(section);
        assertEquals(0, errors.size());
    }
}
