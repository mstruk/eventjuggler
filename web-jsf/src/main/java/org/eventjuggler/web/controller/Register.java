package org.eventjuggler.web.controller;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.eventjuggler.model.User;
import org.eventjuggler.services.DuplicateLoginException;
import org.eventjuggler.services.PasswordHashService;
import org.eventjuggler.services.UserService;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@ConversationScoped
@Named
@Stateful
public class Register implements Serializable {

    @EJB
    UserService userService;

    private String name;
    private String lastName;
    private Integer age;
    private String gender;
    private String country;
    private String username;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String register() {
        User u = new User();
        u.setName(name);
        u.setLastName(lastName);
        u.setLogin(username);
        u.setPassword(password);

        try {
            userService.create(u);
            return "/login.xhtml";
        } catch (DuplicateLoginException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("The username is already taken. Try choose another."));
        }
        return null;
    }
}
