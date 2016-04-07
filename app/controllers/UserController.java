package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.mvc.*;
import play.mvc.Controller;
import play.libs.*;
import models.*;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by npatel on 3/17/16.
 */
public class UserController extends Controller {
    @Inject FormFactory formFactory;
    public Result register(){
        User user = Json.fromJson(request().body().asJson(), User.class);
        if(user == null)
            return badRequest();
        Form<User> form = formFactory.form(User.class).bindFromRequest();

        // Check form errors
        JsonNode node = validateUser(form);
        if(node != null)
            return badRequest(Json.toJson(node));

        // Save the entry
        return createUser();
    }

    public JsonNode validateUser(Form<User> form)
    {
        if(form.hasErrors()) {
            JsonNode node = form.errorsAsJson();    // Get errors
            User error = new User();

            if(form.error("first_name") != null)
                error.setFirst_name( node.get("first_name").get(0).asText() );
            if(form.error("last_name") != null)
                error.setLast_name( node.get("last_name").get(0).asText() );
            if(form.error("email") != null)
                error.setEmail( node.get("email").get(0).asText() );
            if(form.error("password") != null)
                error.setPassword(node.get("password").get(0).asText());
            return Json.toJson(error);
        }
        User user = form.get();
        if(!user.getPassword().equals(user.getConfirm_password()))
        {
            User error = new User();
            error.setPassword("Both passwords must match");
            error.setConfirm_password("Both passwords must match");
            return Json.toJson(error);
        }
        // Check for unique email
        User existingUser = User.findByEmail(user.getEmail());
        if(existingUser != null)
        {
            User error = new User();
            error.setEmail("This email is already in use. Click forgot password, if you like to recover");
            return Json.toJson(error);
        }

        return null;
    }

    // Crud Methods
    private  Result getUser()
    {
        List<User> users = Ebean.find(User.class).findList();
        return ok(Json.toJson(users));
    }

    private Result getUser(Long id)
    {
        User user = Ebean.find(User.class,id);
        return user == null ? notFound() : ok(Json.toJson(user));
    }

    private Result createUser()
    {
        User user = Json.fromJson(request().body().asJson(), User.class);
        Ebean.save(user);
        return created(Json.toJson(user));
    }

    private Result updateUser(Long id)
    {
        User user = Json.fromJson(request().body().asJson(), User.class);
        //user.update();
        Ebean.update(user);
        return ok(Json.toJson(user));
    }

    private Result deleteUser(Long id)
    {
        User user = Ebean.find(User.class, id);
        Ebean.delete(user);
        //user.delete();
        return noContent(); // http://stackoverflow.com/a/2342589/1415732
    }
}
