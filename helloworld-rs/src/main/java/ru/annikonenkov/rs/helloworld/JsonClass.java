package ru.annikonenkov.rs.helloworld;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import ru.annikonenkov.interceptor.Loggable;

import org.jboss.logging.Logger;

@Path("/json")
//@RequestScoped
@Stateless
public class JsonClass {

	@Inject
	Logger log;

	@Context
	Request request;

	@Context
	private HttpHeaders httpHeaders;

	@Context
	private UriInfo uriInfo;

	@GET
	@Path("/{first}/{second}")
	@Produces({ "text/plain" })
	@Loggable
	public String getJSON_0(@PathParam("first") int firstParam, @PathParam("second") int secondParam) {
		String forPrint = String.format("From /json %s", request.getMethod());
		log.info("Тут-------------------------------------------------------------------------------");
		stt();
		return forPrint;
	}

	@Loggable
	private void stt() {
		log.info("Здесь-------------------------------------------------------------------------------");
		SimpleClassForCreateViaNew scfcvn = new SimpleClassForCreateViaNew();
		scfcvn.hello();
	}
}
