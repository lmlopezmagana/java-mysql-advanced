package net.openwebinars.java.mysql.advanced.transactions;

import net.openwebinars.java.mysql.advanced.pool.MyDataSource;

import javax.xml.transform.Result;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SavePointExample {

    public void test() {

        try (Connection conn = MyDataSource.getConnection()) {

            // Autocommit desactivado
            conn.setAutoCommit(false);

            String sql1 = "INSERT INTO empleado (nombre, apellidos, fecha_nacimiento, puesto, email) VALUES (?, ?, ?, ?, ?)";
            String sql2 = "SELECT id_lenguaje FROM lenguaje WHERE nombre = ?";
            String sql3 = "INSERT INTO empleado_lenguaje (id_empleado, id_lenguaje, nivel) VALUES (?, ?, ?)";


            String[] empleado = {
                    "Carlos", "Bueno Alfaro", "27/04/1991", "programador", "carlos@openwebinars.net"
            };

            // Es un programador tan malo que no conoce ningún lenguaje :S
            // La estructura de datos es un Map: Lenguaje, Nivel
            Map<String, String> lenguajesConocidos = Collections.emptyMap();

            try (PreparedStatement pstm1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstm2 = conn.prepareStatement(sql2);
                 PreparedStatement pstm3 = conn.prepareStatement(sql3)) {


                Savepoint savepoint1 = conn.setSavepoint("inicio");

                pstm1.setString(1, empleado[0]);
                pstm1.setString(2, empleado[1]);
                pstm1.setDate(3, Date.valueOf(LocalDate.parse(empleado[2], DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                pstm1.setString(4, empleado[3]);
                pstm1.setString(5, empleado[4]);

                pstm1.executeUpdate();


                Map<String, Integer> lenguajesParaActualizar = new HashMap<>();

                for(String lenguaje : lenguajesConocidos.keySet()) {
                    pstm2.setString(1, lenguaje);
                    try (ResultSet rs = pstm2.executeQuery()) {
                        while (rs.next()) {
                            lenguajesParaActualizar.put(lenguaje, rs.getInt(1));
                        }
                    }
                }

                if (lenguajesParaActualizar.isEmpty()) {
                    // Si no conoce ningún lenguaje de programación
                    System.out.println("Realizando rollback al savepoint1");
                    conn.rollback(savepoint1);

                    // Lo reinsertamos, pero como ayudante, no como programador.
                    pstm1.setString(4, "ayudante");

                    pstm1.executeUpdate();
                    System.out.println("Actualizando para convertirlo en ayudante");

                } else {

                    // Recogemos el ID del empleado insertado
                    int emp_id = 0;

                    try (ResultSet rs = pstm1.getGeneratedKeys()) {
                        while (rs.next()) {
                            emp_id = rs.getInt(1);
                        }
                    }

                    // Si ha habido un problema al obtener este id, también volvemos al punto de inicio.
                    if (emp_id == 0) {
                        conn.rollback(savepoint1);
                        System.out.println("Realizando rollback al savepoint1");
                    }

                    for(String lenguaje : lenguajesConocidos.keySet()) {

                        int leng_id = lenguajesParaActualizar.get(lenguaje);

                        pstm3.setInt(1, emp_id);
                        pstm3.setInt(2, leng_id);
                        pstm3.setString(3, lenguajesConocidos.get(lenguaje));

                        pstm3.executeUpdate();

                    }

                    // Liberamos el savepoint
                    conn.releaseSavepoint(savepoint1);
                    System.out.println("Savepont1 liberado");

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
