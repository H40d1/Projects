# Definición del esquema y las tablas

DROP SCHEMA IF EXISTS series;
CREATE SCHEMA series;
USE series;

CREATE TABLE serie (
  id_serie INT,
  titulo VARCHAR(100),
  sinopsis VARCHAR(200),
  idioma VARCHAR(50),
  fecha_estreno DATE,
  PRIMARY KEY (id_serie)
);

CREATE TABLE temporada (
  id_serie INT,
  n_temporada INT,
  n_capitulos INT,
  fecha_estreno DATE,
  PRIMARY KEY (id_serie, n_temporada),
  FOREIGN KEY (id_serie) REFERENCES serie (id_serie)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE capitulo (
  id_serie INT,
  n_temporada INT,
  n_orden INT,
  fecha_estreno DATE,
  titulo INT,
  PRIMARY KEY (id_serie, n_temporada, n_orden),
  FOREIGN KEY (id_serie, n_temporada) REFERENCES temporada (id_serie, n_temporada)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE usuario (
  id_usuario INT,
  nombre VARCHAR(50),
  apellido1 VARCHAR(50),
  apellido2 VARCHAR(50),
  fotografia LONGBLOB,
  PRIMARY KEY (id_usuario)
);

CREATE TABLE genero (
  id_genero INT,
  descripcion VARCHAR(50),
  PRIMARY KEY (id_genero)
);

CREATE TABLE pertenece (
  id_serie INT,
  id_genero INT,
  PRIMARY KEY (id_serie, id_genero),
  FOREIGN KEY (id_serie) REFERENCES serie (id_serie)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_genero) REFERENCES genero (id_genero)
    ON DELETE CASCADE ON UPDATE CASCADE
);
