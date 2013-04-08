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

package com.saasovation.common.media.canonical;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

// TODO: this is incomplete

public class CanonicalDataFormatter {

    public CanonicalDataFormatter() {
        super();
    }

    public byte[] format(Object anObject) {
        for (Field field : anObject.getClass().getFields()) {
            field.setAccessible(true);
            byte[] formattedField = this.formatField(field, anObject);
            if (formattedField.length > 0)
                ; // TODO
        }

        return null;
    }

    private byte[] formatField(Field aField, Object anObject) {
        byte[] formatted = null;

        if ((aField.getModifiers() & Modifier.STATIC) == 0) {
            if (aField.getType().isPrimitive()) {
                formatted = this.formatPrimitive(aField, anObject);
            } else {
                formatted = this.formatObject(aField, anObject);
            }
        }

        return formatted;
    }

    private byte[] formatObject(Field aField, Object anObject) {
//        String name = aField.getName();
//        Object value;
//
//        try {
//            value = aField.get(anObject);
//        } catch (Throwable t) {
//            throw new IllegalArgumentException("The object cannot be formatted.", t);
//        }

        return null;
    }

    private byte[] formatPrimitive(Field aField, Object anObject) {
//        String name = aField.getName();
//        Object value;
//
//        try {
//            value = aField.get(anObject);
//        } catch (Throwable t) {
//            throw new IllegalArgumentException("An object primitive cannot be formatted.", t);
//        }

        return null;
    }
}
