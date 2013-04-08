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
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.Cache;

import com.saasovation.common.media.OvationsMediaType;
import com.saasovation.common.serializer.ObjectSerializer;
import com.saasovation.identityaccess.application.ApplicationServiceRegistry;
import com.saasovation.identityaccess.application.IdentityApplicationService;
import com.saasovation.identityaccess.domain.model.identity.Tenant;

@Path("/tenants")
public class TenantResource {

    public TenantResource() {
        super();
    }

    @GET
    @Path("{tenantId}")
    @Produces({ OvationsMediaType.ID_OVATION_TYPE })
    @Cache(maxAge=3600)
    public Response getTenant(
            @PathParam("tenantId") String aTenantId) {

        Tenant tenant = this.identityApplicationService().tenant(aTenantId);

        if (tenant == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        String tenantRepresentation = ObjectSerializer.instance().serialize(tenant);

        Response response = Response.ok(tenantRepresentation).build();

        return response;
    }

    private IdentityApplicationService identityApplicationService() {
        return ApplicationServiceRegistry.identityApplicationService();
    }
}
