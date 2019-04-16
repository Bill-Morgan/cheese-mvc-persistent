package org.launchcode.controllers;

import org.apache.catalina.LifecycleState;
import org.hibernate.type.TrueFalseType;
import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private MenuDao menuDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {

       Iterable<Category> categories = categoryDao.findAll();

       for (Category cat : categories) { //test for empty list.  if list not empty add cheese
           model.addAttribute("title", "Add Cheese");
           model.addAttribute(new Cheese());
           model.addAttribute("cheeseCategories", categories);
           return "cheese/add";
       }
       model.addAttribute("title", "Please add a Category before adding Cheeses");
       model.addAttribute("category", new Category());
       return "category/add";

    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors,
                                       @RequestParam Integer categoryId,
                                       Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            return "cheese/add";
        }
        int noCat = -1;
        if (!categoryId.equals(noCat)) {
            Category cat = categoryDao.findOne(categoryId);
            newCheese.setCategory(cat);
        }
        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds) {

        for (Integer cheeseId : cheeseIds) {
            cheeseDao.delete(cheeseId);
        }

        return "redirect:";
    }

    @RequestMapping(value = "category/{categoryId}", method = RequestMethod.GET)
    public String cheeseOfCategory(Model model, @PathVariable Integer categoryId){

        for (Category category : categoryDao.findAll()) {
            if (categoryId.equals(category.getId())) {
                model.addAttribute("cheeses", category.getCheeses());
                model.addAttribute("title", "Cheeses of Category: " + category.getName());
                break;
            }
        }
        return "cheese/index";
    }

    @RequestMapping(value = "edit/{cheeseId}", method = RequestMethod.GET)
    public String displayEditCheeseForm(Model model, @PathVariable int cheeseId){

        model.addAttribute("title", "Edit Cheese: " + cheeseDao.findOne(cheeseId).getName());
        model.addAttribute("cheese", cheeseDao.findOne(cheeseId));
        model.addAttribute("cheeseCategories", categoryDao.findAll());
        return "cheese/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String processEditCheeseForm(@ModelAttribute  @Valid Cheese theCheese,
                                        Errors errors,
                                        @RequestParam int id,
                                        @RequestParam int categoryId,
                                        Model model){
        if (errors.hasErrors()) {
            model.addAttribute("cheese", theCheese);
            model.addAttribute("title", "Edit Cheese: " + theCheese.getName());
        }
        Cheese cheese = cheeseDao.findOne(id);
        cheese.setCategory(categoryDao.findOne(categoryId));
        cheese.setDescription(theCheese.getDescription());
        cheese.setName(theCheese.getName());
        cheeseDao.save(cheese);
        return "redirect:";
    }

    private Boolean isEmptyList(List theList) {
        for (Object cat : theList) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }
}
