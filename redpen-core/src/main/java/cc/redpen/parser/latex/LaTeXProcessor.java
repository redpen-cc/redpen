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

import org.pegdown.ast.RootNode;
import org.apache.commons.lang3.StringUtils;

/**
 * Lame Pegdown adapter for the LaTeX parser.
 */
public class LaTeXProcessor {
    public RootNode parse(char[] stream) {
        final RootNode o = new RootNode();
        new StreamParser(StringUtils.join(stream, ""), (final Token t) -> {
                System.out.println(t.toString());
        }).parse();
        return o;
    }
}
