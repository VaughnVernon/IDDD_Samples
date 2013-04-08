//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.collaboration.port.adapter.service;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.saasovation.collaboration.domain.model.collaborator.Collaborator;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

public class HttpUserInRoleAdapter implements UserInRoleAdapter {

    private static final String HOST = "localhost";
    private static final String PORT = "8081";
    private static final String PROTOCOL = "http";
    private static final String URL_TEMPLATE =
            "/idovation/tenants/{tenantId}/users/{username}/inRole/{role}";

    public HttpUserInRoleAdapter() {
        super();
    }

    public <T extends Collaborator> T toCollaborator(
            Tenant aTenant,
            String anIdentity,
            String aRoleName,
            Class<T> aCollaboratorClass) {

        T collaborator = null;

        try {
            ClientRequest request =
                    this.buildRequest(aTenant, anIdentity, aRoleName);

            ClientResponse<String> response = request.get(String.class);

            if (response.getStatus() == 200) {
                collaborator =
                    new CollaboratorTranslator()
                        .toCollaboratorFromRepresentation(
                            response.getEntity(),
                            aCollaboratorClass);
            } else if (response.getStatus() == 204) {
                ; // not an error, return null
            } else {
                throw new IllegalStateException(
                        "There was a problem requesting the user: "
                        + anIdentity
                        + " in role: "
                        + aRoleName
                        + " with resulting status: "
                        + response.getStatus());
            }

        } catch (Throwable t) {
            throw new IllegalStateException(
                    "Failed because: " + t.getMessage(), t);
        }

        return collaborator;
    }

    private ClientRequest buildRequest(
            Tenant aTenant,
            String anIdentity,
            String aRoleName) {

        ClientRequest request =
            new ClientRequest(this.buildURLFor(URL_TEMPLATE));

        request.pathParameter("tenantId", aTenant.id());
        request.pathParameter("username", anIdentity);
        request.pathParameter("role", aRoleName);

        return request;
    }

    private String buildURLFor(String aTemplate) {
        String url =
            PROTOCOL
            + "://"
            + HOST + ":" + PORT
            + aTemplate;

        return url;
    }
}
