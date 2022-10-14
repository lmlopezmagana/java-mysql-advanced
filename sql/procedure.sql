DELIMITER $$

DROP PROCEDURE IF EXISTS `debug_msg`$$

CREATE PROCEDURE debug_msg(enabled INTEGER, msg VARCHAR(255))
BEGIN
  IF enabled THEN
    select concat('** ', msg) AS '** DEBUG:';
  END IF;
END $$


DELIMITER ;

DELIMITER $$

DROP FUNCTION IF EXISTS SPLIT_STR_POS$$

-- Java, Go, Python, Typescript

CREATE FUNCTION SPLIT_STR_POS(
  x VARCHAR(255),
  delim VARCHAR(12),
  pos INT
)
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN 
    RETURN TRIM(REPLACE(SUBSTRING(SUBSTRING_INDEX(x, delim, pos),
       LENGTH(SUBSTRING_INDEX(x, delim, pos -1)) + 1),
       delim, ''));
END$$

DELIMITER ;


DELIMITER $$

DROP FUNCTION IF EXISTS OCURRENCES$$
CREATE FUNCTION OCURRENCES(
    x VARCHAR(255),
    delim VARCHAR(12)
) RETURNS INT DETERMINISTIC
BEGIN
    RETURN LENGTH(x) - LENGTH(REPLACE(x, delim, ''));
END$$

DELIMITER ;


DELIMITER $$

DROP FUNCTION IF EXISTS NUM_ELEMENTS$$
CREATE FUNCTION NUM_ELEMENTS(
    x VARCHAR(255),
    delim VARCHAR(12)
) RETURNS INT DETERMINISTIC
BEGIN
    RETURN OCURRENCES(x, delim) + 1;
END$$

DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS insertar_programador$$
CREATE PROCEDURE insertar_programador(
    IN e_nombre VARCHAR(100),
    IN e_apellidos VARCHAR(100),
    IN e_fecha_nacimiento VARCHAR(20),
    IN e_email VARCHAR(100),
    IN e_lenguajes VARCHAR(255),
    OUT id_empleado MEDIUMINT)
BEGIN

    DECLARE i INT DEFAULT 1;
    DECLARE leng VARCHAR(40) DEFAULT '';
    DECLARE leng_id MEDIUMINT DEFAULT 0;
    DECLARE search INT DEFAULT 0;
    DECLARE num_lenguajes INT;


    -- Insertamos el nuevo empleado

    INSERT INTO empleado (nombre, apellidos, fecha_nacimiento, puesto, email)
    VALUES (e_nombre, e_apellidos, STR_TO_DATE(e_fecha_nacimiento,'%d/%m/%Y'), 'programador', e_email);

    SET id_empleado = last_insert_id();

    -- Procesamos los lenguajes de programación.
    -- Si hay alguno que no existe, lo insertamos
    -- Después, recogemos todos los IDs y los asignamos 
    -- al empleado recién insertado
        
    WHILE i <= (SELECT NUM_ELEMENTS(e_lenguajes, ',')) DO
    	-- call debug_msg(TRUE, (SELECT concat_ws('','i:', i)));
        SET leng = (SELECT SPLIT_STR_POS(e_lenguajes, ',', i));
 		-- call debug_msg(TRUE, (SELECT concat_ws('','leng:', leng)));
        -- call debug_msg(TRUE, (SELECT GROUP_CONCAT(nombre) FROM lenguaje));
        SET num_lenguajes = (SELECT COUNT(*) FROM lenguaje);
        
        IF (num_lenguajes > 0) THEN
        	SET search = (SELECT FIND_IN_SET(leng, (SELECT GROUP_CONCAT(nombre) FROM lenguaje)));
        END IF;
        IF (search = 0) THEN
        	-- call debug_msg(TRUE, 'Lenguaje no insertado en la base de datos');      
            INSERT INTO lenguaje (nombre) VALUES (leng);
            
            SET leng_id = last_insert_id();
            
            ELSE
				-- call debug_msg(TRUE, 'Lenguaje ya insertado en la base de datos');      

                SET leng_id = (SELECT id_lenguaje FROM lenguaje WHERE nombre = leng);
                
        END IF; 

		-- call debug_msg(TRUE, (SELECT concat_ws('','id_lenguaje:', leng_id)));
		-- call debug_msg(TRUE, (SELECT concat_ws('','id_empleado:', id_empleado)));

        INSERT INTO empleado_lenguaje (id_empleado, id_lenguaje, nivel)
        VALUES (id_empleado, leng_id, 'básico');

		SET i = i+1;

    END WHILE;



END
$$

DELIMITER ;




-- CALL insertar_programador('María','González Ferrán', '23/02/2001', 'maria@openwebinars.net','Go, Java, Python', @id_empleado);
-- SELECT @id_empleado;