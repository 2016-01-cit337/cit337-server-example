package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.text.json.JsonContext;
import org.h2.engine.Database;
import play.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.mvc.*;
import play.mvc.Controller;
import play.libs.*;
import models.*;

import javax.inject.Inject;
import javax.validation.Constraint;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import play.data.validation.*;

/**
 * Created by npatel on 3/17/16.
 */
public class UserController extends Controller {
    @Inject
    FormFactory formFactory;

    public Result register() {
        Form<User> form = formFactory.form(User.class).bindFromRequest();
        if (form == null)
            return badRequest();
        JsonNode node = validateUser(form);
        if (node != null)
            return badRequest(Json.toJson(node));
        // Save the entry
        return createUser();
    }


    private Result createUser() {
        User user = Json.fromJson(request().body().asJson(), User.class);
        Ebean.save(user);
        return created(Json.toJson(user));
    }

    public JsonNode validateUser(Form<User> form) {
        if (form.hasErrors()) {
            JsonNode node = form.errorsAsJson();    // Get errors
            User error = new User();

            if (form.error("first_name") != null)
                error.setFirst_name(node.get("first_name").get(0).asText());
            if (form.error("last_name") != null)
                error.setLast_name(node.get("last_name").get(0).asText());
            if (form.error("email") != null)
                error.setEmail(node.get("email").get(0).asText());
            if (form.error("password") != null)
                error.setPassword(node.get("password").get(0).asText());
            return Json.toJson(error);
        }
        User user = form.get();
        if (!user.getPassword().equals(user.getConfirm_password())) {
            User error = new User();
            error.setPassword("Both passwords must match");
            error.setConfirm_password("Both passwords must match");
            return Json.toJson(error);
        }
        // Check for unique email
        User existingUser = User.findByEmail(user.getEmail());
        if (existingUser != null) {
            User error = new User();
            error.setEmail("This email is already in use. Click forgot password, if you like to recover");
            return Json.toJson(error);
        }

        return null;
    }

    // Crud Methods
    private Result getUser() {
        List<User> users = Ebean.find(User.class).findList();
        return ok(Json.toJson(users));
    }

    private Result getUser(Long id) {
        User user = Ebean.find(User.class, id);
        return user == null ? notFound() : ok(Json.toJson(user));
    }

    private Result updateUser(Long id) {
        User user = Json.fromJson(request().body().asJson(), User.class);
        Ebean.update(user);
        return ok(Json.toJson(user));
    }

    private Result deleteUser(Long id) {
        User user = Ebean.find(User.class, id);
        Ebean.delete(user);
        return noContent(); // http://stackoverflow.com/a/2342589/1415732
    }

    // Authenticate
    public Result authenticate() {
        // Define form model for login params
        class Login {
            public Long     id;
            public String email;
            public String token;
            public String error;

            public Login() {
            }
        }
        // Check errors in form
        JsonNode request = request().body().asJson();

        Login ret = new Login();
        User user = User.findByEmail(request.get("email").asText());
        if (user == null) {
            ret.error = "Either email or password is incorrect. Try again.";
            return unauthorized(Json.toJson(ret));
        }

        // Validate user if found one
        String sha256 = User.sha256(request.get("password").asText());
        if (sha256.equals(user.getPassword())) {
            // Generate authToken and set in user
            String authToken = generateAuthToken();
            user.setAuthToken(authToken);
            Ebean.update(user);
            ret.token = authToken;
            ret.email = user.getEmail();
            ret.id = user.getId();
            return ok(Json.toJson(ret));
        }
        return unauthorized();
    }

    private String generateAuthToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes.toString();
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result authCall() {
        return ok("Very awesome content!");
    }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result logout(long id) {
        User user = Ebean.find(User.class, id);
        if (user != null) {
            user.setAuthToken(generateAuthToken());
            Ebean.save(user);
            return ok();
        }
        return unauthorized();
    }

    public Result search(String search_string)
    {
        List<User> users = null;
        String[] tokens = search_string.split(" ");
        if(search_string == "")
            users = Ebean.find(User.class).select("first_name, last_name").findList();
        else
            users = Ebean.find(User.class)
                .select("first_name, last_name")
                .where()
                .or(Expr.in("first_name", tokens), Expr.in("last_name", tokens))
                .findList();
        JsonContext jc = Ebean.json();
        return ok(jc.toJson(users));
    }
}
