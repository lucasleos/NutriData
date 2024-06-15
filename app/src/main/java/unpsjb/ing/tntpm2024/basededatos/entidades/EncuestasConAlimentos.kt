package unpsjb.ing.tntpm2024.basededatos.entidades

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EncuestasConAlimentos(
    @Embedded val alimento: Alimento,
    @Relation(
        parentColumn= "alimentoId",
        entityColumn= "encuestaId",
        associateBy = Junction(AlimentoEncuesta::class)
    )
    val encuestas: List<Encuesta>
)
