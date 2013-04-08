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

package com.saasovation.identityaccess.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationServiceRegistry implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;

    public static AccessApplicationService accessApplicationService() {
        return (AccessApplicationService) applicationContext.getBean("accessApplicationService");
    }

    public static IdentityApplicationService identityApplicationService() {
        return (IdentityApplicationService) applicationContext.getBean("identityApplicationService");
    }

    public static NotificationApplicationService notificationApplicationService() {
        return (NotificationApplicationService) applicationContext.getBean("notificationApplicationService");
    }

    @Override
    public synchronized void setApplicationContext(
            ApplicationContext anApplicationContext)
    throws BeansException {

        if (ApplicationServiceRegistry.applicationContext == null) {
            ApplicationServiceRegistry.applicationContext = anApplicationContext;
        }
    }
}
