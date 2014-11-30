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
import cc.redpen.formatter.PlainFormatter;
import cc.redpen.formatter.XMLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

/**
 * Factory class of ResultDistributor.
 */
public final class ResultDistributorFactory {
    private static final Logger LOG =
            LoggerFactory.getLogger(ResultDistributor.class);

    private ResultDistributorFactory() {
        // for safe
    }

    /**
     * Create ResultDistributor object.
     *
     * @param outputFormat syntax of output
     * @param output       output stream
     * @return ResultDistributor object when succeeded to create, null otherwise
     */
    public static ResultDistributor createDistributor(Formatter.Type outputFormat,
                                                      OutputStream output) {
        ResultDistributor distributor;
        LOG.info("Creating Distributor...");
        switch (outputFormat) {
            case PLAIN:
                distributor = new ResultDistributor(output, new PlainFormatter());
                break;
            case XML:
                distributor = new ResultDistributor(output, new XMLFormatter());
                break;
            default:
                throw new RuntimeException("There is not such formatter: " + outputFormat);
        }
        return distributor;
    }
}
