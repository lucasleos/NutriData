# AppTnt - NutriData

NutriData es una aplicación móvil desarrollada con Kotlin y Android Studio diseñada para encuestar los hábitos alimenticios de las personas. La aplicación genera estadísticas con el fin de estimar la ingesta habitual de alimentos, nutrientes y energía de la población argentina.

## Características

- **Encuestas de hábitos alimenticios**: Permite a los usuarios registrar su ingesta diaria de alimentos de manera sencilla y rápida.
- **Generación de estadísticas**: Procesa los datos recolectados para ofrecer estadísticas detalladas sobre el consumo de nutrientes.
- **Autenticación**: Implementa el servicio Firebase Authentication para el inicio de sesión. Validación con Google y direcciones de correo de registradas en la consola de Firebase.
- **Almacenamiento en la Nube**: Utiliza Firebase Realtime Database para almacenar y sincronizar los datos de manera segura y accesible desde cualquier dispositivo.
- **Interfaz intuitiva**: Presenta un diseño sencillo y fácil de usar para mejorar la experiencia del usuario.
- **Integración con Google Maps**: Permite localizar la región del encuestado utilizando mapas de Google.

## Stack Tecnológico

- **Lenguaje**: Kotlin
- **Base de datos local**: SQLite
- **ORM**: Room
- **Base de datos Cloud**: Firebase Realtime Database para el almacenamiento en la nube
- **Autenticación**: Firebase Authentication
- **APIs**: Google Maps API para la geolocalización

## Requisitos del Sistema

- Android Studio Arctic Fox o superior
- SDK de Android 21 o superior
- Kotlin 1.5.21 o superior
