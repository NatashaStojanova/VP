package mk.ukim.finki.natashastojanova.vp.web.controllers;

import mk.ukim.finki.natashastojanova.vp.exceptions.IngredientAlreadyExistsException;
import mk.ukim.finki.natashastojanova.vp.exceptions.IngredientNotFoundException;
import mk.ukim.finki.natashastojanova.vp.exceptions.NoMoreSpicyIngredientsException;
import mk.ukim.finki.natashastojanova.vp.model.Ingredient;
import mk.ukim.finki.natashastojanova.vp.model.Pizza;
import mk.ukim.finki.natashastojanova.vp.model.PizzaIngredient;
import mk.ukim.finki.natashastojanova.vp.service.IngredientService;
import mk.ukim.finki.natashastojanova.vp.service.PizzaIngredientService;
import mk.ukim.finki.natashastojanova.vp.service.PizzaService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Natasha Stojanova
 */
@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private IngredientService ingredientService;
    private PizzaIngredientService pizzaIngredientService;
    private PizzaService pizzaService;

    public IngredientController(IngredientService ingredientService, PizzaIngredientService pizzaIngredientService, PizzaService pizzaService) {
        this.ingredientService = ingredientService;
        this.pizzaIngredientService = pizzaIngredientService;
        this.pizzaService = pizzaService;
    }

    @PostMapping
    public void addIngredient(@ModelAttribute Ingredient ingredient) {
        //add new ingredient
        ingredientService.findAll().stream().forEach(ingredient1 -> {
            if (ingredient1.getName().equals(ingredient.getName()))
                throw new IngredientAlreadyExistsException();
        });
        if (ingredientService.findAll().stream()
                .filter(Ingredient::isSpicy)
                .collect(Collectors.toList()).size() >= 4) {
            throw new NoMoreSpicyIngredientsException();
        }

        ingredientService.save(ingredient);

    }

    @PatchMapping("/{id}")
    public void editIngredient(@ModelAttribute Ingredient ingredient, @PathVariable Long id) {
        //edit Ingredient
        ingredient.setId(id);
        if (ingredientService.findById(id).isPresent()) {
            if (ingredientService.findAll().stream().filter(Ingredient::isSpicy).map(Ingredient::getId).count() >= 4)
                throw new NoMoreSpicyIngredientsException();
            ingredientService.save(ingredient);
        } else {
            throw new IngredientNotFoundException();
        }

    }

    @DeleteMapping("/{id}")
    public void deleteIngredient(@ModelAttribute Ingredient ingredient, @PathVariable Long id) {
        //delete Ingredient
        if (ingredientService.findById(id).isPresent()) {
            ingredient = ingredientService.findById(id).get();
            ingredientService.delete(ingredient);
        }
        throw new IngredientNotFoundException();
    }

    @GetMapping
    public ModelAndView getIngredient(HttpServletRequest req, HttpServletResponse resp, @RequestParam(name = "spicy", required = false) boolean spicy) throws UnsupportedEncodingException {

        resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = context.getSession();

        List<Ingredient> allIngredients = new ArrayList<>();
        allIngredients = ingredientService.findAll();
        if (!spicy) {
            ModelAndView modelAndView = new ModelAndView("list-ingredients");
            modelAndView.addObject("ingredients", ingredientService.findAll());
            return modelAndView;
        }
        //spicy==true
        ModelAndView modelAndView = new ModelAndView("list-ingredients");
        modelAndView.addObject("ingredients", ingredientService.findAll().stream().filter(Ingredient::isSpicy).collect(Collectors.toList()));
        return modelAndView;

    }

    @GetMapping("/{id}")
    public Ingredient getIngredient(@PathVariable Long id) {
        //get Ingredient
        if (ingredientService.findById(id).isPresent()) {
            return ingredientService.findById(id).get();
        }
        throw new IngredientNotFoundException();
    }

    @GetMapping("/{id}/pizzas")
    public ModelAndView getAllPizzasWithId(HttpServletRequest req, HttpServletResponse resp, @PathVariable(name = "id") Long id) throws UnsupportedEncodingException {
        /*resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = context.getSession();*/

        if (ingredientService.findById(id).isPresent()) {
            List<Pizza> pizzas = pizzaIngredientService.findAll().stream()
                    .filter(ing -> ing.getIngredient().getId().equals(id))
                    .map(PizzaIngredient::getPizza)
                    .collect(Collectors.toList());
            ModelAndView modelAndView = new ModelAndView("pizza-ingredient");
            modelAndView.addObject("pizzas", pizzas);
            return modelAndView;
        } else
            throw new IngredientNotFoundException();
    }

    @GetMapping("/addIngredient")
    public ModelAndView addIngredient(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = context.getSession();*/

        ModelAndView modelAndView = new ModelAndView("add-ingredient");
        modelAndView.addObject("ingredient", new Ingredient());
        //modelAndView.addObject("bodyContent", "add-ingredient");
        return modelAndView;
    }

    @GetMapping("/editIngredient/{id}")
    public ModelAndView editIngredient(HttpServletRequest req, HttpServletResponse resp, @PathVariable(name = "id") Long ingredientID) throws IOException {
        /*resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = context.getSession();*/

        Ingredient ingredient = null;

        if (ingredientService.findById(ingredientID).isPresent())
            ingredient = ingredientService.findById(ingredientID).get();
        else
            throw new IngredientNotFoundException();

        ModelAndView modelAndView = new ModelAndView("edit-ingredient");
        modelAndView.addObject("ingredient", ingredient);
        return modelAndView;
    }

    @GetMapping("/deleteIngredient/{id}")
    public ModelAndView deleteIngredient(HttpServletRequest req, HttpServletResponse resp, @PathVariable(name = "id") Long ingredientID) throws IOException {
        /*resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = context.getSession();*/

        Ingredient ingredient = null;
        if (ingredientService.findById(ingredientID).isPresent()) {
            ingredient = ingredientService.findById(ingredientID).get();
        } else {
            throw new IngredientNotFoundException();
        }
        ModelAndView modelAndView = new ModelAndView("delete-ingredient");
        modelAndView.addObject("ingredient", ingredient);
        return modelAndView;
    }
}

