# Plan de implementación: Corregir duplicidad en la carga de alimentos

Este plan soluciona el problema por el cual los usuarios deben cargar o confirmar alimentos dos veces al realizar o editar una encuesta.

## Problemas Identificados

1.  **Reinicio de progreso al editar zona**: Al navegar a `MapsFragment` desde una encuesta nueva y regresar, el índice del alimento actual se reinicia a 0 porque el `ViewModel` está asociado al ciclo de vida del `Fragment`.
2.  **Reinicio incorrecto en edición**: Al entrar a editar una encuesta existente, la lógica actual posiciona al usuario en el *último* alimento completado (`size - 1`) en lugar del *siguiente* pendiente (`size`).
3.  **Creación de encuestas duplicadas**: En `NuevaEncuestaFragment`, al rotar la pantalla, se vuelve a ejecutar la lógica de creación si `args.encuestaId` es 0, generando múltiples registros.

## Cambios Propuestos

### ViewModels

#### [MODIFY] [NuevaEncuestaFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/encuesta/NuevaEncuestaFragment.kt)
- Cambiar el alcance de `AlimentoEncuestaViewModel` a nivel de `Activity` (`requireActivity()`).
- Reiniciar el índice a 0 solo si es una encuesta totalmente nueva (`args.encuestaId == 0`).
- Guardar `encuestaId` en el `Bundle` de estado para evitar recreaciones al rotar la pantalla.

#### [MODIFY] [EditarEncuestaFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/encuesta/EditarEncuestaFragment.kt)
- Cambiar el alcance de `AlimentoEncuestaViewModel` a nivel de `Activity`.
- Ajustar la lógica de inicio para posicionar en `alimentosRegistrados.size` (el siguiente alimento a cargar).

### Entidades y DAOs
- No se requieren cambios.

## Verification Plan

### Manual Verification
1.  **Nueva Encuesta**:
    - Iniciar encuesta, cargar 2 alimentos.
    - Click en "Editar Zona", volver.
    - Verificar que continúe en el 3er alimento.
2.  **Edición**:
    - Completar 3 alimentos de una encuesta y salir.
    - Volver a entrar a editar esa encuesta.
    - Verificar que el primer alimento mostrado sea el 4to (no el 3ro de nuevo).
3.  **Rotación**:
    - En una nueva encuesta, rotar la pantalla antes de guardar nada.
    - Verificar que no se creen múltiples encuestas en la base de datos local.
