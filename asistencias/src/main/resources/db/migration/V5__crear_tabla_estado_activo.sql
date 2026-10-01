CREATE TABLE estado_activo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_estado_activo PRIMARY KEY (id),
    CONSTRAINT uk_estado_activo_nombre UNIQUE (nombre)
);