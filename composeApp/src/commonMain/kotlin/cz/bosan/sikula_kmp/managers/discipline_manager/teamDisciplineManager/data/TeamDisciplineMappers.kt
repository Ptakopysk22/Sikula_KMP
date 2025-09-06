package cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.ImprovementsAndRecords
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.domain.TeamDisciplineRecord

fun TeamRecordEntity.toTeamDisciplineRecord(): TeamDisciplineRecord {
    return TeamDisciplineRecord(
        id = id!!.toInt(),
        crewId = crewId,
        campDay = campDay,
        value = value,
        timeStamp = timeStamp,
        refereeId = refereeId,
        comment = comment,
        improvementsAndRecords = ImprovementsAndRecords(
            countsForImprovements = countsForImprovement,
            improvementString = improvement,
            isRecord = isRecord
        ),
        isUploaded = isUploaded
    )
}

fun TeamDisciplineRecordDto.toTeamDisciplineRecord(): TeamDisciplineRecord {
    return TeamDisciplineRecord(
        id = id,
        crewId = crewId,
        campDay = campDay,
        value = value?.toString(),
        timeStamp = timeStamp,
        refereeId = refereeId,
        comment = comment,
        improvementsAndRecords = ImprovementsAndRecords(
            countsForImprovements = improvementsAndRecords.countsForImprovements,
            improvementString = improvementsAndRecords.improvementString,
            isRecord = improvementsAndRecords.isRecord
        ),
        isUploaded = true
    )
}

fun TeamDisciplineRecord.toNewTeamDisciplineRecordDto(campId: Int): NewTeamDisciplineRecordDto {
    return NewTeamDisciplineRecordDto(
        crewId = crewId,
        campDay = campDay,
        campId = campId,
        value = value?.toInt(),
        timeStamp = timeStamp,
        refereeId = refereeId,
        countsForImprovements = improvementsAndRecords?.countsForImprovements,
        comment = comment
    )
}

fun TeamDisciplineRecord.toTeamRecordEntity(
    campId: Int,
    discipline: Discipline,
    idOnServer: Int?,
): TeamRecordEntity {
    return TeamRecordEntity(
        id = id?.toLong(),
        idOnServer = idOnServer,
        campId = campId,
        disciplineId = discipline.getId().toInt(),
        crewId = crewId,
        value = value,
        campDay = campDay,
        timeStamp = timeStamp,
        isUploaded = isUploaded ?: false,
        refereeId = refereeId,
        comment = comment,
        countsForImprovement = improvementsAndRecords?.countsForImprovements,
        improvement = improvementsAndRecords?.improvementString,
        isRecord = improvementsAndRecords?.isRecord
    )
}
