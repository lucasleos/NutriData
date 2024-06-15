package unpsjb.ing.tntpm2024.basededatos.entidades

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AlimentosEnEncuestas(
    @Embedded val encuesta: Encuesta,
    @Relation(
        parentColumn= "encuestaId",
        entityColumn= "alimentoId",
        associateBy = Junction(AlimentoEncuesta::class)
    )
    val alimentos: List<Alimento>
)
