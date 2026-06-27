# NutriData

[![Kotlin](https://img.shields.io/badge/Kotlin-1.5.21-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-5.0+-green.svg)](https://developer.android.com/)
[![Firebase](https://img.shields.io/badge/Firebase-Integration-orange.svg)](https://firebase.google.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**NutriData** es una aplicación móvil desarrollada en **Kotlin** con **Android Studio** para realizar encuestas de hábitos alimenticios en la población argentina. Permite registrar el consumo diario de alimentos, gestionar encuestas (abandonar, reabrir y finalizar), recolectar la zona de domicilio de los voluntarios mediante geolocalización, y generar estadísticas detalladas de nutrientes. Los datos se almacenan localmente con **Room** y se sincronizan con **Firebase Realtime Database**, mientras que la autenticación se gestiona con **Firebase Authentication** (correo electrónico y Google).

---

## Características

- **Registro completo de alimentos**  
  El usuario puede registrar el consumo de cualquier alimento de la tabla de la encuesta, indicando cantidad y frecuencia.

- **Gestión flexible de encuestas**  
  - Iniciar una nueva encuesta.  
  - Abandonar una encuesta en curso (los datos se guardan localmente).  
  - Reabrir una encuesta abandonada y continuar desde donde se dejó.  
  - Finalizar la encuesta, lo que la marca como completada y la incluye en los cálculos estadísticos.

- **Autenticación segura**  
  - Inicio de sesión con **Google** o con correo electrónico.  
  - Solo direcciones de correo previamente registradas en la consola de Firebase pueden acceder (control de acceso por lista blanca).

- **Almacenamiento local y en la nube**  
  - **SQLite + Room** para persistencia local de encuestas y datos del usuario.  
  - **Firebase Realtime Database** para sincronizar datos en la nube, garantizando disponibilidad y acceso desde múltiples dispositivos.

- **Geolocalización y mapas**  
  - Recolecta la zona (provincia/ciudad) del domicilio de cada voluntario mediante **Google Maps API**.  
  - Muestra en un mapa la ubicación aproximada de todos los voluntarios registrados, permitiendo visualizar la distribución geográfica de los participantes.

- **Estadísticas nutricionales**  
  Calcula y muestra el **consumo promedio** a partir de encuestas finalizadas para:
  - Kcal totales  
  - Carbohidratos  
  - Proteínas  
  - Colesterol  
  - Fibras

- **Envío a base de datos centralizada**  
  Permite seleccionar encuestas finalizadas específicas y enviarlas a la base de datos centralizada (Firebase Realtime Database), facilitando la recopilación de datos para estudios poblacionales.

- **Interfaz intuitiva**  
  Diseño limpio y fácil de usar, optimizado para teléfonos y tablets.

---

## Stack Tecnológico

| Componente            | Tecnología                          |
|-----------------------|-------------------------------------|
| **Lenguaje**          | Kotlin                              |
| **IDE**               | Android Studio (Arctic Fox o superior) |
| **Base de datos local** | SQLite con ORM **Room**             |
| **Base de datos Cloud** | Firebase Realtime Database          |
| **Autenticación**     | Firebase Authentication (correo + Google) |
| **Geolocalización**   | Google Maps API                     |
| **Arquitectura**      | MVVM (Model-View-ViewModel)         |
| **Librerías**         | Retrofit, Coroutines, LiveData, ViewModel |

---

## Requisitos del Sistema

- **Android Studio** Arctic Fox (2020.3.1) o superior.
- **SDK de Android** mínimo 21 (Android 5.0 Lollipop).
- **Kotlin** 1.5.21 o superior.
- **Google Play Services** actualizado para Maps y Authentication.
- **Conexión a Internet** (para autenticación, sincronización y mapas).
