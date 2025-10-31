package org.challenge6.javaspark;

import org.challenge6.javaspark.Controllers.UserController;
import org.challenge6.javaspark.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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

            port(4567);
            staticFiles.location("/public");

            // Configuración de CORS
            enableCORS();

            // Inicializar controladores
            UserController userController = new UserController();

            get("/api/users", userController::getAllUsers);
            get("/api/users/:id", userController::getUserById);
            post("/api/users/:id", userController::addUser);
            put("/api/users/:id", userController::updateUser);
            options("/api/users/:id", userController::checkUserExists);
            delete("/api/users/:id", userController::deleteUser);


            // Shutdown hook para cerrar la base de datos correctamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Cerrando aplicación...");
                DatabaseConfig.close();
                stop();
            }));

            // Log de inicio
            logger.info("===========================================");
            logger.info(" Server sucessfully initialized");
            logger.info(" URL: http://localhost:4567");
            logger.info(" Database connected successfully");
            logger.info("===========================================");


            get("/hello", (req, res)->"Hello, world");

            get("/hello/:name", (req,res)->{
                return "Hello, "+ req.params(":name");
            });
        }
    private static void enableCORS() {
        options("/*", (request, response) -> {
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