/*-
 * ========================LICENSE_START=================================
 * IDS Core Platform Webconsole
 * %%
 * Copyright (C) 2017 Fraunhofer AISEC
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
package de.fhg.aisec.ids.webconsole.api;

import de.fhg.aisec.ids.api.conm.IDSCPIncomingConnection;
import de.fhg.aisec.ids.api.conm.IDSCPOutgoingConnection;
import de.fhg.aisec.ids.api.conm.IDSCPServerEndpoint;
import de.fhg.aisec.ids.webconsole.WebConsoleComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST API interface for managing connections from and to the connector.
 * <p>
 * The API will be available at st/<method>.
 *
 * @author Gerd Brost (gerd.brost@aisec.fraunhofer.de)
 */
@Path("/connections")
@Api("Connections")
public class ConnectionAPI {

    @GET
    @Path("/incoming")
    @ApiOperation(value = "Returns a list of all inbound connections", response = IDSCPIncomingConnection.class,
            responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IDSCPIncomingConnection> getIncoming() {
        return WebConsoleComponent.getConnectionManagerOrThrowSUE().listIncomingConnections();
    }

    @GET
    @Path("/outgoing")
    @ApiOperation(value = "Returns a list of all outbound connections", response = IDSCPOutgoingConnection.class,
            responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IDSCPOutgoingConnection> getOutgoing() {
        return WebConsoleComponent.getConnectionManagerOrThrowSUE().listOutgoingConnections();
    }

    @GET
    @Path("/endpoints")
    @ApiOperation(value = "Returns a list of all endpoints provided by this connector",
            response = IDSCPServerEndpoint.class, responseContainer = "List")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IDSCPServerEndpoint> getAvailableEndpoints() {
        return WebConsoleComponent.getConnectionManagerOrThrowSUE().listAvailableEndpoints();
    }


}

