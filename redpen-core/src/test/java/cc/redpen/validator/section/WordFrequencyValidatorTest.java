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
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.ValidatorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WordFrequencyValidatorTest {

    @Test
    public void testDocument() throws RedPenException {
        WordFrequencyValidator validator = (WordFrequencyValidator) ValidatorFactory.getInstance("WordFrequency");

        String text[] = {
                "Mesa Verde National Park is a U.S. National Park and UNESCO World Heritage Site located in Montezuma County, Colorado.",
                "It protects some of the best preserved Ancestral Puebloan archeological sites in the United States.",
                "The park was created by President Theodore Roosevelt in 1906.",
                "It occupies 52,485 acres (21,240 ha) near the Four Corners region, and with more than 4,000 sites and 600 cliff dwellings, it is the largest archeological preserve in the US.",
                "Mesa Verde (Spanish for 'green table') is best known for structures such as Cliff Palace, thought to be the largest cliff dwelling in North America.",
                "Starting c. 7,500 BCE, Mesa Verde was seasonally inhabited by a group of nomadic Paleo-Indians known as the Foothills Mountain Complex.",
                "The variety of projectile points found in the region indicates they were influenced by surrounding areas, including the Great Basin, the San Juan Basin, and the Rio Grande Valley.",
                "Later, Archaic people established semi-permanent rockshelters in and around the mesa.",
                "By 1,000, the Basketmaker culture emerged from the local Archaic population, and by 750 CE the Ancestral Puebloans had developed from the Basketmaker culture.",
                "The Mesa Verdeans survived by utilizing a combination of hunting, gathering, and subsistence farming of crops such as corn, beans, and squash.",
                "They built the mesa's first pueblos sometime after 650, and by the end of the 12th century they began to construct the massive cliff dwellings for which the park is best known.",
                "By 1285, following a period of social and environmental instability driven by a series of severe and prolonged droughts, they abandoned the area and moved south to locations in Arizona and New Mexico, including Rio Chama, Pajarito Plateau, and Santa Fe."
        };

        Document.DocumentBuilder builder = Document.builder(new WhiteSpaceTokenizer()).addSection(1).addParagraph();
        int sentenceNumber = 1;
        for (String line : text) {
            builder.addSentence(new Sentence(line, sentenceNumber++));
        }
        Document document = builder.build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document);

        assertEquals(1, errors.size());
    }
}
