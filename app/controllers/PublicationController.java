package controllers;

import com.avaje.ebean.Ebean;
import models.Publication;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

/**
 * Created by npatel on 4/5/16.
 */
public class PublicationController extends Controller{
    @Inject
    FormFactory formFactory;

    @Security.Authenticated(ActionAuthenticator.class)
    public Result addPublication(){
        Publication publication = Json.fromJson(request().body().asJson(), Publication.class);
        if(publication == null)
            return badRequest();
        Form<Publication> form = formFactory.form(Publication.class).bindFromRequest();

        if(form.hasErrors())
            return badRequest(form.errorsAsJson());

        User user = Ebean.find(User.class, Long.parseLong(session().get("User")));
        publication.setOwner(user);
        if(publication.getId() != 0)
            Ebean.update(publication);
        else
            Ebean.save(publication);
        return ok();
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result getAllPublications()
    {
        User user = Ebean.find(User.class, Long.parseLong(session().get("User")));
        return ok(Json.toJson(user.getPublications()));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result getPublication(Long id)
    {
        Publication publication = Ebean.find(Publication.class,id);
        return publication == null ? notFound() : ok(Json.toJson(publication));
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result deletePublication(Long id)
    {
        Publication publication = Ebean.find(Publication.class, id);
        // Check if owner of document
        User user = Ebean.find(User.class, Long.parseLong(session().get("User")));
        if(user.getId() != publication.getOwner().getId())
            return unauthorized();
        Ebean.delete(publication);
        return ok();
    }


}
