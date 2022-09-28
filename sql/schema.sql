CREATE TABLE empleado (
    id_empleado         MEDIUMINT NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(100) NOT NULL,
    apellidos           VARCHAR(100) NOT NULL,
    fecha_nacimiento    DATE NOT NULL,
    puesto              VARCHAR(100) NOT NULL,
    email               VARCHAR(320),
    PRIMARY KEY (id_empleado)
);

CREATE TABLE lenguaje (
    id_lenguaje         MEDIUMINT NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_lenguaje)
);

CREATE TABLE empleado_lenguaje (
    id_empleado         MEDIUMINT NOT NULL,
    id_lenguaje         MEDIUMINT NOT NULL,
    nivel               VARCHAR(10) NOT NULL,
    PRIMARY KEY (id_empleado, id_lenguaje),
    FOREIGN KEY (id_empleado) REFERENCES empleado (id_empleado),
    FOREIGN KEY (id_lenguaje) REFERENCES lenguaje (id_lenguaje)
);