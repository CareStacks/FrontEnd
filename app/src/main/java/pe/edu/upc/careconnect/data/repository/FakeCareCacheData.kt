package pe.edu.upc.careconnect.data.repository

import pe.edu.upc.careconnect.data.local.CachedDiaryNoteEntity
import pe.edu.upc.careconnect.data.local.CachedDocumentEntity
import pe.edu.upc.careconnect.data.local.CachedNotificationEntity

internal object FakeCareCacheData {
    val documents = listOf(
        CachedDocumentEntity(
            id = "doc-analytics-blood",
            title = "Analítica de Sangre",
            type = "PDF",
            date = "12 Oct 2023",
            section = "RECIENTES",
            tone = "purple",
            description = "Resultados generales de laboratorio",
            sortOrder = 10
        ),
        CachedDocumentEntity(
            id = "doc-insulin-prescription",
            title = "Receta Insulina",
            type = "Digital",
            date = "08 Oct 2023",
            section = "RECIENTES",
            tone = "green",
            description = "Receta activa para tratamiento diario",
            sortOrder = 20
        ),
        CachedDocumentEntity(
            id = "doc-hip-xray",
            title = "Radiografía Cadera",
            type = "Imagen",
            date = "25 Sep 2023",
            section = "RECIENTES",
            tone = "orange",
            description = "Imagen médica de seguimiento",
            sortOrder = 30
        ),
        CachedDocumentEntity(
            id = "doc-cardiology-report",
            title = "Informe Cardiología",
            type = "DOCX",
            date = "14 Jul 2023",
            section = "HISTORIAL ANUAL",
            tone = "purple",
            description = "Informe del control anual",
            sortOrder = 40
        )
    )

    val diaryNotes = listOf(
        CachedDiaryNoteEntity(
            id = "note-today-elena",
            author = "Elena Martínez",
            timestamp = "HOY, 09:30 AM",
            body = "Don Manuel ha pasado una noche tranquila. Desayunó con apetito " +
                "(avena y fruta) y se muestra muy comunicativo esta mañana. Hemos " +
                "realizado los ejercicios de movilidad suave sin molestias.",
            tags = "Estado: Estable|Actividad Física",
            tone = "green",
            isHighlighted = true,
            sortOrder = 10
        ),
        CachedDiaryNoteEntity(
            id = "note-doctor-julian",
            author = "Dr. Julián Rivas",
            timestamp = "AYER, 18:15 PM",
            body = "Revisión semanal completada. La tensión arterial se mantiene en " +
                "rangos normales. Se recomienda mantener la hidratación y continuar " +
                "con el paseo diario de 15 minutos.",
            tags = "Médico",
            tone = "medical",
            isHighlighted = false,
            sortOrder = 20
        ),
        CachedDiaryNoteEntity(
            id = "note-elena-incident",
            author = "Elena Martínez",
            timestamp = "24 OCT, 14:00 PM",
            body = "Ligera desorientación tras la siesta. He administrado la medicación " +
                "pautada y se ha calmado escuchando su música favorita. No ha habido fiebre.",
            tags = "Incidencia leve",
            tone = "error",
            isHighlighted = false,
            sortOrder = 30
        ),
        CachedDiaryNoteEntity(
            id = "note-carlos-family",
            author = "Carlos (Hijo)",
            timestamp = "23 OCT, 20:30 PM",
            body = "He pasado la tarde con papá. Estuvimos viendo fotos antiguas y " +
                "recordaba muchos detalles de la infancia. Cenó muy bien.",
            tags = "",
            tone = "neutral",
            isHighlighted = false,
            sortOrder = 40
        )
    )

    val notifications = listOf(
        CachedNotificationEntity(
            id = "notification-medication",
            title = "Recordatorio de medicación",
            message = "Es hora de administrar la dosis de Enalapril (10mg) a la Sra. Elena.",
            status = "Pendiente",
            statusTone = "warning",
            actionLabel = "Ver detalles",
            timestamp = "Hoy, 09:00 AM",
            iconTone = "primary",
            sortOrder = 10
        ),
        CachedNotificationEntity(
            id = "notification-missed-vitals",
            title = "Alerta de incumplimiento",
            message = "Se ha detectado una omisión en el registro de signos vitales del turno de mañana.",
            status = "Urgente",
            statusTone = "error",
            actionLabel = "Resolver",
            timestamp = "Hoy, 08:30 AM",
            iconTone = "error",
            sortOrder = 20
        ),
        CachedNotificationEntity(
            id = "notification-document",
            title = "Documento actualizado",
            message = "El Dr. Martínez ha subido el nuevo informe de cardiología.",
            status = "Leído",
            statusTone = "read",
            actionLabel = "Descargar",
            timestamp = "Ayer, 18:45 PM",
            iconTone = "green",
            sortOrder = 30
        ),
        CachedNotificationEntity(
            id = "notification-diary",
            title = "Nota de diario compartida",
            message = "Marta añadió una nota sobre el estado de ánimo positivo durante el almuerzo.",
            status = "",
            statusTone = "neutral",
            actionLabel = "Leer nota",
            timestamp = "Ayer, 14:20 PM",
            iconTone = "purple",
            sortOrder = 40
        )
    )
}
