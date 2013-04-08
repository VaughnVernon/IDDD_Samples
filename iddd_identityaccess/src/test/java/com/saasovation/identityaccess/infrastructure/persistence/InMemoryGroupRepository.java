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

package com.saasovation.identityaccess.infrastructure.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saasovation.common.persistence.CleanableStore;
import com.saasovation.identityaccess.domain.model.identity.Group;
import com.saasovation.identityaccess.domain.model.identity.GroupRepository;
import com.saasovation.identityaccess.domain.model.identity.TenantId;

public class InMemoryGroupRepository implements GroupRepository, CleanableStore {

    private Map<String,Group> repository;

    public InMemoryGroupRepository() {
        super();

        this.repository = new HashMap<String,Group>();
    }

    @Override
    public void add(Group aGroup) {
        String key = this.keyOf(aGroup);

        if (this.repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }

        this.repository().put(key, aGroup);
    }

    @Override
    public Collection<Group> allGroups(TenantId aTenantId) {
        List<Group> groups = new ArrayList<Group>();

        for (Group group : this.repository().values()) {
            if (group.tenantId().equals(aTenantId)) {
                groups.add(group);
            }
        }

        return groups;
    }

    @Override
    public Group groupNamed(TenantId aTenantId, String aName) {
        if (aName.startsWith(Group.ROLE_GROUP_PREFIX)) {
            throw new IllegalArgumentException("May not find internal groups.");
        }

        String key = this.keyOf(aTenantId, aName);

        return this.repository().get(key);
    }

    @Override
    public void remove(Group aGroup) {
        String key = this.keyOf(aGroup);

        this.repository().remove(key);
    }

    @Override
    public void clean() {
        this.repository().clear();
    }

    private String keyOf(TenantId aTenantId, String aName) {
        String key = aTenantId.id() + "#" + aName;

        return key;
    }

    private String keyOf(Group aGroup) {
        return this.keyOf(aGroup.tenantId(), aGroup.name());
    }

    private Map<String,Group> repository() {
        return this.repository;
    }
}
