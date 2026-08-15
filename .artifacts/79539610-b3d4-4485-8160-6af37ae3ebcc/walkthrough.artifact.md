# Corrección de Navegación y Menú Lateral

Se ha ajustado la configuración de la interfaz para que el icono de hamburguesa (menú lateral) aparezca correctamente en las pantallas principales de la aplicación.

## Cambios Realizados

### MainActivity
- **Destinos de Nivel Superior:** Se actualizó `AppBarConfiguration` para incluir a `inicioFragment` (Inicio) y `encuestalist` (Listado de Encuestas) como pantallas principales. Esto fuerza al sistema a mostrar el icono de hamburguesa en lugar de la flecha de "atrás".
- **Bloqueo del Drawer:** Se añadió un listener que bloquea el menú lateral cuando el usuario está en `loginFragment`. Esto evita que se pueda abrir el menú antes de estar autenticado, mejorando la seguridad y flujo de la app.

## Verificación

- **Compilación:** El proyecto compila correctamente (`assembleDebug`).
- **Lógica:** Se aplicaron las constantes de `DrawerLayout` para el bloqueo y se definieron los IDs correctos del grafo de navegación.

> [!IMPORTANT]
> Por favor, despliega la aplicación y verifica que:
> 1. En la pantalla de Login no se pueda abrir el menú.
> 2. Al entrar a la pantalla de Inicio, aparezca el icono de hamburguesa.
> 3. Al navegar a "Listado de Encuestas", también aparezca la hamburguesa.
