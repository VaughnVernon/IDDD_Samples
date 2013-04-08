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

package com.saasovation.common.port.adapter.persistence.leveldb;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import com.google.gson.reflect.TypeToken;
import com.saasovation.common.serializer.ObjectSerializer;

public class LevelDBUnitOfWork {

    private static Map<String,ReentrantLock> keyLocks =
            new ConcurrentHashMap<String,ReentrantLock>();

    private static ThreadLocal<LevelDBUnitOfWork> unitsOfWork =
            new ThreadLocal<LevelDBUnitOfWork>();

    private WriteBatch batch;
    private DB database;
    private List<ReentrantLock> locks;
    private Map<String,Set<Object>> referenceKeys;
    private ObjectSerializer serializer;

    public static LevelDBUnitOfWork current() {
        LevelDBUnitOfWork uow = unitsOfWork.get();

        if (uow == null) {
            throw new IllegalStateException("No unit of work has been started.");
        }

        return uow;
    }

    public static LevelDBUnitOfWork readOnly(DB aDatabase) {
        LevelDBUnitOfWork uow = unitsOfWork.get();

        if (uow == null) {
            uow = new LevelDBUnitOfWork(aDatabase, false);

            unitsOfWork.set(uow);
        }

        return uow;
    }

    public static LevelDBUnitOfWork start(DB aDatabase) {
        LevelDBUnitOfWork uow = unitsOfWork.get();

        if (uow == null) {
            uow = new LevelDBUnitOfWork(aDatabase);

            unitsOfWork.set(uow);
        } else {
            uow.createWriteBatch(aDatabase);
        }

        return uow;
    }

    public void commit() {
        this.database.write(this.batch);

        this.close();
    }

    public void lock(String aLockKey) {
        ReentrantLock lock = this.findKeyLock(aLockKey);

        this.locks.add(lock);

        lock.lock();
    }

    public byte[] readObjectAsBytes(LevelDBKey aKey) {
        return this.database.get(aKey.keyAsBytes());
    }

    public <T> T readObject(LevelDBKey aKey, Class<T> aType) {
        return this.readObject(aKey.keyAsBytes(), aType);
    }

    public <T> T readObject(byte[] aKey, Class<T> aType) {
        byte[] objectBytes = this.database.get(aKey);

        T object = null;

        if (objectBytes != null) {
            object = this.serializer.deserialize(new String(objectBytes), aType);
        }

        return object;
    }

    public Object readKey(LevelDBKey aKey) {
        Object singleKey = null;

        Set<Object> keys = this.loadReferenceKeyValues(aKey);

        if (!keys.isEmpty()) {
            singleKey = keys.iterator().next();
        }

        return singleKey;
    }

    public List<Object> readKeys(LevelDBKey aKey) {
        return new ArrayList<Object>(this.loadReferenceKeyValues(aKey));
    }

    public void remove(LevelDBKey aPrimaryKey) {
        this.batch.delete(aPrimaryKey.keyAsBytes());
    }

    public void removeKeyReference(LevelDBKey aKey) {
        Set<Object> allValues = this.loadReferenceKeyValues(aKey);

        if (allValues.remove(aKey.primaryKeyValue())) {
            if (allValues.isEmpty()) {
                this.batch.delete(aKey.keyAsBytes());
            } else {
                String serializedValue = this.serializer.serialize(allValues);

                this.batch.put(aKey.keyAsBytes(), serializedValue.getBytes());
            }
        }
    }

    public void rollback() {
        this.close();
    }

    public void updateKeyReference(LevelDBKey aKey) {
        Set<Object> allValues = this.loadReferenceKeyValues(aKey);

        allValues.add(aKey.primaryKeyValue());

        String serializedValue = this.serializer.serialize(allValues);

        this.batch.put(aKey.keyAsBytes(), serializedValue.getBytes());
    }

    public void write(LevelDBKey aKey, Object aValue) {
        String serializedValue = this.serializer.serialize(aValue);

        this.batch.put(aKey.keyAsBytes(), serializedValue.getBytes());
    }

    public void write(byte[] aKey, Object aValue) {
        String serializedValue = this.serializer.serialize(aValue);

        this.batch.put(aKey, serializedValue.getBytes());
    }

    private LevelDBUnitOfWork(DB aDatabase) {
        this(aDatabase, true);
    }

    private LevelDBUnitOfWork(DB aDatabase, boolean isWritable) {
        super();

        if (isWritable) {
            this.createWriteBatch(aDatabase);
        }

        this.database = aDatabase;
        this.locks = new ArrayList<ReentrantLock>(1);
        this.referenceKeys = new HashMap<String,Set<Object>>();
        this.serializer = ObjectSerializer.instance();
    }

    private void createWriteBatch(DB aDatabase) {
        if (this.batch == null) {
            this.batch = aDatabase.createWriteBatch();
        }
    }

    private void close() {
        unitsOfWork.set(null);

        if (this.batch != null) {
            try {
                this.batch.close();
                this.batch = null;
            } catch (IOException e) {
                throw new IllegalStateException("Cannot close unit of work.");
            }
        }

        if (!this.locks.isEmpty()) {
            for (ReentrantLock lock : this.locks) {
                while (lock.getHoldCount() > 0) {
                    lock.unlock();
                }
            }

            this.locks.clear();
        }
    }

    private ReentrantLock findKeyLock(String aLockKey) {
        ReentrantLock lock = keyLocks.get(aLockKey);

        if (lock == null) {
            lock = new ReentrantLock();

            keyLocks.put(aLockKey, lock);
        }

        return lock;
    }

    private Set<Object> loadReferenceKeyValues(LevelDBKey aKey) {
        Set<Object> allValues = this.referenceKeys.get(aKey.key());

        if (allValues == null) {
            byte[] currentValues = this.database.get(aKey.keyAsBytes());

            if (currentValues == null) {
                allValues = new HashSet<Object>();
            } else {
                Type listType = new TypeToken<HashSet<Object>>() { }.getType();

                allValues = this.serializer.deserialize(new String(currentValues), listType);
            }

            this.referenceKeys.put(aKey.key(), allValues);
        }

        return allValues;
    }
}
