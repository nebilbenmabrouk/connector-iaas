/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.connector.iaas.cache;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.ow2.proactive.connector.iaas.fixtures.InfrastructureFixture;
import org.ow2.proactive.connector.iaas.model.Infrastructure;


public class InfrastructureCacheTest {
    private InfrastructureCache infrastructureCache;

    @Before
    public void init() {
        infrastructureCache = new InfrastructureCache();
    }

    @Test
    public void testConstructor() {
        assertThat(infrastructureCache.getSupportedInfrastructures(), is(not(nullValue())));
        assertThat(infrastructureCache.getSupportedInfrastructures().isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutability() {
        ((Map<String, Infrastructure>) infrastructureCache.getSupportedInfrastructures()).put("openstack",
                                                                                              InfrastructureFixture.getInfrastructure("id-openstack",
                                                                                                                                      "openstack",
                                                                                                                                      "endPoint",
                                                                                                                                      "userName",
                                                                                                                                      "password",
                                                                                                                                      "admin",
                                                                                                                                      "RegionOne",
                                                                                                                                      "3"));
    }

    @Test
    public void testRegisterInfrastructure() {
        infrastructureCache.registerInfrastructure(InfrastructureFixture.getInfrastructure("id-openstack",
                                                                                           "openstack",
                                                                                           "endPoint",
                                                                                           "userName",
                                                                                           "password",
                                                                                           "admin",
                                                                                           "RegionOne",
                                                                                           "3"));
        assertThat(infrastructureCache.getSupportedInfrastructures().size(), is(1));
        assertThat(infrastructureCache.getSupportedInfrastructures().get("id-openstack"),
                   is(InfrastructureFixture.getInfrastructure("id-openstack",
                                                              "openstack",
                                                              "endPoint",
                                                              "userName",
                                                              "password",
                                                              "admin",
                                                              "RegionOne",
                                                              "3")));
    }

    @Test
    public void testDeleteInfrastructure() {
        infrastructureCache.registerInfrastructure(InfrastructureFixture.getInfrastructure("id-openstack",
                                                                                           "openstack",
                                                                                           "endPoint",
                                                                                           "userName",
                                                                                           "password",
                                                                                           "admin",
                                                                                           "RegionOne",
                                                                                           "3"));

        infrastructureCache.deleteInfrastructure(InfrastructureFixture.getInfrastructure("id-openstack",
                                                                                         "openstack",
                                                                                         "endPoint",
                                                                                         "userName",
                                                                                         "password",
                                                                                         "admin",
                                                                                         "RegionOne",
                                                                                         "3"));

        assertThat(infrastructureCache.getSupportedInfrastructures(), is(not(nullValue())));
        assertThat(infrastructureCache.getSupportedInfrastructures().isEmpty(), is(true));
    }

}
