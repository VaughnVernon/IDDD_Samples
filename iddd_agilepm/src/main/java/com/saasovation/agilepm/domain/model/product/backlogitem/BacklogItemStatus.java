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

public enum BacklogItemStatus {

    PLANNED {
        public boolean isPlanned() {
            return true;
        }
    },

    SCHEDULED {
        public boolean isScheduled() {
            return true;
        }
    },

    COMMITTED {
        public boolean isCommitted() {
            return true;
        }
    },

    DONE {
        public boolean isDone() {
            return true;
        }
    },

    REMOVED {
        public boolean isRemoved() {
            return true;
        }
    };

    public boolean isCommitted() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public boolean isPlanned() {
        return false;
    }

    public boolean isRemoved() {
        return false;
    }

    public boolean isScheduled() {
        return false;
    }

    public BacklogItemStatus regress() {
        if (this.isPlanned()) {
            return PLANNED;
        } else if (this.isScheduled()) {
            return PLANNED;
        } else if (this.isCommitted()) {
            return SCHEDULED;
        } else if (this.isDone()) {
            return COMMITTED;
        } else if (this.isRemoved()) {
            return PLANNED;
        }

        return PLANNED;
    }
}
