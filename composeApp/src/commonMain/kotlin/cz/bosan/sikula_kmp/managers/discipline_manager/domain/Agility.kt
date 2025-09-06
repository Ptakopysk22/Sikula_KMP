package cz.bosan.sikula_kmp.managers.discipline_manager.domain

import cz.bosan.sikula_kmp.core.domain.SelectableItem

data class AgilityQuest(
    override val id: Int,
    override val name: String
) : SelectableItem

val AgilityQuests = listOf(
    AgilityQuest(id = 0, name = "Všechny"),
    AgilityQuest(id = 1, name = "Předklon"),
    AgilityQuest(id = 2, name = "Záda"),
    AgilityQuest(id = 3, name = "Čelkopták"),
    AgilityQuest(id = 4, name = "Placka"),
    AgilityQuest(id = 5, name = "Pták ohnivák"),
    AgilityQuest(id = 6, name = "Tarzan"),
    AgilityQuest(id = 7, name = "Svis a shyb"),
    AgilityQuest(id = 8, name = "Dřep"),
    AgilityQuest(id = 9, name = "Míč"),
    AgilityQuest(id = 10, name = "Medvídek"),
    AgilityQuest(id = 11, name = "Kliky"),
    AgilityQuest(id = 12, name = "Čekačka"),
    AgilityQuest(id = 13, name = "Mozeček"),
    AgilityQuest(id = 14, name = "Chůdy"),
    AgilityQuest(id = 15, name = "Hula hoop"),
    AgilityQuest(id = 16, name = "Kotoul"),
    AgilityQuest(id = 17, name = "Švihadlo"),
    AgilityQuest(id = 18, name = "Vodník"),
)

fun getAgilityName(id: Int?): String? {
    return AgilityQuests.find { it.id == id }?.name
}