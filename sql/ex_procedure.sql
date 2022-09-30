DELIMITER $$
DROP PROCEDURE IF EXISTS obtenerEmpleadoPorId$$
CREATE PROCEDURE obtenerEmpleadoPorId(IN emp_id MEDIUMINT)
BEGIN
    SELECT *
    FROM empleado
    WHERE id_empleado = emp_id;
END$$
DELIMITER ;

CALL obtenerEmpleadoPorId(1);