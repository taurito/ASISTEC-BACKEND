CREATE TABLE unidad (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        codigo VARCHAR(20) NOT NULL,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(255),
                        activo BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,

                        CONSTRAINT pk_unidad PRIMARY KEY (id),
                        CONSTRAINT uk_unidad_codigo UNIQUE (codigo)
);