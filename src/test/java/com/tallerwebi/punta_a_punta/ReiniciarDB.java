package com.tallerwebi.punta_a_punta;

import java.io.IOException;

public class ReiniciarDB {

  public static void limpiarBaseDeDatos() {
    try {
      String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
      String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
      String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
      String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
      String dbPassword = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "user";

      String sqlCommands =
        "SET FOREIGN_KEY_CHECKS=0; " +
        "DELETE FROM AutoPuja; " +
        "DELETE FROM Oferta; " +
        "DELETE FROM Subasta; " +
        "DELETE FROM Usuario; " +
        "ALTER TABLE AutoPuja AUTO_INCREMENT = 1; " +
        "ALTER TABLE Oferta AUTO_INCREMENT = 1; " +
        "ALTER TABLE Subasta AUTO_INCREMENT = 1; " +
        "ALTER TABLE Usuario AUTO_INCREMENT = 1; " +
        "SET FOREIGN_KEY_CHECKS=1; " +
        "INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true); " +
        "INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test2@unlam.edu.ar', 'test', 'USER', true);";

      String comando = String.format(
        "docker exec tallerwebi-mysql mysql -h %s -P %s -u %s -p%s %s -e \"%s\"",
        dbHost,
        dbPort,
        dbUser,
        dbPassword,
        dbName,
        sqlCommands
      );

      String sistemaOperativo = System.getProperty("os.name").toLowerCase();

      Process process;
      if (sistemaOperativo.contains("win")) {
        process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", comando });
      } else {
        process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", comando });
      }
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        System.out.println("Base de datos limpiada exitosamente");
      } else {
        System.err.println("Error al limpiar la base de datos. Exit code: " + exitCode);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Error ejecutando script de limpieza: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
