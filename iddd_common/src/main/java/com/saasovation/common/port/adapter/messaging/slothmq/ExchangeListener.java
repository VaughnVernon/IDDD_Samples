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

package com.saasovation.common.port.adapter.messaging.slothmq;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class ExchangeListener {

	private Set<String> messageTypes;

    public ExchangeListener() {
        super();

        this.establishMessageTypes();

    	SlothClient.instance().register(this);
    }

    public void close() {
    	SlothClient.instance().unregister(this);
    }

    protected abstract String exchangeName();

    protected abstract void filteredDispatch(String aType, String aTextMessage);

    protected abstract String[] listensTo();

	protected boolean listensTo(String aType) {
		Set<String> types = this.listensToMessageTypes();

		return types.isEmpty() || types.contains(aType);
	}

	protected abstract String name();

    private void establishMessageTypes() {
        String[] filterOutAllBut = this.listensTo();

        if (filterOutAllBut == null) {
            filterOutAllBut = new String[0];
        }

        this.setMessageTypes(new HashSet<String>(Arrays.asList(filterOutAllBut)));
    }

    private Set<String> listensToMessageTypes() {
    	return this.messageTypes;
    }

    private void setMessageTypes(HashSet<String> aMessageTypes) {
    	this.messageTypes = aMessageTypes;
	}
}
