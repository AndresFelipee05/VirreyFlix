-- Datos para usuario
insert into usuario (id, email, nombre) values (1, "@gmail", "Andres");
insert into usuario (id, email, nombre) values (2, "@yahoo", "Jorge");
insert into usuario (id, email, nombre) values (3, "@hotmail", "Sergio");
insert into usuario (id, email, nombre) values (4, "@duck", "Celia");

-- Datos para perfil
insert into perfil (edad, id, usuario_id, nombre) values (19, 1, 1, "andresfv");
insert into perfil (edad, id, usuario_id, nombre) values (20, 2, 2, "procollado");
insert into perfil (edad, id, usuario_id, nombre) values (21, 3, 3, "sergiomejia");
insert into perfil (edad, id, usuario_id, nombre) values (18, 4, 4, "celiajimenez");

-- Datos para genero
insert into genero (id, nombre) values (1, "Terror");
insert into genero (id, nombre) values (2, "Comedia");
insert into genero (id, nombre) values (3, "Drama");

-- Datos para serie
insert into serie (calificacion_edad, id, titulo) values (16, 1, "Stranger Things");
insert into serie (calificacion_edad, id, titulo) values (3, 2, "Bluey");
insert into serie (calificacion_edad, id, titulo) values (12, 3, "Dr House");

-- Datos para serie_genero
insert into serie_genero (genero_id, serie_id) values (1, 1);
insert into serie_genero (genero_id, serie_id) values (2, 2);

-- Datos para episodio
insert into episodio (duracion, id, serie_id, titulo) values (20, 1, 1, "Episodio 1");
insert into episodio (duracion, id, serie_id, titulo) values (25, 2, 1, "Episodio 2");
insert into episodio (duracion, id, serie_id, titulo) values (30, 3, 2, "Episodio 3");
insert into episodio (duracion, id, serie_id, titulo) values (45, 4, 3, "Episodio 4");

-- Datos para historial
insert into historial (episodio_id, fecha_reproduccion, id, perfil_id) values(1, '2025-02-04 19:26:58.547032', 1, 1);
insert into historial (episodio_id, fecha_reproduccion, id, perfil_id) values(2, '2025-02-04 19:26:58.547032', 2, 1);
insert into historial (episodio_id, fecha_reproduccion, id, perfil_id) values(1, '2025-02-03 22:43:03.029153', 3, 2);
insert into historial (episodio_id, fecha_reproduccion, id, perfil_id) values(2, '2025-02-03 22:43:20.650129', 4, 3);
insert into historial (episodio_id, fecha_reproduccion, id, perfil_id) values(3, '2025-02-03 23:33:49.536446', 5, 3);