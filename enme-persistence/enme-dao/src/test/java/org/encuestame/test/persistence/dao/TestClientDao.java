/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: system online surveys Copyright (C) 2011
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */

package org.encuestame.test.persistence.dao;

import org.encuestame.test.config.AbstractBase;
import org.encuestame.utils.categories.test.DefaultTest;
import org.encuestame.persistence.dao.imp.ClientDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * {@link ClientDao} test.
 * @author Picado, Juan juanATencuestame.org
 * @since Jan 24, 2010 5:12:28 PM
 */
@Category(DefaultTest.class)
public class TestClientDao  extends AbstractBase{
 
    /**
     * Before.
     */
    @Before
    public void initBefore(){
       
    }

    /**
     * Get Client.
     */
    @Test
    public void testGetClientById(){
       //TODO: 
    }

    /**
     * Test find clients by project.
     */
    @Test
    public void testFindAllClientByProjectId(){
    	//TODO: 
    }
}
