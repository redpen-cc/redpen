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

import org.junit.jupiter.api.Test;
import static cc.redpen.parser.latex.Tools.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.pegdown.ast.*;

class ToolsTest {
    @Test
    void testSummary() {
        final Node node = new RootNode();
        node.getChildren().add(new HeaderNode(1, new TextNode("First section")));

        assertEquals(
            "(RootNode (HeaderNode#1 (TextNode 'First section')))",
            summary(node));
    }

    /* <1>part,<2>chapter,<3>section,<4>subsection,<5>subsubsection */
    @Test
    void testSummary2() {
        final Node node = new RootNode();
        node.getChildren().add(new HeaderNode(5));

        assertEquals(
            "(RootNode (HeaderNode#5 ()))",
            summary(node));
    }

    @Test
    void testSummary3() {
        final Node node = new RootNode();
        node.getChildren().add(new SimpleNode(SimpleNode.Type.Linebreak));

        assertEquals(
            "(RootNode (SimpleNode#Linebreak))",
            summary(node));
    }
}
