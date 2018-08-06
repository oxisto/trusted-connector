/*-
 * ========================LICENSE_START=================================
 * ids-webconsole
 * %%
 * Copyright (C) 2018 Fraunhofer AISEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package de.fhg.aisec.ids.webconsole.api.helper;

import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(ProcessExecutor.class);

  public int execute(String[] cmd, OutputStream stdout, OutputStream stderr)
      throws InterruptedException, IOException {
    Runtime rt = Runtime.getRuntime();
    Process proc = rt.exec(cmd);

    StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), stderr);
    StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), stdout);

    errorGobbler.start();
    outputGobbler.start();

    return proc.waitFor();
  }
}
