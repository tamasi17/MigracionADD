package main.java.utils;

import main.java.models.*;
import main.java.v1.DaoClienteV1;
import main.java.v1.DaoPedidoV1;
import main.java.v1.DaoProductoV1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static main.java.logging.LoggerProvider.getLogger;

/**
 * Clase que migra de prac2 a prac2migra con DriverManager (v1)
 */
public class JdbcMigrator {

    /**
     * Migra todas las tablas de prac2 a prac2migra
     * Como la transaccion tiene que ser atomica, compartiremos las dos conexiones sin cerrarlas
     * en *todos* los metodos que use migrarDb()
     * @return
     */
    public ResultadoMigracion migrarDB() {

        ResultadoMigracion resultadoMigracion = new ResultadoMigracion(
                "[all]", 0,
                0, 0,
                0, EstadoMigracion.PARCIAL
        );

           int totalErrores=0;
           int totalRecibidos=0;
           int totalInsertados=0;
           long tiempoTotal=0;



        try (Connection conOriginal = ConnectionFactory.getConnectionDmOriginal();
             Connection conMigra = ConnectionFactory.getConnectionDmMigrada()) {

            // Transaccion atomica en prac2migra
           conMigra.setAutoCommit(false);


           // MIGRACION CLIENTES
            Savepoint spClientes = conMigra.setSavepoint("spClientes");
            getLogger().info("[MIGRACION] Savepoint: Clientes");

            ResultadoMigracion migracionClientes = migrarClientes(conOriginal, conMigra);

            if (migracionClientes.getEstadoMigracion() != EstadoMigracion.COMPLETA){
                conMigra.rollback(spClientes); // Rollback si no ha migrado bien clientes
                getLogger().warn("Error al migrar: rollback "+
                        migracionClientes.getTablaMigrada() +" a "+ spClientes.getSavepointName());
            }

            totalErrores += migracionClientes.getErrores();
            totalRecibidos += migracionClientes.getRecibidos();
            totalInsertados += migracionClientes.getInsertados();
            tiempoTotal += migracionClientes.getTiempoTranscurrido();

            // MIGRACION PRODUCTOS
            Savepoint spProductos = conMigra.setSavepoint("spProductos");
            getLogger().info("[MIGRACION] Savepoint: Productos");

            ResultadoMigracion migracionProd = migrarProductos(conOriginal, conMigra);

            if (migracionProd.getEstadoMigracion() != EstadoMigracion.COMPLETA){
                conMigra.rollback(spProductos); // Rollback si no ha migrado bien productos
                getLogger().warn("Error al migrar: rollback "+
                        migracionClientes.getTablaMigrada() +" a "+ spProductos.getSavepointName());
            }

            totalErrores += migracionProd.getErrores();
            totalRecibidos += migracionProd.getRecibidos();
            totalInsertados += migracionProd.getInsertados();
            tiempoTotal += migracionProd.getTiempoTranscurrido();

            // MIGRACION PEDIDOS
            Savepoint spPedidos = conMigra.setSavepoint("spPedidos");
            getLogger().info("[MIGRACION] Savepoint: Pedidos");

            ResultadoMigracion migracionPedidos = migrarPedidos(conOriginal, conMigra);

            if (migracionPedidos.getEstadoMigracion() != EstadoMigracion.COMPLETA){
                conMigra.rollback(spPedidos); // Rollback si no ha migrado bien productos
                getLogger().warn("Error al migrar: rollback "+
                        migracionPedidos.getTablaMigrada() +" a "+ spPedidos.getSavepointName());
            }


            totalErrores += migracionPedidos.getErrores();
            totalRecibidos += migracionPedidos.getRecibidos();
            totalInsertados += migracionPedidos.getInsertados();
            tiempoTotal += migracionPedidos.getTiempoTranscurrido();


            resultadoMigracion.setErrores(totalErrores);
            resultadoMigracion.setRecibidos(totalRecibidos);
            resultadoMigracion.setInsertados(totalInsertados);
            resultadoMigracion.setTiempoTranscurrido(tiempoTotal);

            if (totalErrores == 0) {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.COMPLETA);
            } else {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.PARCIAL);
            }

            conMigra.setAutoCommit(true);

        } catch (SQLException sqle) {
            getLogger().error("Fallo al migrar base de datos");
        }

        escribeResultado(resultadoMigracion);

        return resultadoMigracion;

    }

    private ResultadoMigracion migrarClientes(Connection conOriginal, Connection conMigra) {

        ResultadoMigracion resultadoMigracion = new ResultadoMigracion(
                "clientes", 0,
                0, 0,
                0, EstadoMigracion.PARCIAL
        );

        try{

            long tiempo = System.currentTimeMillis();
            int errores = 0;

            DaoClienteV1 daoCliente = new DaoClienteV1();

            // Recibimos datos de clientes original
            List<Cliente> clientesOriginal = daoCliente.findAll();
            int recibidos = clientesOriginal.size();

            // Creamos y cargamos la base de datos migrada
            DbSetup.crearTablaClientesMigrada();

            // Solo suma un error si varios fallan en el batch, version con errores dentro de DataLoader?
            try {
                DataLoader.cargarClientesMigrada(daoCliente, clientesOriginal, conMigra);
            } catch (SQLException sqle) {
                errores++;
            }

            int insertados = daoCliente.findAllMigra(conMigra).size();
            tiempo = (System.currentTimeMillis() - tiempo);


            resultadoMigracion.setErrores(errores);
            resultadoMigracion.setRecibidos(recibidos);
            resultadoMigracion.setInsertados(insertados);
            resultadoMigracion.setTiempoTranscurrido(tiempo);

            if (errores == 0) {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.COMPLETA);
            } else {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.PARCIAL);
            }


            getLogger().info("Migracion clientes:"+resultadoMigracion.getEstadoMigracion().toString());

        } catch (Exception e) {
            resultadoMigracion.setEstadoMigracion(EstadoMigracion.FALLIDA);
            getLogger().error("Problema al conectarse a las bases de datos durante la migracion");
        }

        return resultadoMigracion;
    }

    private ResultadoMigracion migrarProductos(Connection conOriginal, Connection conMigra) {

        ResultadoMigracion resultadoMigracion = new ResultadoMigracion(
                "productos", 0,
                0, 0,
                0, EstadoMigracion.PARCIAL
        );

        try{

            long tiempo = System.currentTimeMillis();
            int errores = 0;

            DaoProductoV1 dao = new DaoProductoV1();

            // Recibimos datos de prod original
            List<Producto> prodOriginal = dao.findAll();
            int recibidos = prodOriginal.size();

            // Creamos y cargamos la base de datos migrada
            DbSetup.crearTablaProductosMigrada();

            // Solo suma un error si varios fallan en el batch, version con errores dentro de DataLoader?
            try {
                DataLoader.cargarProductosMigrada(dao, prodOriginal, conMigra);
            } catch (SQLException sqle) {
                errores++;
            }

            int insertados = dao.findAllMigra(conMigra).size();
            tiempo = (System.currentTimeMillis() - tiempo);


            resultadoMigracion.setErrores(errores);
            resultadoMigracion.setRecibidos(recibidos);
            resultadoMigracion.setInsertados(insertados);
            resultadoMigracion.setTiempoTranscurrido(tiempo);

            if (errores == 0) {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.COMPLETA);
            } else {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.PARCIAL);
            }


            getLogger().info("Migracion productos:"+resultadoMigracion.getEstadoMigracion().toString());

        } catch (Exception e) {
            resultadoMigracion.setEstadoMigracion(EstadoMigracion.FALLIDA);
            getLogger().error("Problema al conectarse a las bases de datos durante la migracion");
        }

        return resultadoMigracion;
    }

    private ResultadoMigracion migrarPedidos(Connection conOriginal, Connection conMigra) {

        ResultadoMigracion resultadoMigracion = new ResultadoMigracion(
                "pedidos", 0,
                0, 0,
                0, EstadoMigracion.PARCIAL
        );

        try{

            long tiempo = System.currentTimeMillis();
            int errores = 0;

            DaoPedidoV1 dao = new DaoPedidoV1();

            // Recibimos datos de pedidos original
            List<Pedido> pedidos = dao.findAll();
            int recibidos = pedidos.size();

            // Creamos y cargamos la base de datos migrada
            DbSetup.crearTablaPedidosMigrada();

            // Solo suma un error si varios fallan en el batch, version con errores dentro de DataLoader?
            try {
                DataLoader.cargarPedidosMigrada(dao, pedidos, conMigra);
            } catch (SQLException sqle) {
                errores++;
            }

            int insertados = dao.findAllMigra(conMigra).size();
            tiempo = (System.currentTimeMillis() - tiempo);


            resultadoMigracion.setErrores(errores);
            resultadoMigracion.setRecibidos(recibidos);
            resultadoMigracion.setInsertados(insertados);
            resultadoMigracion.setTiempoTranscurrido(tiempo);

            if (errores == 0) {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.COMPLETA);
            } else {
                resultadoMigracion.setEstadoMigracion(EstadoMigracion.PARCIAL);
            }


            getLogger().info("Migracion pedidos:"+resultadoMigracion.getEstadoMigracion().toString());

        } catch (Exception e) {
            resultadoMigracion.setEstadoMigracion(EstadoMigracion.FALLIDA);
            getLogger().error("Problema al conectarse a las bases de datos durante la migracion");
        }

        return resultadoMigracion;
    }

    /**
     * Metodo que escribe el resultado de la migracion en un fichero txt.
     * Acumulable, no borra la info anterior.
     * @param rm
     */
    private void escribeResultado(ResultadoMigracion rm){

        // Carpeta
        File carpeta = new File("migracion");
        if (!carpeta.exists()) {
            carpeta.mkdirs(); // create folder if it doesn't exist
        }

        // Fichero objetivo
        File file = new File(carpeta, "resultadoMigracion.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("\n"+ rm.toString());
            writer.newLine();
            writer.write(String.valueOf(LocalDate.now()));
            getLogger().info("Resultado migración escrito en " + file.getAbsolutePath());
        } catch (IOException ioe) {
            getLogger().error("Error escribiendo resultado migracion: "+ ioe.getLocalizedMessage());
        }
    }

}
