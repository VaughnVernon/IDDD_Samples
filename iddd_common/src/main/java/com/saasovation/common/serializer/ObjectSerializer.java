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

package com.saasovation.common.serializer;

import java.lang.reflect.Type;

public class ObjectSerializer extends AbstractSerializer {

    private static ObjectSerializer eventSerializer;

    public static synchronized ObjectSerializer instance() {
        if (ObjectSerializer.eventSerializer == null) {
            ObjectSerializer.eventSerializer = new ObjectSerializer();
        }

        return ObjectSerializer.eventSerializer;
    }

    public ObjectSerializer(boolean isCompact) {
        this(false, isCompact);
    }

    public ObjectSerializer(boolean isPretty, boolean isCompact) {
        super(isPretty, isCompact);
    }

    public <T extends Object> T deserialize(String aSerialization, final Class<T> aType) {
        T domainEvent = this.gson().fromJson(aSerialization, aType);

        return domainEvent;
    }

    public <T extends Object> T deserialize(String aSerialization, final Type aType) {
        T domainEvent = this.gson().fromJson(aSerialization, aType);

        return domainEvent;
    }

    public String serialize(Object anObject) {
        String serialization = this.gson().toJson(anObject);

        return serialization;
    }

    private ObjectSerializer() {
        this(false, false);
    }
}
