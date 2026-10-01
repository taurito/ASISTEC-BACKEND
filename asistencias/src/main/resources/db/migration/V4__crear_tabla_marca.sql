CREATE TABLE marca (
        id BIGINT NOT NULL AUTO_INCREMENT,
        nombre VARCHAR(50) NOT NULL,
        descripcion VARCHAR(255),
        activo BOOLEAN NOT NULL DEFAULT TRUE,

        CONSTRAINT pk_marca PRIMARY KEY (id),
        CONSTRAINT uk_marca_nombre UNIQUE (nombre)
);