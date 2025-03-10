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

package com.trivago.cluecumberCore.exceptions;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * The base Cluecumber Report plugin exception that all exceptions extend.
 * Since this extends the {@link MojoExecutionException},
 * it will stop the plugin execution when thrown.
 */
public class CluecumberPluginException extends MojoExecutionException {
    /**
     * Constructor.
     *
     * @param message The error message for the exception.
     */
    public CluecumberPluginException(final String message) {
        super(message);
    }
}
