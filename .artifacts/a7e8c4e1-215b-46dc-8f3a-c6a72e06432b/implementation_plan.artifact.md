# Plan: Eliminar sidebar y mover accesos de Encuestas Firebase y Cerrar Sesión al Inicio

Eliminar el menú lateral (sidebar / Navigation Drawer) de la aplicación y reubicar los botones de "Encuestas subidas a Firebase" y "Cerrar sesión" en la pantalla de Inicio (`InicioFragment`).

## User Review Required

> [!IMPORTANT]
> - Se eliminará completamente el componente `DrawerLayout` y `NavigationView` de la aplicación (`MainActivity` y `activity_main.xml`).
> - Se agregarán dos nuevas tarjetas (cards) en la pantalla de inicio (`fragment_inicio2.xml` y `InicioFragment.kt`):
>   1. **Encuestas subidas a Firebase** (abre `ListaEncuestasFireBaseActivity`).
>   2. **Cerrar sesión** (ejecuta el cierre de sesión de Firebase y Google Sign-In, regresando al `LoginFragment`/`MainActivity`).

## Open Questions

- Ninguna. Los requerimientos son claros y directos.

## Proposed Changes

### Componente Principal (UI y Navegación)

#### [MODIFY] [activity_main.xml](file:///home/lucas/NutriData/app/src/main/res/layout/activity_main.xml)
- Remover `androidx.drawerlayout.widget.DrawerLayout` y `com.google.android.material.navigation.NavigationView`.
- Ajustar la estructura raíz para contener la `MaterialToolbar` y el `NavHostFragment` directamente en un `LinearLayout` (o `ConstraintLayout`).

#### [MODIFY] [MainActivity.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/MainActivity.kt)
- Eliminar referencias a `drawerLayout`, `navView`, `appBarConfiguration` con drawer, `drawerLockMode`, `setNavigationItemSelectedListener`, y `actualizarVisibilidadMenu()`.
- Mantener la configuración del `NavController`, `MaterialToolbar`, y la ocultación de barra en destinos sin toolbar.

#### [MODIFY] [fragment_inicio2.xml](file:///home/lucas/NutriData/app/src/main/res/layout/fragment_inicio2.xml)
- Agregar dos `MaterialCardView` adicionales dentro del ScrollView para:
  - Encuestas subidas a Firebase (`cardFirebaseEncuestas`).
  - Cerrar sesión (`cardLogout`).

#### [MODIFY] [InicioFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/inicio/InicioFragment.kt)
- Añadir listeners para los nuevos botones en `configurarBotones()`:
  - `cardFirebaseEncuestas`: Inicia `ListaEncuestasFireBaseActivity`.
  - `cardLogout`: Ejecuta la lógica de cierre de sesión (`FirebaseAuth` + Google Sign-In y reinicio de actividad).

## Verification Plan

### Automated Tests
- Compilación del proyecto (`gradle_build("app:assembleDebug")`).

### Manual Verification
- Verificar que la aplicación inicia sin sidebar / Navigation Drawer (sin icono hamburguesa de drawer).
- Verificar que en la pantalla de Inicio (`InicioFragment`) aparecen las tarjetas de "Encuestas subidas a Firebase" y "Cerrar sesión".
- Probar que al hacer clic en "Encuestas subidas a Firebase" se abre correctamente `ListaEncuestasFireBaseActivity`.
- Probar que al hacer clic en "Cerrar sesión" se cierra sesión y se redirige al login.
