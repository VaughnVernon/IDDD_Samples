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

package com.saasovation.identityaccess.domain.model;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.spring.SpringHibernateSessionProvider;

public abstract class DomainTest extends TestCase {

    protected ApplicationContext applicationContext;
    private SpringHibernateSessionProvider sessionProvider;
    private Transaction transaction;

    protected DomainTest() {
        super();
    }

    protected Session session() {
        Session session = this.sessionProvider.session();

        return session;
    }

    protected void setUp() throws Exception {

        applicationContext =
                new ClassPathXmlApplicationContext(
                        new String[] {
                                "applicationContext-identityaccess.xml",
                                "applicationContext-common.xml" });

        this.sessionProvider =
                (SpringHibernateSessionProvider) applicationContext.getBean("sessionProvider");

        this.setTransaction(this.session().beginTransaction());

        DomainEventPublisher.instance().reset();

        System.out.println(">>>>>>>>>>>>>>>>>>>> " + this.getName());

        super.setUp();
    }

    protected void tearDown() throws Exception {

        this.transaction().rollback();

        this.setTransaction(null);

        this.session().clear();

        System.out.println("<<<<<<<<<<<<<<<<<<<< (done)");

        super.tearDown();
    }

    protected Transaction transaction() {
        return transaction;
    }

    private void setTransaction(Transaction aTransaction) {
        this.transaction = aTransaction;
    }
}
