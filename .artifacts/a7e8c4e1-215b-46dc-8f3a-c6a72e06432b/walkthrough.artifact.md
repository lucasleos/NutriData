# Walkthrough: Eliminación de Sidebar y Reubicación de Acceso a Firebase y Cerrar Sesión

Se ha eliminado completamente el menú lateral (sidebar / Navigation Drawer) de la aplicación y se han reubicado los accesos de "Encuestas subidas a Firebase" y "Cerrar sesión" como tarjetas interactivas en la pantalla de inicio (`InicioFragment`).

## Cambios Realizados

### Interfaz y Navegación Principal

#### [MODIFY] [activity_main.xml](file:///home/lucas/NutriData/app/src/main/res/layout/activity_main.xml)
- Se eliminó el contenedor raíz `DrawerLayout` y el `NavigationView` lateral.
- Se simplificó la estructura a un `LinearLayout` que contiene la `MaterialToolbar` y el `NavHostFragment`.

#### [MODIFY] [MainActivity.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/MainActivity.kt)
- Se removieron todas las referencias a `drawerLayout`, `navView`, bloqueo de drawer por destino y listeners de selección de menú lateral.

---

### Pantalla de Inicio (`InicioFragment`)

#### [MODIFY] [fragment_inicio2.xml](file:///home/lucas/NutriData/app/src/main/res/layout/fragment_inicio2.xml)
- Se agregaron dos nuevas tarjetas (`MaterialCardView`):
  1. **Encuestas Firebase**: Tarjeta azul con icono de nube para acceder a `ListaEncuestasFireBaseActivity`.
  2. **Cerrar sesión**: Tarjeta gris con icono de cierre de sesión para salir de la cuenta de Firebase/Google.

#### [MODIFY] [InicioFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/inicio/InicioFragment.kt)
- Se implementaron los click listeners para las nuevas tarjetas:
  - `cardFirebaseEncuestas`: Lanza `ListaEncuestasFireBaseActivity`.
  - `cardLogout`: Ejecuta la desconexión (`FirebaseAuth.signOut()` y Google Sign-In) y redirige al inicio de sesión (`MainActivity`).

---

## Verificación de Resultados

### Automated Tests
- Compilación exitosa del proyecto con Gradle (`gradle_build("app:assembleDebug")`).
