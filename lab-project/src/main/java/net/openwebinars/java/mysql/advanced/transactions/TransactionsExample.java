package net.openwebinars.java.mysql.advanced.transactions;

import net.openwebinars.java.mysql.advanced.pool.MyDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionsExample {

    public void transactionExample() {


        try (Connection conn = MyDataSource.getConnection()) {

            // Autocommit desactivado
            conn.setAutoCommit(false);

            // Obtenemos el ID de un empleado por su nombre y apellidos
            String sql1 = "SELECT id_empleado FROM empleado WHERE nombre = ? AND apellidos = ?";

            // Obtenemos el ID de los lenguajes que ya conoce un empleado
            String sql2 = "SELECT id_lenguaje FROM empleado_lenguaje WHERE id_empleado = ?";

            // Obtenemos el ID de un lenguaje por nombre
            String sql3 = "SELECT id_lenguaje FROM lenguaje WHERE nombre = ?";

            // Actualizamos el nivel de conocimiento de un lenguaje de un empleado
            String sql4 = "UPDATE empleado_lenguaje SET nivel = ? WHERE id_empleado = ? AND id_lenguaje = ?";

            // Insertamos el conocimiento de un nuevo lenguaje de programación para un empleado
            String sql5 = "INSERT INTO empleado_lenguaje (id_empleado, id_lenguaje, nivel) VALUES (?, ?, ?)";

            // Empleado a buscar, lo hemos insertado a través del procedimiento almacenado
            String nombre = "Antonio", apellidos = "Mora Ramírez";

            // Estos son los datos a actualizar, lenguaje -> nivel de conocimiento
            Map<String, String> actualizacionNiveles = Map.of("Java", "Intermedio", "Python", "Avanzado", "Go", "Intermedio");


            try (PreparedStatement pstm1 = conn.prepareStatement(sql1);
                 PreparedStatement pstm2 = conn.prepareStatement(sql2);
                 PreparedStatement pstm3 = conn.prepareStatement(sql3)) {


                // AQUÍ COMIENZA LA TRANSACCIÓN
                // Es algo que JDBC hace por nosotros.

                // Primero, buscamos el ID del del empleado

                pstm1.setString(1, nombre);
                pstm1.setString(2, apellidos);

                int emp_id = -1;


                try (ResultSet rs = pstm1.executeQuery()) {
                    while (rs.next()) {
                        emp_id = rs.getInt(1);
                    }
                }


                if (emp_id != -1) { // El empleado existe, seguimos

                    // Buscamos los lenguajes de programación conocidos por el usuario
                    // Esta búsqueda nos sirve para después elegir entre actualizar
                    // (UPDATE) o insertar (INSERT)

                    pstm2.setInt(1, emp_id);

                    List<Integer> lenguajesConocidos = new ArrayList<>();


                    try (ResultSet rs = pstm2.executeQuery()) {
                        while (rs.next()) {
                            lenguajesConocidos.add(rs.getInt(1));
                        }
                    }


                    // Este Map es similar al Map actualizacionNiveles.
                    // Incluye la misma clave, cada uno de los lenguajes de
                    // programación. Como valor, se consulta el ID de cada uno.
                    Map<String, Integer> lenguajesParaActualizar = new HashMap<>();

                    // Esta consulta sería francamente mejorable usando el operador IN
                    // Otros gestores de bases de datos, como PostgreSQL permiten usar
                    // el método setArray, pero MySQL no.

                    // Otra posible estrategia con MySQL podría ser el uso de
                    // la función FIND_IN_SET, usando la concatenación de todos los ID
                    for (String s : actualizacionNiveles.keySet()) {
                        pstm3.setString(1, s);
                        try (ResultSet rs = pstm3.executeQuery()) {
                            while (rs.next()) {
                                lenguajesParaActualizar.put(s, rs.getInt(1));
                            }
                        }
                    }


                    // Procedemos a realizar la actualización.
                    for (String s: lenguajesParaActualizar.keySet()) {

                        // Si el lenguaje a actualizar ya existe en empleado_lenguaje
                        if (lenguajesConocidos.contains(lenguajesParaActualizar.get(s))) {
                            // UPDATE
                            try(PreparedStatement pstm4 = conn.prepareStatement(sql4)) {
                                pstm4.setString(1, actualizacionNiveles.get(s));
                                pstm4.setInt(2, emp_id);
                                pstm4.setInt(3, lenguajesParaActualizar.get(s));

                                pstm4.executeUpdate();

                            }
                        // Si se trata de un lenguaje que no existe en empleado_lenguaje
                        } else {
                            // INSERT
                            try (PreparedStatement pstm5 = conn.prepareStatement(sql5)) {
                                pstm5.setInt(1, emp_id);
                                pstm5.setInt(2, lenguajesParaActualizar.get(s));
                                pstm5.setString(3, actualizacionNiveles.get(s));

                                pstm5.executeUpdate();

                            }
                        }


                    }

                }

                // Aquí se produce la confirmación de la transacción, y por tanto
                // su finalización. De esta forma, pasamos a un estado consistente.
                conn.commit();
                System.out.println("Transacción finalizada");
            } catch (SQLException ex) {
                // Deshacemos la transacción si ha habido algún problema
                conn.rollback();
                System.err.println("Se ha descartado la transacción");
                throw ex;
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }


}
