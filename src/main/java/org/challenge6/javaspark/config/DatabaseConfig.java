package org.challenge6.javaspark.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/collectibles_store";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";

    private DatabaseConfig() {
        // Constructor privado para patrón Singleton
    }

    public static void initialize() {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(DB_URL);
                config.setUsername(DB_USER);
                config.setPassword(DB_PASSWORD);

                // Configuración del pool de conexiones
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);

                // Propiedades adicionales de PostgreSQL
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                dataSource = new HikariDataSource(config);
                logger.info("Pool de conexiones inicializado correctamente");

                // Verificar la conexión
                try (Connection conn = dataSource.getConnection()) {
                    logger.info("Conexión a PostgreSQL establecida: {}", conn.getMetaData().getDatabaseProductVersion());
                }

            } catch (Exception e) {
                logger.error("Error al inicializar el pool de conexiones", e);
                throw new RuntimeException("No se pudo conectar a la base de datos", e);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            initialize();
        }
        return dataSource;
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexiones cerrado");
        }
    }
}
