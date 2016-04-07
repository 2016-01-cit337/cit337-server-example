package controllers;

import play.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.mvc.*;
import play.mvc.Controller;
import play.libs.*;

import javax.inject.Inject;
import javax.validation.Constraint;

/**
 * Created by npatel on 3/17/16.
 */
public class AuthController extends Controller {
    @Inject FormFactory formFactory;
    public Result register(){
        Form<RegisterForm> form = formFactory.form(RegisterForm.class).bindFromRequest();

        if(form.hasErrors()) {
            JsonNode node = form.errorsAsJson();    // Get errors
            RegisterForm error = new RegisterForm();

            if(form.error("first_name") != null)
                error.first_name = node.get("first_name").get(0).asText();
            if(form.error("last_name") != null)
                error.last_name = node.get("last_name").get(0).asText();
            if(form.error("email") != null)
                error.email = node.get("email").get(0).asText();
            if(form.error("password") != null)
                error.password = node.get("password").get(0).asText();
            return badRequest(Json.toJson(error));
        }
        RegisterForm registerForm = form.get();
        if(registerForm.password != registerForm.confirm_password)
        {
            RegisterForm error = new RegisterForm();
            error.password = error.confirm_password = "Both passwords must match";
            return badRequest(Json.toJson(error));
        }
        // Save the entry

        return ok("Success");

    }

    public static class RegisterForm{
        @Constraints.Required
        @Constraints.MinLength(3)
        public String first_name;

        @Constraints.Required
        @Constraints.MinLength(3)
        public String last_name;

        @Constraints.Required
        @Constraints.Email
        public String email;

        @Constraints.Required
        @Constraints.MinLength(6)
        public String password;

        public String confirm_password;
    }
}
