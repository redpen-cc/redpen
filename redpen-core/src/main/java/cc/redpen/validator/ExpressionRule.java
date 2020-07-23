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
package cc.redpen.validator;

import cc.redpen.tokenizer.TokenElement;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRule {
    private List<TokenElement> elements;

    public ExpressionRule() {
        this.elements = new ArrayList<>();
    }

    public ExpressionRule addElement(TokenElement element) {
        this.elements.add(element);
        return this;
    }

    public String toSurface() {
        String result = "";
        for (int i = 0; i < elements.size(); i++)
            result += elements.get(i).getSurface();
        return result;
    }

    @Override
    public String toString() {
        return "ExpressionRule{" +
                "elements=" + elements +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionRule that = (ExpressionRule) o;

        return !(elements != null ? !elements.equals(that.elements) : that.elements != null);

    }

    @Override
    public int hashCode() {
        return elements != null ? elements.hashCode() : 0;
    }

    public boolean match(List<TokenElement> tokens) {
        if (tokens.size() == 0) {
            return false;
        }
        for (int i = 0; i < tokens.size(); i++) {
            boolean result = true;
            for (int j = 0; j < elements.size(); j++) {
                if (tokens.size() <= i+j) {
                    result = false;
                    break;
                } else if (!tokens.get(i+j).getSurface().equals(elements.get(j).getSurface())) {
                    result = false;
                }
            }
            if (result) {
                return true;
            }
        }
        return false;
    }
}
