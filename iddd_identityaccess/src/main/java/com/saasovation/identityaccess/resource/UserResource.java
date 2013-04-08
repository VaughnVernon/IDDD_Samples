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

package com.saasovation.identityaccess.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.saasovation.common.media.OvationsMediaType;
import com.saasovation.common.serializer.ObjectSerializer;
import com.saasovation.identityaccess.application.command.AuthenticateUserCommand;
import com.saasovation.identityaccess.application.representation.UserInRoleRepresentation;
import com.saasovation.identityaccess.application.representation.UserRepresentation;
import com.saasovation.identityaccess.domain.model.identity.User;
import com.saasovation.identityaccess.domain.model.identity.UserDescriptor;

@Path("/tenants/{tenantId}/users")
public class UserResource extends AbstractResource {

    public UserResource() {
        super();
    }

    @GET
    @Path("{username}/autenticatedWith/{password}")
    @Produces({ OvationsMediaType.ID_OVATION_TYPE })
    public Response getAuthenticUser(
            @PathParam("tenantId") String aTenantId,
            @PathParam("username") String aUsername,
            @PathParam("password") String aPassword,
            @Context Request aRequest) {

        UserDescriptor userDescriptor =
                this.identityApplicationService()
                    .authenticateUser(
                            new AuthenticateUserCommand(
                                    aTenantId,
                                    aUsername,
                                    aPassword));

        if (userDescriptor.isNullDescriptor()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Response response = this.userDescriptorResponse(aRequest, userDescriptor);

        return response;
    }

    @GET
    @Path("{username}")
    @Produces({ OvationsMediaType.ID_OVATION_TYPE })
    public Response getUser(
            @PathParam("tenantId") String aTenantId,
            @PathParam("username") String aUsername,
            @Context Request aRequest) {

        User user = this.identityApplicationService().user(aTenantId, aUsername);

        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Response response = this.userResponse(aRequest, user);

        return response;
    }

    @GET
    @Path("{username}/inRole/{role}")
    @Produces({ OvationsMediaType.ID_OVATION_TYPE })
    public Response getUserInRole(
            @PathParam("tenantId") String aTenantId,
            @PathParam("username") String aUsername,
            @PathParam("role") String aRoleName) {

        Response response = null;

        User user = null;

        try {
            user = this.accessApplicationService()
                       .userInRole(
                               aTenantId,
                               aUsername,
                               aRoleName);
        } catch (Exception e) {
            // fall through
        }

        if (user != null) {
            response = this.userInRoleResponse(user, aRoleName);
        } else {
            response = Response.noContent().build();
        }

        return response;
    }

    private Response userDescriptorResponse(
            Request aRequest,
            UserDescriptor aUserDescriptor) {

        Response response = null;

        String representation = ObjectSerializer.instance().serialize(aUserDescriptor);

        response =
            Response
                .ok(representation)
                .cacheControl(this.cacheControlFor(30))
                .build();

        return response;
    }

    private Response userInRoleResponse(User aUser, String aRoleName) {

        UserInRoleRepresentation userInRoleRepresentation =
                new UserInRoleRepresentation(aUser, aRoleName);

        String representation =
                ObjectSerializer
                    .instance()
                    .serialize(userInRoleRepresentation);

        Response response =
                Response
                    .ok(representation)
                    .cacheControl(this.cacheControlFor(60))
                    .build();

        return response;
    }

    private Response userResponse(Request aRequest, User aUser) {

        Response response = null;

        EntityTag eTag = this.userETag(aUser);

        ResponseBuilder conditionalBuilder = aRequest.evaluatePreconditions(eTag);

        if (conditionalBuilder != null) {
            response =
                    conditionalBuilder
                        .cacheControl(this.cacheControlFor(3600))
                        .tag(eTag)
                        .build();
        } else {
            String representation =
                    ObjectSerializer
                        .instance()
                        .serialize(new UserRepresentation(aUser));

            response =
                    Response
                        .ok(representation)
                        .cacheControl(this.cacheControlFor(3600))
                        .tag(eTag)
                        .build();
        }

        return response;
    }
}
