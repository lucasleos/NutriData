# Walkthrough - Map of Survey Origins in Statistics Screen

Implemented an interactive Google Map on the statistics screen (`EstadisticaFragment` / `fragment_estadistica.xml`) to visualize where surveys originate by neighborhood/zone, displaying the exact count of surveys per neighborhood (e.g., "3").

## Changes

### Database / ViewModel Layer

#### [EstadisticaViewModel.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/estadistica/EstadisticaViewModel.kt)
- Added `zoneCounts` LiveData observing all surveys and grouping them by `zona` with their respective counts.

### UI / Layout Layer

#### [fragment_estadistica.xml](file:///home/lucas/NutriData/app/src/main/res/layout/fragment_estadistica.xml)
- Added a new CardView containing a `FragmentContainerView` (`SupportMapFragment`) titled "Origen de Encuestas por Zona".

#### [EstadisticaFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/estadistica/EstadisticaFragment.kt)
- Integrated Google Maps (`OnMapReadyCallback`, `SupportMapFragment`).
- Loaded neighborhood polygons from `barrios_madryn.geojson`.
- Observed `viewModel.zoneCounts` and placed markers showing the survey count (e.g., "3") at the center of each neighborhood polygon where surveys have been completed.

## Verification Results

### Automated Tests
- Executed `app:assembleDebug` -> **Success**
- Executed `app:testDebugUnitTest` -> **1 passed, 0 failed**
