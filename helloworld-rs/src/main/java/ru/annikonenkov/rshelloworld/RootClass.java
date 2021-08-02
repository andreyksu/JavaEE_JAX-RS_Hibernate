/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.annikonenkov.rshelloworld;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ru.annikonenkov.ejbpack.calc.ICalck;
import ru.annikonenkov.ejbpack.someservice.SomeService;
import ru.annikonenkov.jmspack.PublisherClass;
import ru.annikonenkov.jpapack.Mother;
import ru.annikonenkov.jpapack.MotherDAO;
import ru.annikonenkov.jpapack.Person;
import ru.annikonenkov.jpapack.PersonDAO;

/**
 * A simple REST service which is able to say hello to someone using
 * HelloService Please take a look at the web.xml where JAX-RS is enabled
 *
 * @author andreyksu@gmail.com
 */

@Path("/")
@RequestScoped
public class RootClass {

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private UriInfo uriInfo;

    @EJB
    private ICalck someCalck;

    @EJB
    private PersonDAO personDAO;

    @EJB
    private MotherDAO motherDAO;

    @Inject
    private SomeService someService;

    @Inject
    private PublisherClass publisherClass;

    @GET
    @Path("/json/{first}/{second}")
    @Produces({"text/plain"})
    public String getJSON_0(@PathParam("first") int firstParam, @PathParam("second") int secondParam) {
        int mathResultDiff = someCalck.difference(firstParam, secondParam);
        int mathResultSumm = someCalck.summ(firstParam, secondParam);
        String inf = someCalck.getFullInfo();

        String forPrint = someService.createMessage(String.format(
                "mathResultDiff = %d \n mathResultSumm = %d\n inf = %s\n", mathResultDiff, mathResultSumm, inf));

        return forPrint;
    }

    @GET
    @Path("/json/{first}-{second}")
    @Produces({"application/json"})
    public String getJSON_1(@PathParam("first") int firstParam, @PathParam("second") int secondParam) {
        int mathResult = someCalck.difference(firstParam, secondParam);
        return "{\"result\":\"" + someService.createMessage(String.format("mathResult = %d", mathResult)) + "\"}";
    }

    @GET
    @Path("/json/full")
    @Produces({"application/json"})
    public String getJSON_2(@DefaultValue("0") @QueryParam("first") int firstParam,
                            @DefaultValue("0") @QueryParam("second") int secondParam) {
        int mathResult = someCalck.difference(firstParam, secondParam);
        return "{\"result\":\""
                + someService.createMessage(
                String.format("mathResult = %d \n %s", mathResult, httpHeaders.getRequestHeaders().toString()))
                + "\"}";
    }

    @GET
    @Path("/http-headers")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllHttpHeaders() {
        return Response.ok(httpHeaders.getRequestHeaders()).build();

    }

    @GET
    @Path("/uri-info")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllUriInfo() {
        return Response.ok(uriInfo.getPathParameters()).build();
    }

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllInfo() {
        return Response.ok(someCalck.getFullInfo()).build();
    }

    @GET
    @Path("/xml")
    @Produces({"application/xml"})
    public String getXML() {
        return "<xml><result>" + someService.createMessage("Prefix") + "</result></xml>";
    }

    @GET
    @Path("/getPerson")
    @Produces({"application/json"})
    public String getPersons() {
        List<Person> list = personDAO.findAny();
        String result = "[";
        int count = 0;
        for (Person i : list) {
            if (count > 0) {
                result += ",";
            }
            result += i.toStringFullRelates();
            count++;
        }
        result += "]";
        return result;
    }

    @GET
    @Path("/getPerson/name-{name}")
    @Produces({"application/json"})
    public String getPersonsByName(@PathParam("name") String name) {
        List<Person> list = personDAO.findByName(name);
        String result = "[";
        int count = 0;
        for (Person i : list) {
            if (count > 0) {
                result += ",";
            }
            result += i.toStringFullRelates();
            count++;
        }
        result += "]";
        return result;
    }

    @GET
    @Path("/getPerson/id-{id}")
    @Produces({"application/json"})
    public String getPersonsById(@PathParam("id") int id) {
        Person person = personDAO.findById(id);
        return person.toStringFullRelates();
    }

    @GET
    @Path("/addPerson/{name}-{old}")
    @Produces({"application/json"})
    public void addPersons(@PathParam("name") String name, @PathParam("old") int old) {
        personDAO.put(name, old);
    }

    /*
    @GET
    @Path("/getMothers")
    @Produces({"application/json"})
    public String getMothers() { //А вот если с Lazy использвать здесь обращение к полям, то упадем по ошибке. Т.к. JTA транзакция завершена, и уже завершена сессия к БД. Ибо JPA транзакция живет в рамках JTA.
        List<Mother> list = motherDAO.findAny();
        String result = "[";
        int count = 0;
        for (Mother i : list) {
            if (count > 0) {
                result += ",";
            }
            result += i.toStringWithPerson();
            count++;
        }
        result += "]";
        return result;
    }
    */


    @GET
    @Path("/getMothers")
    @Produces({"application/json"})
    public String getMothers() {
        return motherDAO.findAnyAsStr();
    }

    @GET
    @Path("/jms/{param}")
    @Produces({"application/json"})
    public void jms(@PathParam("param") String param) {
        publisherClass.sendMessage(param);
    }

}
