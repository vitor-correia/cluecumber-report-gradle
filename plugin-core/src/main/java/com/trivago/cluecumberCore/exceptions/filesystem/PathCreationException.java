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

package com.trivago.cluecumberCore.exceptions.filesystem;

import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;

/**
 * Thrown when a path cannot be created.
 */
public class PathCreationException extends CluecumberPluginException {
    /**
     * Constructor.
     *
     * @param path The path to be created.
     */
    public PathCreationException(final String path) {
        super("Path '" + path + "' could not be created.");
    }
}
