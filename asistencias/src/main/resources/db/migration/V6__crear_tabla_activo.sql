CREATE TABLE activo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    modelo VARCHAR(100),
    serie VARCHAR(100),
    codigo_activo VARCHAR(50) NOT NULL,

    tipo_activo_id BIGINT NOT NULL,
    marca_id BIGINT NOT NULL ,
    estado_activo_id BIGINT NOT NULL,

    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT pk_activo PRIMARY KEY (id),

    CONSTRAINT uk_activo_codigo UNIQUE (codigo_activo),
    CONSTRAINT uk_activo_serie UNIQUE (serie),

    CONSTRAINT fk_ativo_tipo_activo
        FOREIGN KEY (tipo_activo_id)
        REFERENCES tipo_activo(id),

    CONSTRAINT fk_activo_marca
        FOREIGN KEY (marca_id)
        REFERENCES marca(id),

    CONSTRAINT fk_activo_estado_activo
        FOREIGN KEY (estado_activo_id)
        REFERENCES estado_activo(id)

);