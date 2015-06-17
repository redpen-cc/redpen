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
package cc.redpen.parser.latex;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 * Token data type.
 */
public class Token {
    public String t;
    public String v;
    public List<Token> p = new ArrayList<>();
    public Position pos;

    public Token(final String type, final String value, final Position pos) {
        this.t = type;
        this.v = value;
        this.pos = new Position(pos);
    }

    public Token(final String type, final char value, final Position pos) {
        this.t = type;
        this.v = String.valueOf(value);
        this.pos = new Position(pos);
    }

    public boolean equals(final Token other) {
        return (this.t == other.t && this.v == other.v && this.p.equals(other.p));
    }

    @Override
    public String toString() {
        return String.format("%s(%s) \"%s\" %s", this.t, this.pos, this.v, this.p);
    }
}
