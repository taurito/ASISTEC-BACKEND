CREATE TABLE rol (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_rol PRIMARY KEY (id),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    empleado_id BIGINT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT uk_usuario_empleado UNIQUE (empleado_id),

    CONSTRAINT fk_usuario_empleado
        FOREIGN KEY (empleado_id)
        REFERENCES empleado(id)
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,

    CONSTRAINT pk_usuario_rol
        PRIMARY KEY (usuario_id, rol_id),

    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario(id),

    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (rol_id)
            REFERENCES rol(id)
);

INSERT INTO rol (nombre, descripcion)
VALUES
    ('ADMIN', 'Administrador del sistema'),
    ('TECNICO', 'Técnico de la unidad de informática'),
    ('CONSULTA', 'Usuario con permisos de consulta');