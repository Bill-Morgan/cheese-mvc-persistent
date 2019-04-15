package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {

        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                     Errors errors,
                                     Model model){

        if (errors.hasErrors()) {
            model.addAttribute("menu", newMenu);
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}")
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model, @PathVariable int menuId) {

        AddMenuItemForm menuForm = new AddMenuItemForm();
        menuForm.setMenu(menuDao.findOne(menuId));
        menuForm.setCheeses(cheeseDao.findAll());
        model.addAttribute("menuForm", menuForm);
        model.addAttribute("title", "Add item to menu: " + menuForm.getMenu().getName());
        return "menu/form";
    }

    @RequestMapping(value = "add-item", method =  RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute @Valid AddMenuItemForm menuForm,
                                       Errors errors,
                                       @RequestParam int menuId,
                                       @RequestParam Integer cheeseId,
                                       Model model){
        String existsError = "";
        for (Cheese cheese : menuDao.findOne(menuId).getCheeses()){
            if (cheeseId.equals(cheese.getId())){
                existsError = cheese.getName() + " is already in the " +
                                     menuDao.findOne(menuId).getName() + " menu.";
                break;
            }
        }
        if (errors.hasErrors() | !existsError.isEmpty()) {
            menuForm.setMenu(menuDao.findOne(menuId));
            menuForm.setCheeses(cheeseDao.findAll());
            model.addAttribute("menuForm", menuForm);
            model.addAttribute("menuForm", menuForm);
            model.addAttribute("existsError", existsError);
            model.addAttribute("title", "Add item to menu: " + menuDao.findOne(menuId).getName());
            return "menu/form";
        }
        Menu theMenu = menuDao.findOne(menuId);
        theMenu.addItem(cheeseDao.findOne(cheeseId));
        menuDao.save(theMenu);
        return "redirect:/menu/view/" + theMenu.getId();
    }

}
