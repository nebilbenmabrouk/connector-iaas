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
package org.ow2.proactive.connector.iaas.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.jclouds.compute.RunNodesException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ow2.proactive.connector.iaas.cloud.CloudManager;
import org.ow2.proactive.connector.iaas.fixtures.InfrastructureFixture;
import org.ow2.proactive.connector.iaas.fixtures.InstanceFixture;
import org.ow2.proactive.connector.iaas.model.Infrastructure;
import org.ow2.proactive.connector.iaas.model.Instance;

import jersey.repackaged.com.google.common.collect.Sets;


public class InstanceServiceTest {

    @InjectMocks
    private InstanceService instanceService;

    @Mock
    private InfrastructureService infrastructureService;

    @Mock
    private CloudManager cloudManager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateInstance() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        Instance instance = InstanceFixture.getInstance("instance-id",
                                                        "instance-name",
                                                        "image",
                                                        "2",
                                                        "512",
                                                        "cpu",
                                                        "publicIP",
                                                        "privateIP",
                                                        "running");

        when(cloudManager.createInstance(infrastructure, instance)).thenReturn(Sets.newHashSet(instance));

        Set<Instance> created = instanceService.createInstance("id-aws", instance);

        assertThat(created.size(), is(1));
        verify(cloudManager, times(1)).createInstance(infrastructure, instance);
    }

    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void testCreateInstanceException() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        Instance instance = InstanceFixture.getInstance("instance-id",
                                                        "instance-name",
                                                        "image",
                                                        "2",
                                                        "512",
                                                        "cpu",
                                                        "publicIP",
                                                        "privateIP",
                                                        "running");

        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(null);

        instanceService.createInstance("id-aws", instance);

        verify(cloudManager, times(0)).createInstance(infrastructure, instance);

    }

    @Test
    public void testDeleteInstance() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        Instance instance = InstanceFixture.simpleInstance("instance-id");

        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        instanceService.deleteInstance(infrastructure.getId(), instance.getId());

        verify(cloudManager, times(1)).deleteInstance(infrastructure, instance.getId());
    }

    @Test
    public void testDeleteInstanceInfrastructureIdDoesNotExists() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(null);

        instanceService.deleteInstance(infrastructure.getId(), "instanceID");

        verify(cloudManager, times(0)).deleteInstance(infrastructure, "instanceID");
    }

    @Test
    public void testDeleteInstanceByTag() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        Instance instance1 = InstanceFixture.simpleInstanceWithTag("id1", "tag1");
        Instance instance2 = InstanceFixture.simpleInstanceWithTag("id2", "tag2");

        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);
        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(instance1,
                                                                                                    instance2));

        instanceService.deleteInstanceByTag(infrastructure.getId(), "tag1");

        verify(cloudManager, times(1)).getAllInfrastructureInstances(infrastructure);
        verify(cloudManager, times(1)).deleteInstance(infrastructure, "id1");
        verify(cloudManager, times(0)).deleteInstance(infrastructure, "id2");
    }

    @Test
    public void testDeleteCreatedInstances() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        Instance instance1 = InstanceFixture.simpleInstance("id1");
        Instance instance2 = InstanceFixture.simpleInstance("id2");
        Instance instance3 = InstanceFixture.simpleInstance("id2");

        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);
        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(instance1,
                                                                                                    instance2,
                                                                                                    instance3));
        when(cloudManager.getCreatedInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(instance1,
                                                                                                        instance2));

        instanceService.deleteCreatedInstances(infrastructure.getId());

        verify(cloudManager, times(0)).getAllInfrastructureInstances(infrastructure);
        verify(cloudManager, times(1)).getCreatedInfrastructureInstances(infrastructure);
        verify(cloudManager, times(1)).deleteInstance(infrastructure, "id1");
        verify(cloudManager, times(1)).deleteInstance(infrastructure, "id2");
        verify(cloudManager, times(0)).deleteInstance(infrastructure, "id3");
    }

    @Test
    public void testDeleteAllInstances() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        Instance instance1 = InstanceFixture.simpleInstance("id1");
        Instance instance2 = InstanceFixture.simpleInstance("id2");
        Instance instance3 = InstanceFixture.simpleInstance("id3");

        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);
        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(instance1,
                                                                                                    instance2,
                                                                                                    instance3));

        instanceService.deleteAllInstances(infrastructure.getId());

        verify(cloudManager, times(1)).getAllInfrastructureInstances(infrastructure);
        verify(cloudManager, times(1)).deleteInstance(infrastructure, "id1");
        verify(cloudManager, times(1)).deleteInstance(infrastructure, "id2");
        verify(cloudManager, times(1)).deleteInstance(infrastructure, "id3");
    }

    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void testDeleteInstanceByTagInfrastructureIdDoesNotExists() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(null);

        instanceService.deleteInstanceByTag(infrastructure.getId(), "someTag");

        verify(cloudManager, times(0)).deleteInstance(infrastructure, "id111");
        verify(cloudManager, times(0)).deleteInstance(infrastructure, "id112");
    }

    @Test
    public void testGetAllInstances() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(InstanceFixture.simpleInstance("id")));

        Set<Instance> created = instanceService.getAllInstances(infrastructure.getId());

        assertThat(created.size(), is(1));

        verify(cloudManager, times(1)).getAllInfrastructureInstances(infrastructure);

    }

    @Test
    public void testGetCreatedInstances() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);

        Instance instance1 = InstanceFixture.simpleInstance("id1");
        Instance instance2 = InstanceFixture.simpleInstance("id2");

        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);
        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(instance1,
                                                                                                    instance2));
        when(cloudManager.getCreatedInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(instance1));

        Set<Instance> created = instanceService.getCreatedInstances(infrastructure.getId());

        assertThat(created.size(), is(1));
        verify(cloudManager, times(0)).getAllInfrastructureInstances(infrastructure);
        verify(cloudManager, times(1)).getCreatedInfrastructureInstances(infrastructure);
    }

    @Test(expected = javax.ws.rs.NotFoundException.class)
    public void testGetAllInstancesInfrastructureIdDoesNotExists() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(null);

        instanceService.getAllInstances(infrastructure.getId());

        verify(cloudManager, times(0)).getAllInfrastructureInstances(infrastructure);

    }

    @Test
    public void testGetInstanceByTag() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(InstanceFixture.simpleInstance("id")));

        instanceService.getInstanceByTag(infrastructure.getId(), "instanceTAG");

        verify(cloudManager, times(1)).getAllInfrastructureInstances(infrastructure);

    }

    @Test
    public void testGetInstanceById() throws NumberFormatException, RunNodesException {

        Infrastructure infrastructure = InfrastructureFixture.getInfrastructure("id-aws",
                                                                                "aws",
                                                                                "endPoint",
                                                                                "userName",
                                                                                "password",
                                                                                null,
                                                                                null,
                                                                                null);
        when(infrastructureService.getInfrastructure(infrastructure.getId())).thenReturn(infrastructure);

        when(cloudManager.getAllInfrastructureInstances(infrastructure)).thenReturn(Sets.newHashSet(InstanceFixture.simpleInstance("id")));

        instanceService.getInstanceById(infrastructure.getId(), "id");

        verify(cloudManager, times(1)).getAllInfrastructureInstances(infrastructure);

    }
}
