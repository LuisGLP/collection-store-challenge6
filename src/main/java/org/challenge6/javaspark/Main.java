package org.challenge6.javaspark;

import org.challenge6.javaspark.Controllers.ItemController;
import org.challenge6.javaspark.Controllers.OfferController;
import org.challenge6.javaspark.Controllers.UserController;
import org.challenge6.javaspark.Controllers.ViewController;
import org.challenge6.javaspark.config.DatabaseConfig;
import org.challenge6.javaspark.exceptions.CustomException;
import org.challenge6.javaspark.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            DatabaseConfig.initialize();
            logger.info("Database connected successfully");
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }

        // ==================== CONFIGURACIÓN INICIAL ====================
        // IMPORTANTE: Estas configuraciones DEBEN ir ANTES de cualquier ruta
        port(4567);
        staticFiles.location("/public");

        // ==================== MANEJO GLOBAL DE EXCEPCIONES ====================
        // Manejar 404 - Ruta no encontrada
        notFound((req, res) -> ExceptionHandler.handleNotFound(req, res));

        // Manejar 500 - Error interno del servidor
        internalServerError((req, res) ->
                ExceptionHandler.handleInternalError(new Exception("Internal Server Error"), req, res)
        );

        // Manejar excepciones genéricas
        exception(Exception.class, (e, req, res) -> {
            res.body(ExceptionHandler.handleInternalError(e, req, res));
        });

        // Manejar excepciones personalizadas
        exception(CustomException.class, (e, req, res) -> {
            res.body(ExceptionHandler.handleCustomException(e, req, res));
        });

        // ==================== CONFIGURACIÓN DE CORS ====================
        enableCORS();

        // ==================== INICIALIZAR CONTROLADORES ====================
        UserController userController = new UserController();
        ItemController itemController = new ItemController();
        OfferController offerController = new OfferController();
        ViewController viewController = new ViewController();

        // ==================== VISTAS (Mustache) ====================
        get("/auctions", viewController::renderAuctionList);
        get("/auction/:id", viewController::renderAuctionDetail);

        // ==================== API USUARIOS ====================
        get("/api/users", userController::getAllUsers);
        get("/api/users/:id", userController::getUserById);
        post("/api/users/:id", userController::addUser);
        put("/api/users/:id", userController::updateUser);
        delete("/api/users/:id", userController::deleteUser);

        options("/api/users/:id", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            return userController.checkUserExists(req, res);
        });

        // ==================== API ITEMS ====================
        get("/api/items", itemController::getAllItems);
        get("/api/items/:id", itemController::getItemById);
        post("/api/items", itemController::addItem);
        put("/api/items/:id", itemController::updateItem);
        delete("/api/items/:id", itemController::deleteItem);

        options("/api/items/:id", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            return itemController.checkItemExists(req, res);
        });

        // ==================== API OFERTAS ====================
        get("/api/offers", offerController::getAllOffers);
        get("/api/offers/:id", offerController::getOfferById);
        post("/api/offers", offerController::addOffer);
        delete("/api/offers/:id", offerController::deleteOffer);

        // Ofertas por item
        get("/api/items/:itemId/offers", offerController::getOffersByItem);
        get("/api/items/:itemId/highest-offer", offerController::getHighestOffer);

        // Ofertas por usuario
        get("/api/users/:userId/offers", offerController::getOffersByUser);

        // ==================== RUTAS DE PRUEBA ====================
        get("/hello", (req, res) -> "Hello, world");

        get("/hello/:name", (req, res) -> {
            return "Hello, " + req.params(":name");
        });


        // ==================== PÁGINA PRINCIPAL ====================
        get("/", (req, res) -> {
            res.redirect("/auctions");
            return null;
        });

        // Shutdown hook para cerrar la base de datos correctamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Closing application...");
            DatabaseConfig.close();
            stop();
        }));

        // Log de inicio
        logger.info("===========================================");
        logger.info(" Server successfully initialized");
        logger.info(" URL: http://localhost:4567");
        logger.info(" Database connected successfully");
        logger.info("===========================================");
    }

    private static void enableCORS() {
        options("/*", (request, response) -> {
            String path = request.pathInfo();
            if (path.startsWith("/api/users/") ||
                    path.startsWith("/api/items/") ||
                    path.startsWith("/api/offers/")) {
                return "";
            }

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        });
    }
}