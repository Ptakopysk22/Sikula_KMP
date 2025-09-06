package cz.bosan.sikula_kmp.managers.children_manager.data

import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.children_manager.domain.SelectableChild

fun ChildDto.toChild(): Child {
    return Child(
        id = id,
        name = name,
        nickName = nickName,
        birthDate = birthDate,
        role = occupations[0].role,
        isActive = occupations[0].isActive,
        crewId = occupations[0].crewId,
        groupId = occupations[0].groupId,
        trailCategoryId = occupations[0].trailCategoryId
    )
}

fun LightChild.toChild(): Child {
    return Child(
        id = id,
        name = "",
        nickName = nickname,
        birthDate = null,
        role = null,
        isActive = true,
        groupId = null,
        crewId = null,
        trailCategoryId = trailCategoryId.takeIf { it != -1 }
    )
}

fun Child.toChildEntity(campId: Int): ChildEntity {
    return ChildEntity(
        id = id,
        campId = campId,
        name = name,
        nickName = nickName,
        birthDate = birthDate,
        role = role,
        isActive = isActive,
        groupId = groupId,
        crewId = crewId,
        trailCategoryId = trailCategoryId
    )
}

fun ChildEntity.toChild(): Child {
    return Child(
        id = id,
        name = name,
        nickName = nickName,
        birthDate = birthDate,
        role = role,
        isActive = isActive,
        groupId = groupId,
        crewId = crewId,
        trailCategoryId = trailCategoryId
    )
}

fun Child.toSelectableChild(): SelectableChild {
    return SelectableChild(
        id = id,
        name = nickName,
    )
}