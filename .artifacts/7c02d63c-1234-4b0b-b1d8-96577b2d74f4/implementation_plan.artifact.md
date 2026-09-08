# Map of Survey Origins in Statistics Screen

Implement an interactive Google Map view in the statistics screen (`EstadisticaFragment` / `fragment_estadistica.xml`) that visualizes where surveys come from. For each neighborhood/zone with completed surveys, display a marker showing the count of surveys (e.g., "3" for 3 surveys in that neighborhood).

## User Review Required

> [!IMPORTANT]
> - We will embed a `SupportMapFragment` (or `MapView`) inside the statistics ScrollView or as a dedicated section in `fragment_estadistica.xml`.
> - Surveys contain a `zona` field matching neighborhood names from `barrios_madryn.geojson`. We will load all surveys from the database, group them by `zona`, count how many surveys belong to each neighborhood, and display a marker with the count at the center of each neighborhood polygon.

## Open Questions

- Should tapping a neighborhood marker or polygon show a popup with survey details or count? (We can display a marker with the count and title/snippet).

## Proposed Changes

### Database / ViewModel Layer

#### [MODIFY] [EstadisticaViewModel.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/estadistica/EstadisticaViewModel.kt)
- Add observation of all surveys (`repository.allEncuestas` or `EncuestaDAO` query) to compute survey counts per zone/neighborhood (`Map<String, Int>`).

### UI / Layout Layer

#### [MODIFY] [fragment_estadistica.xml](file:///home/lucas/NutriData/app/src/main/res/layout/fragment_estadistica.xml)
- Add a CardView containing a container/fragment for the map (`androidx.fragment.app.FragmentContainerView` or `MapView` for Google Maps) to display survey origins by zone.

#### [MODIFY] [EstadisticaFragment.kt](file:///home/lucas/NutriData/app/src/main/java/unpsjb/ing/tntpm2024/estadistica/EstadisticaFragment.kt)
- Initialize the Google Map in `EstadisticaFragment`.
- Load `barrios_madryn.geojson` from assets (similar to `MapsFragment`).
- Observe survey counts per zone from `EstadisticaViewModel`.
- Draw neighborhood polygons and place custom markers or title markers showing the count (e.g. "Barrio X: 3 encuestas") at the center of each neighborhood polygon that has surveys.

## Verification Plan

### Automated Tests
- Run unit tests (`app:testDebugUnitTest`) to ensure viewmodel logic compiles and passes.

### Manual Verification
- Deploy to emulator/device, navigate to statistics screen, and verify that the map displays neighborhood polygons and survey counts correctly.
