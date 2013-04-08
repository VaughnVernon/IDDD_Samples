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

package com.saasovation.collaboration.domain.model.collaborator;

import java.io.Serializable;

public abstract class Collaborator
        implements Comparable<Collaborator>, Serializable {

    private static final long serialVersionUID = 1L;

    private String emailAddress;
    private String identity;
    private String name;

    public Collaborator(String anIdentity, String aName, String anEmailAddress) {
        this();

        this.setEmailAddress(anEmailAddress);
        this.setIdentity(anIdentity);
        this.setName(aName);
    }

    public String emailAddress() {
        return this.emailAddress;
    }

    public String identity() {
        return this.identity;
    }

    public String name() {
        return this.name;
    }

    @Override
    public int compareTo(Collaborator aCollaborator) {

        int diff = this.identity().compareTo(aCollaborator.identity());

        if (diff == 0) {
            diff = this.emailAddress().compareTo(aCollaborator.emailAddress());

            if (diff == 0) {
                diff = this.name().compareTo(aCollaborator.name());
            }
        }

        return diff;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Collaborator typedObject = (Collaborator) anObject;
            equalObjects =
                this.emailAddress().equals(typedObject.emailAddress()) &&
                this.identity().equals(typedObject.identity()) &&
                this.name().equals(typedObject.name());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (57691 * this.hashPrimeValue())
            + this.emailAddress().hashCode()
            + this.identity().hashCode()
            + this.name().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                " [emailAddress=" + emailAddress + ", identity=" + identity + ", name=" + name + "]";
    }

    protected Collaborator() {
        super();
    }

    protected abstract int hashPrimeValue();

    private void setEmailAddress(String anEmailAddress) {
        this.emailAddress = anEmailAddress;
    }

    private void setIdentity(String anIdentity) {
        this.identity = anIdentity;
    }

    private void setName(String aName) {
        this.name = aName;
    }
}
