# Implementación de imágenes de alimentos en la encuesta

Se ha completado la tarea de mostrar imágenes ilustrativas de los alimentos durante la realización de la encuesta.

## Cambios realizados

### 1. Modelo de Datos y Base de Datos
- Se añadió el campo `imagenNombre` a la entidad `Alimento` ([Alimento.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/basededatos/entidades/Alimento.kt)).
- Se incrementó la versión de la base de datos a **4** en [EncuestasDatabase.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/basededatos/EncuestasDatabase.kt).
- Se actualizaron los datos iniciales con nombres de recursos de referencia (ej. `"banana"`, `"huevo"`, `"leche_polvo"`).

### 2. Interfaz de Usuario
- Se agregó un `ImageView` con ID `ivAlimento` en los layouts de encuesta:
    - [fragment_nueva_encuesta.xml](file:///home/lucas/NutriData/app/src/main/res/layout/fragment_nueva_encuesta.xml)
    - [fragment_editar_encuesta.xml](file:///home/lucas/NutriData/app/src/main/res/layout/fragment_editar_encuesta.xml)
- La imagen se posiciona justo debajo del nombre del alimento para una identificación rápida.

### 3. Lógica de Carga Dinámica
- Se implementó la función `cargarImagenAlimento` en:
    - [NuevaEncuestaFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/encuesta/NuevaEncuestaFragment.kt)
    - [EditarEncuestaFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/encuesta/EditarEncuestaFragment.kt)
- Esta función busca el recurso en la carpeta `drawable` usando el nombre almacenado. Si no lo encuentra, muestra un icono por defecto (`ic_food_logo`).

## Próximos Pasos para el Usuario

> [!IMPORTANT]
> Para que las imágenes se visualicen, debes agregar los archivos de imagen a la carpeta `res/drawable` con los nombres asignados en la base de datos.

Por ejemplo, si un alimento tiene el nombre de imagen `"banana"`, debes tener un archivo llamado `banana.png`, `banana.jpg`, etc., en tus recursos.

Si deseas cambiar el nombre de la imagen para un alimento específico, deberás actualizar el campo `imagen_nombre` en la tabla `tabla_alimento` de la base de datos.
