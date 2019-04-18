package org.launchcode.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Menu {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min = 3, max = 15)
    private String name;

    @ManyToMany(mappedBy = "menus")
    private List<Cheese> cheeses;

    public Menu() {
    }

    public Menu(String name) {
        this.name = name;
    }

    public void addItem(Cheese item) {
        cheeses.add(item);
    }

    public void removeItem(Integer cheeseId) {
        cheeses.remove(cheeseId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Cheese> getCheeses() {
        return cheeses;
    }
}
