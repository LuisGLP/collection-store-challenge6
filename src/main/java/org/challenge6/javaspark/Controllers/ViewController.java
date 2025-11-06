package org.challenge6.javaspark.Controllers;


import org.challenge6.javaspark.entity.Item;
import org.challenge6.javaspark.entity.User;
import org.challenge6.javaspark.services.ItemService;
import org.challenge6.javaspark.services.UserService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewController {
    private final ItemService itemService;
    private final UserService userService;
    private final MustacheTemplateEngine templateEngine;

    public ViewController() {
        this.itemService = ItemService.getInstance();
        this.userService = UserService.getInstance();
        this.templateEngine = new MustacheTemplateEngine();
    }

    public String renderAuctionList(Request req, Response res) {
        Map<String, Object> model = new HashMap<>();

        try {
            List<Item> items = itemService.getItemsByStatus("active");
            model.put("items", items);
            model.put("title", "Subastas Activas");
        } catch (Exception e) {
            model.put("error", "Error al cargar las subastas");
        }

        return templateEngine.render(new ModelAndView(model, "auction-list.mustache"));
    }

    public String renderAuctionDetail(Request req, Response res) {
        String itemId = req.params(":id");
        Map<String, Object> model = new HashMap<>();

        try {
            Item item = itemService.getItemById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item no encontrado"));

            List<User> users = userService.getAllUsers();

            model.put("item", item);
            model.put("users", users);
            model.put("itemId", itemId);
            model.put("title", "Subasta: " + item.getName());
        } catch (Exception e) {
            model.put("error", "Error al cargar la subasta");
        }

        return templateEngine.render(new ModelAndView(model, "auction-detail.mustache"));
    }
}
