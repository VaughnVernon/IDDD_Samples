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

package com.saasovation.collaboration.domain.model.calendar;

public enum RepeatType {

    DoesNotRepeat {
        public boolean isDoesNotRepeat() {
            return true;
        }
    },

    Daily {
        public boolean isDaily() {
            return true;
        }
    },

    Weekly {
        public boolean isWeekly() {
            return true;
        }
    },

    Monthy {
        public boolean isMonthly() {
            return true;
        }
    },

    Yearly {
        public boolean isYearly() {
            return true;
        }
    };

    public boolean isDaily() {
        return false;
    }

    public boolean isDoesNotRepeat() {
        return false;
    }

    public boolean isMonthly() {
        return false;
    }

    public boolean isWeekly() {
        return false;
    }

    public boolean isYearly() {
        return false;
    }
}
