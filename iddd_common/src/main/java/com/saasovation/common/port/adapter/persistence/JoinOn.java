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

package com.saasovation.common.port.adapter.persistence;

import java.sql.ResultSet;

public class JoinOn {

    private Object currentLeftQualifier;
    private String leftKey;
    private String rightKey;

    public JoinOn(String aLeftKey, String aRightKey) {
        super();

        this.leftKey = aLeftKey;
        this.rightKey = aRightKey;
    }

    public JoinOn() {
        super();
    }

    public boolean hasCurrentLeftQualifier(ResultSet aResultSet) {
        try {
            Object columnValue = aResultSet.getObject(this.leftKey());

            if (columnValue == null) {
                return false;
            }

            return columnValue.equals(this.currentLeftQualifier);

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isJoinedOn(ResultSet aResultSet) {

        Object leftColumn = null;
        Object rightColumn = null;

        try {
            if (this.isSpecified()) {
                leftColumn = aResultSet.getObject(this.leftKey());
                rightColumn = aResultSet.getObject(this.rightKey());
            }

        } catch (Exception e) {
            // ignore
        }

        return leftColumn != null && rightColumn != null;
    }

    public boolean isSpecified() {
        return this.leftKey() != null && this.rightKey() != null;
    }

    public String leftKey() {
        return this.leftKey;
    }

    public String rightKey() {
        return this.rightKey;
    }

    public void saveCurrentLeftQualifier(String aColumnName, Object aColumnValue) {
        if (aColumnName.equals(this.leftKey())) {
            this.currentLeftQualifier = aColumnValue;
        }
    }
}
