/*-
 * ========================LICENSE_START=================================
 * rat-repository
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
package de.fhg.ids.attestation;

import de.fhg.aisec.ids.messages.AttestationProtos.Pcr;

public class Configuration {
  private long id;
  private String name;
  private String type;
  private Pcr[] values;

  public Configuration(long id, String name, String type, Pcr[] values) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.values = values.clone();
  }

  public Configuration(long id, String name, String type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  public Pcr[] getValues() {
    return values.clone();
  }

  public void setValues(Pcr[] values) {
    this.values = values.clone();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
