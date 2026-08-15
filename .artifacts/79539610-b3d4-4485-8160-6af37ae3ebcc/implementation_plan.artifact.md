# Corregir Icono de Hamburguesa y Navegación

El problema se debe a que el `AppBarConfiguration` solo reconoce al `loginFragment` como destino de nivel superior (por ser el destino inicial del grafo). Al navegar hacia `inicioFragment`, el sistema muestra una flecha de retroceso.

## Cambios Propuestos

### MainActivity

#### [MODIFY] [MainActivity.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/MainActivity.kt)
- Configurar `AppBarConfiguration` para incluir `inicioFragment` y `encuestalist` como destinos de nivel superior. Esto hará que muestren el icono de hamburguesa en lugar de la flecha de atrás.
- Implementar un `OnDestinationChangedListener` para bloquear el menú lateral (`DrawerLayout`) cuando el usuario se encuentra en la pantalla de login, evitando que se pueda abrir antes de autenticarse.

## Plan de Verificación

### Pruebas Manuales
1. Iniciar la aplicación: Verificar que en la pantalla de Login no se muestre la flecha de atrás (o que el menú esté bloqueado si aparece la hamburguesa).
2. Iniciar sesión: Al llegar a la pantalla de Inicio, verificar que aparezca el icono de hamburguesa y que el menú lateral funcione.
3. Navegar a "Listado de Encuestas": Verificar que también muestre el icono de hamburguesa.
4. Navegar a una pantalla de detalle: Verificar que muestre la flecha de atrás correctamente.
