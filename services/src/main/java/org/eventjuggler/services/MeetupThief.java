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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Event;
import org.eventjuggler.model.Group;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class MeetupThief {

    private EntityManager em;

    public MeetupThief(EntityManager em) {
        this.em = em;
    }

    public static void main(String[] args) throws Exception {

        String key = "36481225492e147265b6652452d767";
        String category = "34";
        String page = "1";

        URL openEventsUrl = new URL("https://api.meetup.com/2/open_events?key=" + key + "&category=" + category + "&page="
                + page);

        JSONObject o = get(openEventsUrl);
        JSONArray eventsJson = o.getJSONArray("results");
        for (int i = 0; i < eventsJson.length(); i++) {
            JSONObject eventJson = eventsJson.getJSONObject(i);

            String groupId = eventJson.getJSONObject("group").getString("id");

            URL groupUrl = new URL("https://api.meetup.com/2/groups?key=" + key + "&group_id=" + groupId);
            JSONObject groupJson = get(groupUrl).getJSONArray("results").getJSONObject(0);

            eventJson.put("group", groupJson);

            System.out.println(groupJson.opt("topics"));
        }
    }

    public List<Event> steal(String category, String page, String key) throws Exception {
        URL openEventsUrl = new URL("https://api.meetup.com/2/open_events?key=" + key + "&category=" + category + "&page=" + page);

        JSONObject o = get(openEventsUrl);
        JSONArray eventsJson = o.getJSONArray("results");

        List<Event> events = new LinkedList<Event>();

        for (int i = 0; i < eventsJson.length(); i++) {
            JSONObject eventJson = eventsJson.getJSONObject(i);

            String groupId = eventJson.getJSONObject("group").getString("id");

            URL groupUrl = new URL("https://api.meetup.com/2/groups?key=" + key + "&group_id=" + groupId);
            JSONObject groupJson = get(groupUrl).getJSONArray("results").getJSONObject(0);

            eventJson.put("group", groupJson);

            Event event = createEvent(eventJson);
            events.add(event);
        }

        return events;
    }

    private static JSONObject get(URL url) throws Exception {
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setDoInput(true);
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.setRequestProperty("Content-Type", "application/json");

        BufferedReader rd = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        c.disconnect();

        return new JSONObject(sb.toString());
    }

    private Event createEvent(JSONObject j) throws Exception {
        List<Event> r = em.createQuery("from Event e where e.title = :title", Event.class)
                .setParameter("title", j.getString("name")).getResultList();
        if (!r.isEmpty()) {
            return r.get(0);
        }

        Event e = new Event();

        // TODO e.setAttendance();
        e.setDescription(j.optString("description"));

        if (j.getJSONObject("group").optJSONObject("group_photo") != null) {
            e.setImageId(j.optJSONObject("group").optJSONObject("group_photo").getString("thumb_link"));
        }

        e.setLocation(createAddress(j.optJSONObject("venue")));
        // TODO e.setOrganizer();
        e.setOrganizerGroup(createGroup(j.getJSONObject("group")));
        e.setTags(createTags(j));
        e.setTime(j.getLong("time"));
        e.setTitle(j.getString("name"));

        em.persist(e);

        System.out.println("Created event: " + e.getTitle());

        return e;
    }

    private String createTags(JSONObject j) throws Exception {
        JSONArray t = j.getJSONObject("group").optJSONArray("topics");
        if (t == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.length(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(t.getJSONObject(i).getString("name"));
        }
        return sb.toString();
    }

    private Group createGroup(JSONObject j) throws Exception {
        List<Group> r = em.createQuery("from Group g where g.name = :name", Group.class)
                .setParameter("name", j.getString("name")).getResultList();
        if (!r.isEmpty()) {
            return r.get(0);
        }

        Group g = new Group();
        g.setDescription(j.optString("description"));
        // TODO g.setImageId();
        g.setName(j.getString("name"));
        // TODO g.setLocation();
        // TODO g.setMembers();
        // TODO g.setOwner();

        em.persist(g);

        System.out.println("Created group: " + g.getName());

        return g;
    }

    private Address createAddress(JSONObject j) throws Exception {
        if (j == null) {
            return null;
        }

        List<Address> r = em.createQuery("from Address a where a.name = :name", Address.class)
                .setParameter("name", j.getString("name")).getResultList();
        if (!r.isEmpty()) {
            return r.get(0);
        }

        Address a = new Address();
        a.setCity(j.optString("city"));
        a.setCountry(j.optString("country"));
        // TODO a.setImageId();
        a.setLatitude(j.optString("lat"));
        a.setLongitude(j.optString("lon"));
        a.setName(j.getString("name"));
        // TODO a.setStreet();
        // TODO a.setStreetNum();
        a.setZip(j.optString("zip"));

        em.persist(a);

        System.out.println("Created address: " + a.getName());

        return a;
    }

}
