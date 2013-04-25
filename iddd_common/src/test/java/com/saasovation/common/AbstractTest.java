package com.saasovation.common;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.unitils.UnitilsJUnit4TestClassRunner;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AbstractTest {

    @Before
    public void initMocks() {
	MockitoAnnotations.initMocks(this);
    }

}
