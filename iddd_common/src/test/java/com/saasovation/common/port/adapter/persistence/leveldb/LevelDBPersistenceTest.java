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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LevelDBPersistenceTest extends LevelDBTest {

    private LevelRepository levelRepository = new LevelRepository();

    public LevelDBPersistenceTest() {
        super();
    }

    public void testSaveAndQuery() throws Exception {
        Level level1 = new Level("1", "One", 1);
        Level level2 = new Level("2", "Two", 2);
        Level level3 = new Level("3", "Three", 3);

        LevelDBUnitOfWork.start(this.database());
        levelRepository.save(level1);
        levelRepository.save(level2);
        levelRepository.save(level3);
        LevelDBUnitOfWork.current().commit();

        assertEquals(3, levelRepository.allLevels().size());

        assertEquals(level1.id(), levelRepository.levelOfId("1").id());
        assertEquals(level2.id(), levelRepository.levelOfId("2").id());
        assertEquals(level3.id(), levelRepository.levelOfId("3").id());

        assertEquals(level1.name(), levelRepository.levelOfName("One").name());
        assertEquals(level2.name(), levelRepository.levelOfName("Two").name());
        assertEquals(level3.name(), levelRepository.levelOfName("Three").name());

        assertEquals(level1.value(), levelRepository.levelOfId("1").value());
        assertEquals(level2.value(), levelRepository.levelOfId("2").value());
        assertEquals(level3.value(), levelRepository.levelOfId("3").value());
    }

    public void testRemoveAndQuery() throws Exception {
        Level level1 = new Level("1", "One", 1);
        Level level2 = new Level("2", "Two", 2);
        Level level3 = new Level("3", "Three", 3);

        LevelDBUnitOfWork.start(this.database());
        levelRepository.save(level1);
        levelRepository.save(level2);
        levelRepository.save(level3);
        LevelDBUnitOfWork.current().commit();

        assertEquals(3, levelRepository.allLevels().size());
        assertEquals(level1.id(), levelRepository.levelOfId("1").id());
        assertEquals(level2.id(), levelRepository.levelOfId("2").id());
        assertEquals(level3.id(), levelRepository.levelOfId("3").id());

        LevelDBUnitOfWork.start(this.database());
        levelRepository.remove(level2);
        LevelDBUnitOfWork.current().commit();

        assertEquals(2, levelRepository.allLevels().size());
        assertNull(levelRepository.levelOfId("2"));
        assertEquals(level1.id(), levelRepository.levelOfId("1").id());
        assertEquals(level3.id(), levelRepository.levelOfId("3").id());

        LevelDBUnitOfWork.start(this.database());
        levelRepository.remove(level1);
        LevelDBUnitOfWork.current().commit();

        assertEquals(1, levelRepository.allLevels().size());
        assertNull(levelRepository.levelOfId("1"));
        assertNull(levelRepository.levelOfId("2"));
        assertEquals(level3.id(), levelRepository.levelOfId("3").id());

        LevelDBUnitOfWork.start(this.database());
        levelRepository.remove(level3);
        LevelDBUnitOfWork.current().commit();

        assertTrue(levelRepository.allLevels().isEmpty());
        assertNull(levelRepository.levelOfId("1"));
        assertNull(levelRepository.levelOfId("2"));
        assertNull(levelRepository.levelOfId("3"));
    }

    public void testConcurrentSaves() throws Exception {
        final List<Integer> orderOfCommits = new ArrayList<Integer>();

        Level level1 = new Level("1", "One", 1);

        LevelDBUnitOfWork.start(this.database());
        levelRepository.save(level1);

        new Thread() {
            @Override
            public void run() {
                Level level2 = new Level("2", "Two", 2);
                Level level3 = new Level("3", "Three", 3);

                System.out.println("Preparing to commit levels 2 and 3...");
                LevelDBUnitOfWork.start(database());
                levelRepository.save(level2);
                levelRepository.save(level3);
                LevelDBUnitOfWork.current().commit();
                orderOfCommits.add(2);
                orderOfCommits.add(3);
                System.out.println("Committed levels 2 and 3.");
            }
         }.start();

         System.out.println("Set to commit level 1, soon...");
         Thread.sleep(250L);

         System.out.println("Back to preparing to commit level 1...");
         LevelDBUnitOfWork.current().commit();
         orderOfCommits.add(1);
         System.out.println("Committed level 1.");

         for (int idx = 0; idx < orderOfCommits.size(); ++idx) {
             assertEquals(idx + 1, orderOfCommits.get(idx).intValue());
         }

         Thread.sleep(250L);

         Collection<Level> savedLevels = levelRepository.allLevels();

         assertFalse(savedLevels.isEmpty());
         assertEquals(3, savedLevels.size());
    }

    private static class LevelRepository extends AbstractLevelDBRepository {

        private static final String PRIMARY = "LEVEL#PK";
        private static final String ALL_LEVELS = "LEVEL#ALL";
        private static final String NAME_OF_LEVEL = "LEVEL#NAME";

        public LevelRepository() {
            super(TEST_DATABASE);
        }

        public Collection<Level> allLevels() {
            List<Level> levels = new ArrayList<Level>();

            LevelDBKey allLevelsKey = new LevelDBKey(ALL_LEVELS);

            LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

            List<Object> keys = uow.readKeys(allLevelsKey);

            for (Object levelId : keys) {
                Level level = uow.readObject(levelId.toString().getBytes(), Level.class);

                if (level != null) {
                    levels.add(level);
                }
            }

            return levels;
        }

        public Level levelOfName(String aName) {
            Level level = null;

            LevelDBKey nameKey = new LevelDBKey(NAME_OF_LEVEL, aName);

            Object levelId = LevelDBUnitOfWork.readOnly(this.database()).readKey(nameKey);

            if (levelId != null) {
                LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

                level = uow.readObject(levelId.toString().getBytes(), Level.class);
            }

            return level;
        }

        public Level levelOfId(String anId) {
            LevelDBKey primaryKey = new LevelDBKey(PRIMARY, anId);

            Level level =
                    LevelDBUnitOfWork.readOnly(this.database())
                        .readObject(primaryKey.key().getBytes(), Level.class);

            return level;
        }

        public void remove(Level aLevel) {
            LevelDBKey lockKey = new LevelDBKey(PRIMARY);

            LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

            uow.lock(lockKey.key());

            this.remove(aLevel, uow);
        }

        public void save(Level aLevel) {
            LevelDBKey lockKey = new LevelDBKey(PRIMARY);

            LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

            uow.lock(lockKey.key());

            this.save(aLevel, uow);
        }

        private void remove(Level aLevel, LevelDBUnitOfWork aUoW) {
            LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aLevel.id());
            aUoW.remove(primaryKey);

            LevelDBKey allLevels = new LevelDBKey(primaryKey, ALL_LEVELS);
            aUoW.removeKeyReference(allLevels);

            LevelDBKey nameOfLevel = new LevelDBKey(primaryKey, NAME_OF_LEVEL, aLevel.name());
            aUoW.removeKeyReference(nameOfLevel);
        }

        private void save(Level aLevel, LevelDBUnitOfWork aUoW) {
            LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aLevel.id());
            aUoW.write(primaryKey, aLevel);

            LevelDBKey allLevels = new LevelDBKey(primaryKey, ALL_LEVELS);
            aUoW.updateKeyReference(allLevels);

            LevelDBKey nameOfLevel = new LevelDBKey(primaryKey, NAME_OF_LEVEL, aLevel.name());
            aUoW.updateKeyReference(nameOfLevel);
        }
    }

    private static class Level {
        private String id;
        private String name;
        private int value;

        public Level(String anId, String aName, int aValue) {
            super();

            this.id = anId;
            this.name = aName;
            this.value = aValue;
        }

        public String id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }

        public int value() {
            return this.value;
        }
    }
}
