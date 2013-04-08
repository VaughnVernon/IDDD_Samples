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

public enum EncodingMarker {

    Begin(0x81),
    Key(0xa7),
    Null(0xc0),
    True(0xc1),
    False(0xc2),
    Int8(0xc3),
    Int16(0xc4),
    Int32(0xc5),
    Int64(0xc6),
    Array(0xc7),
    Map(0xc8),

    Undefined(0xcf);

    private int marker;

    EncodingMarker(int aMarker) {
        this.marker = aMarker;
    }

    public byte marker() {
        return (byte) this.marker;
    }
}
