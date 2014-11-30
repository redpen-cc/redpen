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
package cc.redpen.distributor;

import cc.redpen.formatter.Formatter;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;

import java.util.List;
import java.util.Map;

/**
 * FakeResultDistributor does nothing. this class is just for testing.
 */
public class FakeResultDistributor implements ResultDistributor {
    /**
     * Constructor.
     */
    public FakeResultDistributor() {
        super();
    }

    @Override
    public void setFormatter(Formatter formatter) {
    }

    @Override
    public void distribute(Map<Document, List<ValidationError>> docErrorsMap) {

    }
}
