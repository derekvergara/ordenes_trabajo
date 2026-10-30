-- phpMyAdmin SQL Dump
-- version 4.8.4
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 30-10-2026 a las 01:58:55
-- Versión del servidor: 10.1.37-MariaDB
-- Versión de PHP: 7.3.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `bd_ordenes`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `auditoria`
--

CREATE TABLE `auditoria` (
  `id` bigint(20) NOT NULL,
  `accion` enum('ACTUALIZAR','CREAR','ELIMINAR') NOT NULL,
  `cambios` text,
  `entidad` enum('ORDEN','USUARIO') NOT NULL,
  `entidad_id` bigint(20) NOT NULL,
  `fecha` datetime(6) NOT NULL,
  `usuario` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `auditoria`
--

INSERT INTO `auditoria` (`id`, `accion`, `cambios`, `entidad`, `entidad_id`, `fecha`, `usuario`) VALUES
(1, 'CREAR', 'Orden creada - cliente: \"Adoni\", equipo: \"Cpu Gamer \"', 'ORDEN', 3, '2026-10-22 22:21:17.000000', 'derek@gmail.com'),
(2, 'ACTUALIZAR', 'cliente: \"Adoni\" -> \"Adonis cul\"; danio: \"Mantenimiento general \" -> \"Mantenimiento general + Cambio de fuente de poder \"; estado: \"INGRESADO\" -> \"EN_REVISION\"; repuestos: \"\" -> \"Fuente corsair 80 plus bronce certified\";', 'ORDEN', 3, '2026-10-22 22:22:33.000000', 'derek@gmail.com'),
(3, 'CREAR', 'Orden creada - cliente: \"Kevin Morocho\", equipo: \"Laptop Predator\"', 'ORDEN', 4, '2026-10-22 22:24:05.000000', 'kevin@gmail.com'),
(4, 'ACTUALIZAR', 'costoValor: \"0.0\" -> \"25.0\"; repuestos: \"\" -> \"Cambio de teclado \";', 'ORDEN', 4, '2026-10-22 22:24:59.000000', 'kevin@gmail.com'),
(5, 'ACTUALIZAR', 'rol: \"USER\" -> \"ADMIN\"; password: cambiada;', 'USUARIO', 4, '2026-10-28 19:33:01.000000', 'derek@gmail.com'),
(6, 'ACTUALIZAR', 'activo: \"true\" -> \"false\";', 'USUARIO', 4, '2026-10-28 19:36:06.000000', 'derek@gmail.com'),
(7, 'ACTUALIZAR', 'activo: \"false\" -> \"true\";', 'USUARIO', 4, '2026-10-28 19:36:29.000000', 'derek@gmail.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ordenes`
--

CREATE TABLE `ordenes` (
  `id` bigint(20) NOT NULL,
  `cliente` varchar(255) NOT NULL,
  `costo_valor` double NOT NULL,
  `danio` varchar(255) NOT NULL,
  `equipo` varchar(255) NOT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_ingreso` date NOT NULL,
  `fecha_salida` date DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `observaciones` varchar(500) DEFAULT NULL,
  `repuestos` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `ordenes`
--

INSERT INTO `ordenes` (`id`, `cliente`, `costo_valor`, `danio`, `equipo`, `estado`, `fecha_ingreso`, `fecha_salida`, `is_active`, `observaciones`, `repuestos`, `telefono`) VALUES
(1, 'Andy Bustos Panda', 60, 'pantalla rota mucho', 'Laptop Acer', 'ENTREGADO', '2026-09-01', '2026-10-22', b'1', 'Deja con cargador con mochila', 'Pantalla mas bateria', '0123456789'),
(2, 'Kevin carabajo', 0, 'Visagras', 'Laptop Predator', 'EN_REVISION', '2026-10-23', NULL, b'0', '', '', '0987654321'),
(3, 'Adonis cul', 0, 'Mantenimiento general + Cambio de fuente de poder ', 'Cpu Gamer ', 'EN_REVISION', '2026-10-22', NULL, b'1', '', 'Fuente corsair 80 plus bronce certified', '0987456321'),
(4, 'Kevin Morocho', 25, 'No Reconoce las teclas', 'Laptop Predator', 'INGRESADO', '2026-10-22', NULL, b'1', '', 'Cambio de teclado ', '0123456789');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `nombre`) VALUES
(1, 'ADMIN'),
(2, 'USER');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `correo`, `is_active`, `nombre`, `password`, `rol_id`) VALUES
(1, 'derek@gmail.com', b'1', 'Derek', '$2a$10$39bwcwV799qBmxMjz.2zauPJATIySd2iFIzF0Hf74.cEpjP51mwXi', 1),
(2, 'kevin@gmail.com', b'1', 'Kevin Carabajo ', '$2a$10$7vkmdet9Sg5hfw2QdSV1COr2rDYWoUBZ802j2t4tWOnS3NHiiv/0e', 2),
(3, 'Andres@gmail.com', b'1', 'Andres', '$2a$10$AYIT4paGHhP7pQmX4XtcFOzhH79xZqEK3ZaOoI0aH820P4DATZ0uS', 2),
(4, 'juan@gmail.com', b'1', 'Juan', '$2a$10$SSKoiPb/tu4zjKT2..XFse41vkGGrFIS2xchKKcb.noEReLimwsqu', 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `auditoria`
--
ALTER TABLE `auditoria`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `ordenes`
--
ALTER TABLE `ordenes`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKldv0v52e0udsh2h1rs0r0gw1n` (`nombre`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKqf5elo4jcq7qrt83oi0qmenjo` (`rol_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `auditoria`
--
ALTER TABLE `auditoria`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `ordenes`
--
ALTER TABLE `ordenes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `FKqf5elo4jcq7qrt83oi0qmenjo` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
