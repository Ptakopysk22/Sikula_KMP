package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.domain.IndividualDisciplineRecord

fun IndividualDisciplineRecordDto.toIndividualDisciplineRecord(discipline: Discipline): IndividualDisciplineRecord {
    return IndividualDisciplineRecord(
        id = id,
        competitorId = competitorId,
        campDay = campDay,
        value = if (discipline == Discipline.Individual.MORSE) value?.toString() else value?.toInt()?.toString(),
        timeStamp = timeStamp,
        refereeId = refereeId,
        comment = comment,
        countsForImprovement = improvementsAndRecords.countsForImprovements,
        improvement = improvementsAndRecords.improvementString,
        isRecord = improvementsAndRecords.isRecord,
        quest = quest,
        workedOff = workedOff,
        isUploaded = true
    )
}

fun IndividualDisciplineRecord.toNewIndividualDisciplineRecordDto(campId: Int): NewIndividualDisciplineRecordDto {
    return NewIndividualDisciplineRecordDto(
        competitorId = competitorId,
        campDay = campDay,
        campId = campId,
        value = if(value == "") null else value?.toDouble(),
        timeStamp = timeStamp,
        refereeId = refereeId,
        countsForImprovements = countsForImprovement,
        comment = comment,
        quest = quest
    )
}

fun IndividualRecordEntity.toIndividualRecord(): IndividualDisciplineRecord {
    return IndividualDisciplineRecord(
        id = id!!.toInt(),
        competitorId = competitorId,
        campDay = campDay,
        value = value,
        timeStamp = timeStamp,
        refereeId = refereeId,
        comment = comment,
        countsForImprovement = countsForImprovement,
        improvement = improvement,
        isRecord = isRecord,
        isUploaded = isUploaded
    )
}

fun IndividualDisciplineRecord.toIndividualRecordEntity(
    campId: Int,
    discipline: Discipline,
    idOnServer: Int?,
): IndividualRecordEntity {
    return IndividualRecordEntity(
        id = id?.toLong(),
        idOnServer = idOnServer,
        campId = campId,
        disciplineId = discipline.getId().toInt(),
        competitorId = competitorId,
        value = value,
        campDay = campDay,
        timeStamp = timeStamp,
        isUploaded = isUploaded ?: false,
        refereeId = refereeId,
        comment = comment,
        countsForImprovement = countsForImprovement,
        improvement = improvement,
        isRecord = isRecord
    )
}