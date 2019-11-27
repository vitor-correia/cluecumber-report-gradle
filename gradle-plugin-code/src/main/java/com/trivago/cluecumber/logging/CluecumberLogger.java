/*
 * Copyright 2019 trivago N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trivago.cluecumber.logging;

import com.trivago.cluecumberCore.logging.ICluecumberLogger;
import org.slf4j.Logger;

public class CluecumberLogger implements ICluecumberLogger {

    private Logger LOGGER;

    public CluecumberLogger(Logger logger) {
        this.LOGGER = logger;
    }

    @Override
    public void info(String msg) {
        LOGGER.info(msg);
    }

    @Override
    public void warn(String msg) {
        LOGGER.warn(msg);
    }

    @Override
    public void error(String msg) {
        LOGGER.warn(msg);
    }

}
