package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * Created by npatel on 4/5/16.
 */
@Entity
public class Publication extends Model {
    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    private String authors;

    @Constraints.Required
    private String title;

    @Constraints.Required
    private String location;

    @Constraints.Required
    private int startPage;

    @Constraints.Required
    private int endPage;

    @Constraints.Required
    private String month;

    @Constraints.Required
    private int year;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
