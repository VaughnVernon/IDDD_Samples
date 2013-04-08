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

package com.saasovation.collaboration.domain.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRepository;
import com.saasovation.collaboration.domain.model.calendar.CalendarIdentityService;
import com.saasovation.collaboration.domain.model.calendar.CalendarRepository;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.forum.DiscussionRepository;
import com.saasovation.collaboration.domain.model.forum.ForumIdentityService;
import com.saasovation.collaboration.domain.model.forum.ForumRepository;
import com.saasovation.collaboration.domain.model.forum.PostRepository;

public class DomainRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static CalendarIdentityService calendarIdentityService() {
        return (CalendarIdentityService) applicationContext.getBean("calendarIdentityService");
    }

    public static CalendarEntryRepository calendarEntryRepository() {
        return (CalendarEntryRepository) applicationContext.getBean("calendarEntryRepository");
    }

    public static CalendarRepository calendarRepository() {
        return (CalendarRepository) applicationContext.getBean("calendarRepository");
    }

    public static CollaboratorService collaboratorService() {
        return (CollaboratorService) applicationContext.getBean("collaboratorService");
    }

    public static DiscussionRepository discussionRepository() {
        return (DiscussionRepository) applicationContext.getBean("discussionRepository");
    }

    public static ForumIdentityService forumIdentityService() {
        return (ForumIdentityService) applicationContext.getBean("forumIdentityService");
    }

    public static ForumRepository forumRepository() {
        return (ForumRepository) applicationContext.getBean("forumRepository");
    }

    public static PostRepository postRepository() {
        return (PostRepository) applicationContext.getBean("postRepository");
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext anApplicationContext) throws BeansException {
        if (DomainRegistry.applicationContext == null) {
            DomainRegistry.applicationContext = anApplicationContext;
        }
    }
}
