package main.java.utils;

public class DatabaseSetup {


    void borrarTablas(){
        String sql = "DROP";
    }

    boolean crearTablas(){
        boolean tablasCreadas = false;

        String sql = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    telefono BIGINT,
                    activo BOOLEAN NOT NULL,
                    fecha_registro DATE
                )
                """;



        return tablasCreadas;
    }




}
