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

package com.saasovation.collaboration.domain.model.calendar;

import java.util.Date;

import com.saasovation.common.AssertionConcern;

public final class TimeSpan extends AssertionConcern {

    private Date begins;
    private Date ends;

    public TimeSpan(Date aBegins, Date anEnds) {
        super();

        this.assertCorrectTimeSpan(aBegins, anEnds);

        this.setBegins(aBegins);
        this.setEnds(anEnds);
    }

    public Date begins() {
        return this.begins;
    }

    public Date ends() {
        return this.ends;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            TimeSpan typedObject = (TimeSpan) anObject;
            equalObjects =
                this.begins().equals(typedObject.begins()) &&
                this.ends().equals(typedObject.ends());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
                + (1653 * 89)
                + this.begins().hashCode()
                + this.ends().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "TimeSpan [begins=" + begins + ", ends=" + ends + "]";
    }

    private void assertCorrectTimeSpan(Date aBegins, Date anEnds) {
        this.assertArgumentNotNull(aBegins, "Must provide begins.");
        this.assertArgumentNotNull(anEnds, "Must provide ends.");
        this.assertArgumentFalse(aBegins.after(anEnds), "Time span must not end before it begins.");
    }

    private void setBegins(Date aBegins) {
        this.begins = aBegins;
    }

    private void setEnds(Date anEnds) {
        this.ends = anEnds;
    }
}
