-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 27-05-2023 a las 15:26:52
-- Versión del servidor: 10.4.27-MariaDB
-- Versión de PHP: 8.1.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `tiempoextrabd`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `noticia`
--

CREATE TABLE `noticia` (
  `id` int(11) NOT NULL,
  `contenido` text DEFAULT NULL,
  `fechaPublicacion` datetime DEFAULT NULL,
  `autor_id` int(11) DEFAULT NULL,
  `categoria` enum('FUTBOL_NACIONAL','FUTBOL_INTERNACIONAL','BALONCESTO_NACIONAL','BALONCESTO_INTERNACIONAL','UFC') DEFAULT NULL,
  `noticiaPremium` tinyint(1) DEFAULT NULL,
  `elementoConNombre_nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `noticia`
--

INSERT INTO `noticia` (`id`, `contenido`, `fechaPublicacion`, `autor_id`, `categoria`, `noticiaPremium`, `elementoConNombre_nombre`) VALUES
(33, 'Esto es la primera noticia que se guardara en una base de datos', '2023-05-25 18:48:14', 7, 'FUTBOL_NACIONAL', 0, 'Primera noticia'),
(34, 'daksdklasfjkldssdaf', '2023-05-25 18:57:09', 7, 'FUTBOL_NACIONAL', 0, 'aasdsad'),
(35, 'dsfsdfdsfsd', '2023-05-25 18:57:17', 7, 'FUTBOL_NACIONAL', 1, 'asdafsdfg'),
(36, 'dsadasdasdasdas', '2023-05-25 18:57:35', 7, 'FUTBOL_NACIONAL', 1, 'asdasdasdsa'),
(38, 'javi', '2023-05-26 14:56:29', 7, 'BALONCESTO_NACIONAL', 1, 'javi'),
(39, 'akjsdfhasdfdlsfajakdsfdfasfdsafdsa', '2023-05-26 15:27:49', 7, 'FUTBOL_NACIONAL', 0, '1ºNoticia creada con logs'),
(40, 'mamasdklasfhkdsfkdlfdsl.ghjdfsgdg', '2023-05-26 16:35:29', 7, 'BALONCESTO_INTERNACIONAL', 1, 'Prueba mama'),
(41, 'esto es un ejemplo pal log para ver que escribe bien', '2023-05-26 19:38:02', 7, 'FUTBOL_NACIONAL', 0, 'Ejemplo pal log editado'),
(43, 'manue', '2023-05-26 23:16:03', 7, 'FUTBOL_NACIONAL', 1, 'Manue');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `suscripcion`
--

CREATE TABLE `suscripcion` (
  `id` int(11) NOT NULL,
  `precioPorMes` float DEFAULT NULL,
  `categoria` enum('FUTBOL_NACIONAL','FUTBOL_INTERNACIONAL','BALONCESTO_NACIONAL','BALONCESTO_INTERNACIONAL','UFC') DEFAULT NULL,
  `fechaInicio` date DEFAULT NULL,
  `fechaFin` date DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `usuario_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `suscripcion`
--

INSERT INTO `suscripcion` (`id`, `precioPorMes`, `categoria`, `fechaInicio`, `fechaFin`, `nombre`, `usuario_id`) VALUES
(4, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(5, 10, 'BALONCESTO_NACIONAL', '2023-05-27', '2024-05-27', 'Suscripción Anual', 14),
(6, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(7, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(8, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(9, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(10, 10, 'FUTBOL_INTERNACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(11, 10, 'FUTBOL_INTERNACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(12, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(13, 10, 'FUTBOL_NACIONAL', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14),
(14, 10, 'UFC', '2023-05-27', '2023-06-27', 'Suscripción Mensual', 14);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `nombreUsuario` varchar(255) DEFAULT NULL,
  `contraseña` varchar(255) DEFAULT NULL,
  `isEditor` tinyint(1) DEFAULT NULL,
  `isAdmin` tinyint(1) DEFAULT NULL,
  `elementoConNombre_nombre` varchar(50) DEFAULT NULL,
  `isPremium` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `nombreUsuario`, `contraseña`, `isEditor`, `isAdmin`, `elementoConNombre_nombre`, `isPremium`) VALUES
(1, 'test', 'test', 0, 0, NULL, 0),
(7, 'Editor', 'Editor', 1, 0, NULL, 0),
(10, 'Admin', 'admin', 0, 1, NULL, 0),
(14, 'Maxi', '123', 0, 0, NULL, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `noticia`
--
ALTER TABLE `noticia`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `suscripcion`
--
ALTER TABLE `suscripcion`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_suscripcion_usuario` (`usuario_id`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `noticia`
--
ALTER TABLE `noticia`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT de la tabla `suscripcion`
--
ALTER TABLE `suscripcion`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `suscripcion`
--
ALTER TABLE `suscripcion`
  ADD CONSTRAINT `fk_suscripcion_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
