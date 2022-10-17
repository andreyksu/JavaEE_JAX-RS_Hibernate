package ru.annikonenkov.rs.helloworld;

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

import org.jboss.logging.Logger;

import ru.annikonenkov.ejbpack.calc.ICalck;
import ru.annikonenkov.ejbpack.someservice.SomeService;
import ru.annikonenkov.jmspack.PublisherClass;

/**
 * @author andreyksu@gmail.com
 */

@Path("/")
@RequestScoped
public class RootClass {

	@Inject
	Logger log;

	@Context
	private HttpHeaders httpHeaders;

	@Context
	private UriInfo uriInfo;

	@EJB
	private ICalck someCalck;

	@Inject
	private SomeService someService;

	@Inject
	private PublisherClass publisherClass;

	@GET
	@Path("/json/{first}/{second}")
	@Produces({ "text/plain" })
	public String getJSON_0(@PathParam("first") int firstParam, @PathParam("second") int secondParam) {
		int mathResultDiff = someCalck.difference(firstParam, secondParam);
		int mathResultSumm = someCalck.summ(firstParam, secondParam);
		String inf = someCalck.getFullInfo();

		String forPrint = someService.createMessage(String.format(
				"mathResultDiff = %d \n mathResultSumm = %d\n inf = %s\n", mathResultDiff, mathResultSumm, inf));

		String forPrint1 = String.format("From / \n %s", forPrint);
		return forPrint1;
	}

	@GET
	@Path("/json/{first}-{second}")
	@Produces({ "application/json" })
	public String getJSON_1(@PathParam("first") int firstParam, @PathParam("second") int secondParam) {
		int mathResult = someCalck.difference(firstParam, secondParam);
		return "{\"result\":\"" + someService.createMessage(String.format("mathResult = %d", mathResult)) + "\"}";
	}

	@GET
	@Path("/json/full")
	@Produces({ "application/json" })
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
	@Produces({ "application/xml" })
	public String getXML() {
		return "<xml><result>" + someService.createMessage("Prefix") + "</result></xml>";
	}

	@GET
	@Path("/jms/{param}")
	@Produces({ "application/json" })
	public void jms(@PathParam("param") String param) {
		publisherClass.sendMessage(param);
	}

}
