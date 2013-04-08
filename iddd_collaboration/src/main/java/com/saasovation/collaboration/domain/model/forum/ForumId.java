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

package com.saasovation.collaboration.domain.model.forum;

import com.saasovation.common.domain.model.AbstractId;

public final class ForumId extends AbstractId {

    private static final long serialVersionUID = 1L;

    public ForumId(String anId) {
        super(anId);
    }

    protected ForumId() {
        super();
    }

    @Override
    protected int hashOddValue() {
        return 83713;
    }

    @Override
    protected int hashPrimeValue() {
        return 11;
    }
}
