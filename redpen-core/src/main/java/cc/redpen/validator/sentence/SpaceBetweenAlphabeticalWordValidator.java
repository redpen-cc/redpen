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
package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.util.StringUtils;
import cc.redpen.validator.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.redpen.config.SymbolType.*;

public class SpaceBetweenAlphabeticalWordValidator extends Validator {
    private char leftParenthesis = '(';
    private char rightParenthesis = ')';
    private char comma = ',';

    private final String shard = "[^A-Za-z0-9 !@#$%^&*()_+=\\[\\]\\\\{}|=<>,.{};':\",./<>?（）［］｛｝-]";
    private final String word = "[A-Za-z0-9 !@#$%^&*()_+=\\[\\]\\\\{}|=<>,.{};':\",./<>?（）｛｝［］-]+";
    private final Pattern pat = Pattern.compile(shard + "\\s+(" + word + ")\\s+" + shard);

    public SpaceBetweenAlphabeticalWordValidator() {
        super("forbidden", false); // Spaces are enforced (false) or forbidden (true)
    }

    @Override public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage(), Locale.CHINESE.getLanguage());
    }

    @Override
    public void validate(Sentence sentence) {
        if (!getBoolean("forbidden")) {
            char prevCharacter = ' ';
            int idx = 0;
            for (char character : sentence.getContent().toCharArray()) {
                if (notHasWhiteSpaceBeforeLeftParenthesis(prevCharacter, character)) {
                    addLocalizedErrorWithPosition("Before", sentence, idx, idx + 1);
                } else if (
                        notHasWhiteSpaceAfterRightParenthesis(prevCharacter, character)) {
                    addLocalizedErrorWithPosition("After", sentence, idx, idx + 1);
                }
                prevCharacter = character;
                idx++;
            }
        } else {
            final Matcher m = pat.matcher(sentence.getContent());
            while (m.find()) {
                final String word = m.group(1);
                if (!word.contains(" ")) {
                    addLocalizedError("Forbidden", sentence, word);
                }
            }
        }
    }

    // TODO: need refactoring...
    private boolean notHasWhiteSpaceBeforeLeftParenthesis(char prevCharacter, char character) {
        return !StringUtils.isBasicLatin(prevCharacter)
                && prevCharacter != leftParenthesis
                && prevCharacter != comma
                && (prevCharacter != rightParenthesis && rightParenthesis != '（') // For handling multi-byte Parenthesis
                && StringUtils.isBasicLatin(character)
                && Character.isLetter(character);
    }

    private boolean notHasWhiteSpaceAfterRightParenthesis(char prevCharacter, char character) {
        return !StringUtils.isBasicLatin(character)
                && character != rightParenthesis
                && (character != leftParenthesis && leftParenthesis != '（')  // For handling multi-byte Parenthesis
                && character != comma
                && StringUtils.isBasicLatin(prevCharacter)
                && Character.isLetter(prevCharacter);
    }

    @Override
    protected void init() throws RedPenException {
        leftParenthesis = getSymbolTable().getSymbol(LEFT_PARENTHESIS).getValue();
        rightParenthesis = getSymbolTable().getSymbol(RIGHT_PARENTHESIS).getValue();
        comma = getSymbolTable().getSymbol(COMMA).getValue();
    }
}
