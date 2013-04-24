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

package com.saasovation.common.notification;

import com.saasovation.common.domain.model.DomainEvent;

public class TestableNullPropertyDomainEvent extends DomainEvent {

    private Long id;
    private String name;
    private Integer numberMustBeNull;
    private String textMustBeNull;
    private String textMustBeNull2;
    private Nested nested;
    private Nested nullNested;

    public TestableNullPropertyDomainEvent(Long anId, String aName) {
	super();

	this.setId(anId);
	this.setName(aName);

	this.nested = new Nested();
	this.nullNested = null;
    }

    public Long id() {
	return id;
    }

    public Nested nested() {
	return nested;
    }

    public Nested nullNested() {
	return nullNested;
    }

    public Integer numberMustBeNull() {
	return numberMustBeNull;
    }

    public String textMustBeNull() {
	return textMustBeNull;
    }

    public String textMustBeNull2() {
	return textMustBeNull2;
    }

    public String name() {
	return name;
    }

    private void setId(Long id) {
	this.id = id;
    }

    private void setName(String name) {
	this.name = name;
    }

    public static class Nested {
	private String nestedTextMustBeNull;

	private NestedDeeply nestedDeeply;
	private NestedDeeply nullNestedDeeply;

	public Nested() {
	    super();

	    this.nestedDeeply = new NestedDeeply();
	    this.nullNestedDeeply = null;
	}

	public NestedDeeply nestedDeeply() {
	    return nestedDeeply;
	}

	public NestedDeeply nullNestedDeeply() {
	    return nullNestedDeeply;
	}

	public String nestedTextMustBeNull() {
	    return nestedTextMustBeNull;
	}
    }

    public static class NestedDeeply {
	private String nestedDeeplyTextMustBeNull;
	private String nestedDeeplyTextMustBeNull2;

	public NestedDeeply() {
	    super();
	}

	public String nestedDeeplyTextMustBeNull() {
	    return nestedDeeplyTextMustBeNull;
	}

	public String nestedDeeplyTextMustBeNull2() {
	    return nestedDeeplyTextMustBeNull2;
	}
    }
}
