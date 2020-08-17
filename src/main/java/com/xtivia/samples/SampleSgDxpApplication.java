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
package com.xtivia.samples;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.xtivia.samples.resources.AllAuthenticatedResource;
import com.xtivia.samples.resources.AuthorizedResource;
import com.xtivia.samples.resources.MethodAuthenticatedResource;
import com.xtivia.samples.resources.OmniadminResource;
import com.xtivia.samples.resources.OrgMemberResource;
import com.xtivia.samples.resources.OrgRoleResource;
import com.xtivia.samples.resources.RegularRoleResource;
import com.xtivia.sgdxp.core.IAuthorizer;
import com.xtivia.sgdxp.core.IContext;
import com.xtivia.sgdxp.core.SgDxpApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

//import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/*
 * A sample application to demonstrate the use of Service Guard for DXP
 */


@Component(
		property = {
			JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/sgdxp_samples",
			JaxrsWhiteboardConstants.JAX_RS_NAME + "=Xtivia.SgDxp.Sample",
			"auth.verifier.guest.allowed=true",
			"oauth2.scopechecker.type=none",
			"liferay.access.control.disable=true",
			"auth.verifier.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false"
		},
		service = Application.class)

//@Component(immediate=true, service=Application.class, property={"jaxrs.application=true"})
//@ApplicationPath("/sgdxp_samples")
public class SampleSgDxpApplication extends SgDxpApplication implements IAuthorizer {
	
	/*
	 * Register our JAX-RS providers and resources
	 */

	@Override
	public Set<Object> getSingletons() {
		
		// invoke the parent class method; we will add our examples to the singletons added
		// by the SG-DXP base class
		Set<Object> singletons = super.getSingletons();

		//add the automated Jackson marshaller for JSON
		singletons.add(new JacksonJsonProvider());

		// add our sample protected REST endpoints (resources)
		singletons.add(new AllAuthenticatedResource());
		singletons.add(new MethodAuthenticatedResource());
		singletons.add(new OmniadminResource());
		singletons.add(new OrgMemberResource());
		singletons.add(new AuthorizedResource());
		singletons.add(new OrgRoleResource());
		singletons.add(new RegularRoleResource());

		return singletons;
	}
	
	/*
	 * This method demonstrates how you can perform logic when your bundle is activated/updated. For simplicity's
	 * sake we print a message to the console--this is particularly useful during update-style deployments. 
	 * 
	 */
	@Activate
	@Modified
	public void activate(Map<String, Object> properties) {
		System.out.println("The sample SG-DXP sample application has been activated/updated at " + new Date().toString());
	}


    @Override
    public IAuthorizer getAuthorizer(IContext ctx) {
        return this;
    }

    /*
      A somewhat nonsensical method that simply authorizes based on whether or not the current minute is even
      A real world example would do something more meaningful using the values available in the suppplied context
     */
    @Override
    public boolean authorize(IContext context) {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.MINUTE) % 2 == 0 ) {
            return true;
        } else {
            return false;
        }
    }
}