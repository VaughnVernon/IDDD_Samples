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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

import com.saasovation.identityaccess.application.ApplicationServiceTest;
import com.saasovation.identityaccess.resource.GroupResource;
import com.saasovation.identityaccess.resource.NotificationResource;
import com.saasovation.identityaccess.resource.TenantResource;
import com.saasovation.identityaccess.resource.UserResource;

public abstract class ResourceTestCase extends ApplicationServiceTest {

    protected final static int PORT = 8081;

    private TJWSEmbeddedJaxrsServer server;

    protected ResourceTestCase() {
        super();
    }

    protected void dumpHeaders(MultivaluedMap<String, String> aResponseHeaders) {
        for (String key : aResponseHeaders.keySet()) {
            System.out.print(key + ":");
            String sep = " ";
            for (String value : aResponseHeaders.get(key)) {
                System.out.print(sep);
                System.out.print(value);
                sep = ", ";
            }

            System.out.println();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();

        this.setUpEmbeddedServer();
    }

    protected void tearDown() throws Exception {
        this.getServer().stop();

        this.setServer(null);

        super.tearDown();
    }

    private void setUpEmbeddedServer() {
        TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();

        server.setPort(PORT);
        server.getDeployment().setApplication(new ResourceTestCaseApplication());
        server.getDeployment().getActualResourceClasses().add(GroupResource.class);
        server.getDeployment().getActualResourceClasses().add(NotificationResource.class);
        server.getDeployment().getActualResourceClasses().add(TenantResource.class);
        server.getDeployment().getActualResourceClasses().add(UserResource.class);

        server.start();

        this.setServer(server);
    }

    private TJWSEmbeddedJaxrsServer getServer() {
        return server;
    }

    private void setServer(TJWSEmbeddedJaxrsServer aServer) {
        this.server = aServer;
    }

    private static class ResourceTestCaseApplication extends Application {

        public ResourceTestCaseApplication() {
            super();
        }

        @Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> classes = new HashSet<Class<?>>();
            return classes;
        }

        @Override
        public Set<Object> getSingletons() {
            Set<Object> singletons = new HashSet<Object>();
            return singletons;
        }
    }
}
