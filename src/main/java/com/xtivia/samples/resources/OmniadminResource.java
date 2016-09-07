/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.samples.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.xtivia.sgdxp.annotation.Omniadmin;

@Path("/omniadmin")
public class OmniadminResource extends PeopleResource {

    @GET
    @Produces("application/json")
    public List<Person> getAllPeople() {
        return super.getAllPeople();
    }

    @Override
    @Omniadmin
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getPerson(@PathParam("id") String id) {
        return super.getPerson(id);
    }
}
