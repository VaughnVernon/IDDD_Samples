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

package com.saasovation.common.media;

public class Link {

    private String href;
    private String rel;
    private String title;
    private String type;

    public Link(String anHref, String aRel) {
        this(anHref, aRel, null, null);
    }

    public Link(
            String anHref,
            String aRel,
            String aTitle,
            String aType) {

        this();

        this.setHref(anHref);
        this.setRelationship(aRel);
        this.setTitle(aTitle);
        this.setType(aType);
    }

    public String href() {
        return this.getHref();
    }

    public String getHref() {
        return this.href;
    }

    public String rel() {
        return this.getRel();
    }

    public String getRel() {
        return this.rel;
    }

    public String title() {
        return this.getTitle();
    }

    public String getTitle() {
        return this.title;
    }

    public String type() {
        return this.getType();
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {

        // see http://www.w3.org/Protocols/9707-link-header.html

        StringBuilder builder = new StringBuilder();

        builder
            .append("<")
            .append(this.getHref())
            .append(">; rel=")
            .append(this.getRel());

        // title is optional
        if (this.getTitle() != null) {
            builder
                .append("; title=")
                .append(this.getTitle());
        }

        // per LINK extension, type is optionally permitted
        if (this.getType() != null) {
            builder
                .append("; type=")
                .append(this.getType());
        }

        return builder.toString();
    }

    protected Link() {
        super();
    }

    private void setHref(String anHref) {
        if (anHref == null) {
            throw new IllegalArgumentException("Href must not be null.");
        }
        this.href = anHref;
    }

    private void setRelationship(String aRel) {
        if (aRel == null) {
            throw new IllegalArgumentException("Rel must not be null.");
        }
        this.rel = aRel;
    }

    private void setTitle(String aTitle) {
        this.title = aTitle;
    }

    private void setType(String aType) {
        this.type = aType;
    }
}
