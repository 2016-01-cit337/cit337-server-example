package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import play.data.validation.Constraints;
import play.*;

import javax.annotation.Generated;
import javax.persistence.*;
import javax.validation.Constraint;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by npatel on 3/18/16.
 */
@Entity
public class User extends Model{
    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    @Constraints.MinLength(3)
    private String first_name;

    @Constraints.Required
    @Constraints.MinLength(3)
    private String last_name;

    @Constraints.Required
    @Constraints.Email
    private String email;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private String authToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
            this.password = User.sha256(password);
    }

    @Constraints.Required
    @Constraints.MinLength(6)
    private String password;

    // Methods
    //public static Finder<Long,User> find = new Finder<>(Long.class, User.class);

    public static User findByEmail(String email)
    {
        return Ebean.find(User.class)
                .where()
                .like("email", email)
                .findUnique();
    }

    /**
     * Return a page of computer
     *
     * @param page     Page to display
     * @param pageSize Number of computers per page
     * @param sortBy   Computer property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public static PagedList<User> page(int page, int pageSize, String sortBy,
                                  String order, String filter) {
        return Ebean.find(User.class)
                .where()
                .ilike("first_name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .fetch("company")
                .findPagedList(pageSize*(page-1)+1, pageSize*page);
    }

    // Transient field
    @Transient
    String confirm_password;

    // Transient field
    public String getConfirm_password() {
        return confirm_password;
    }
    public void setConfirm_password(String confirm_password) {
        this.confirm_password = User.sha256(confirm_password);
    }

    // 256 Sha hashing
    public static String sha256(String input) {
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Publication> publications;

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }
}
