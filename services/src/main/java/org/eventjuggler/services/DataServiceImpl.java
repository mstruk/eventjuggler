/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eventjuggler.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.eventjuggler.model.Event;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class DataServiceImpl implements DataService {

    @PersistenceContext(unitName = "eventjuggler")
    private EntityManager em;

    @Resource(name = "java:jboss/datasources/ExampleDS")
    private DataSource dataSource;

    @Override
    public void clear() {
        for (EntityType<?> e : em.getMetamodel().getEntities()) {
            for (Object o : em.createQuery("from " + e.getName()).getResultList()) {
                em.remove(o);
            }
        }
    }

    @Override
    public String exportData() {
        try {
            Connection jdbcConnection = dataSource.getConnection();
            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

            IDataSet dataSet = connection.createDataSet();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FlatXmlDataSet.write(dataSet, os);
            os.close();

            jdbcConnection.close();

            return os.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importData(String data) {
        try {
            Connection jdbcConnection = dataSource.getConnection();

            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

            ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());
            FlatXmlProducer flatXmlProducer = new FlatXmlProducer(new InputSource(is));
            FlatXmlDataSet dataSet = new FlatXmlDataSet(flatXmlProducer);
            is.close();

            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

            jdbcConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Event> stealFromMeetup(String category, String page, String key) {
        MeetupThief thief = new MeetupThief(em);
        try {
            return thief.steal(category, page, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
