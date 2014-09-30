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
package cc.redpen;

import org.junit.Ignore;
import org.junit.Test;

public class MainTest {

    @Ignore("Enable this to run basic validation test during development ")
    @Test
    public void testMain() throws RedPenException {
        String[] args = new String[]{
            "-c", "redpen-cli/sample/conf/redpen-conf-en.xml",
            "redpen-cli/sample/doc/txt/en/sampledoc-en.txt"
        };
        
        Main.main(args);
    }

    @Ignore("Enable this to show help")
    @Test
    public void testHelp() throws RedPenException {
        Main.main(new String[] { "-h" });
    }

    @Ignore("Enable this to show version")
    @Test
    public void testVersion() throws RedPenException {
        Main.main(new String[] { "-v" });
    }

}
