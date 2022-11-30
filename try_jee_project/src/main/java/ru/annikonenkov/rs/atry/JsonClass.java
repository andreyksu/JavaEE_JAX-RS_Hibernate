package ru.annikonenkov.rs.atry;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;

import ru.annikonenkov.atry.interceptor.Loggable;

// Данный класс имеет больший приоритет на фоне RootClass, где идет "/" а в методах /json
@Path("/json")
// @RequestScoped
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
    @Produces({"text/plain"})
    @Loggable
    public String getJSON(@PathParam("first") int firstParam, @PathParam("second") int secondParam) {
        String forPrint = String.format("From /json %s", request.getMethod());
        log.info("getJSON(...)-------------------------------------------------------------------------------");
        tmpMethod();
        return forPrint;
    }

    @Loggable
    private void tmpMethod() {
        log.info("tmpMethod()-------------------------------------------------------------------------------");
        SimpleClassForCreateViaNew scfcvn = new SimpleClassForCreateViaNew();
        scfcvn.hello();
    }

    @GET
    @Path("/{first}")
    @Produces({"text/plain"})
    @Loggable
    public Response returnURI(@PathParam("first") int firstParam) throws FileNotFoundException {

        //Пример с обращением к Spring приложению.
        Client client = ClientBuilder.newClient(); // Построитель интерфейсов WebTarget
        // client.register() - разобраться с конфигурацией
        WebTarget target = client.target("http://192.168.0.154:8989/food/food"); // Представляет URI с которого можно делать запросы для получения Response.
        Invocation invocation = target.queryParam("weight", "15").request(MediaType.APPLICATION_JSON).acceptLanguage("en").buildGet();
        Response response = invocation.invoke();
        String responseContent = response.readEntity(String.class);
        Set<String> allowedMethods = response.getAllowedMethods();
        MultivaluedMap<String, Object> headers = response.getHeaders();
        Set<Link> links = response.getLinks();
        Map<String, NewCookie> cookies =  response.getCookies();
        MediaType mediaType = response.getMediaType();
        MultivaluedMap<String, Object> metadata = response.getMetadata();
        URI uriResponse = response.getLocation();
        Locale locale = response.getLanguage();
        Date date = response.getDate();
        
        
        log.info(String.format("responseConten = %s", responseContent));
        log.info(String.format("headers = %s", headers.toString()));
        log.info(String.format("cookies = %s", cookies.toString()));
        log.info(String.format("allowedMethods = %s", allowedMethods.toString()));        
        log.info(String.format("links = %s", links.toString()));        
        log.info(String.format("mediaType = %s", mediaType.toString()));
        log.info(String.format("metadata = %s", metadata.toString()));
        log.info(String.format("uriResponse = %s", uriResponse));
        log.info(String.format("locale = %s", locale));
        log.info(String.format("date = %s", date));

        // Второй пример FileUpload
        // File fileToUpload = null;
        // Response resp2 = target.request(MediaType.TEXT_PLAIN).put(Entity.entity(new FileInputStream(fileToUpload), new MediaType("application", "pdf")));

        // Третий пример
        // Response resp3 = target.request().post(Entity.json(null)); //вместо null - нужно указать Pojo class или DTO

        // ----------------
        URI uri = uriInfo.getAbsolutePathBuilder().path("fromMethodReturnURI").build();
        return Response.accepted(uri).build();
    }

}
