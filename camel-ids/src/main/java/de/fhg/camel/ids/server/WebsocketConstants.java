/*-
 * ========================LICENSE_START=================================
 * camel-ids
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
package de.fhg.camel.ids.server;

public final class WebsocketConstants {

  public static final int DEFAULT_PORT = 9292;
  public static final String DEFAULT_HOST = "0.0.0.0";

  public static final String CONNECTION_KEY = "websocket.connectionKey";
  public static final String SEND_TO_ALL = "websocket.sendToAll";

  public static final String WS_PROTOCOL = "ws";
  public static final String WSS_PROTOCOL = "wss";

  private WebsocketConstants() {};
}
