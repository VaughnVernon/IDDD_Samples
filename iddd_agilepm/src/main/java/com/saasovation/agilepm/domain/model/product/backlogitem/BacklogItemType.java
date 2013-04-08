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

package com.saasovation.agilepm.domain.model.product.backlogitem;

public enum BacklogItemType  {

    FEATURE {
        public boolean isFeature() {
            return true;
        }
    },

    ENHANCEMENT {
        public boolean isEnhancement() {
            return true;
        }
    },

    DEFECT {
        public boolean isDefect() {
            return true;
        }
    },

    FOUNDATION {
        public boolean isFoundation() {
            return true;
        }
    },

    INTEGRATION {
        public boolean isIntegration() {
            return true;
        }
    };

    public boolean isDefect() {
        return false;
    }

    public boolean isEnhancement() {
        return false;
    }

    public boolean isFeature() {
        return false;
    }

    public boolean isFoundation() {
        return false;
    }

    public boolean isIntegration() {
        return false;
    }
}
