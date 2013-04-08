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

package com.saasovation.agilepm.port.adapter.messaging;

public class ProductDiscussionExclusiveOwnerId {

    private static final String PREFIX = "agilepm.product.discussion.";

    private String id;

    public static ProductDiscussionExclusiveOwnerId fromEncodedId(
            String anEncodedId) {

        ProductDiscussionExclusiveOwnerId id = null;

        if (ProductDiscussionExclusiveOwnerId.isValid(anEncodedId)) {
            id = new ProductDiscussionExclusiveOwnerId(
                    anEncodedId.substring(PREFIX.length()));
        }

        return id;
    }

    public static boolean isValid(
            String anEncodedId) {

        return anEncodedId.startsWith(PREFIX) &&
                anEncodedId.length() > PREFIX.length();
    }

    public ProductDiscussionExclusiveOwnerId(String anId) {
        super();

        this.id = anId;
    }

    public String encoded() {
        return PREFIX + this.id;
    }

    public String id() {
        return this.id;
    }
}
