package cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager

fun AccountDto.toAccount(): Account {
    return Account(
        id = id,
        campId = campId,
        name = name,
        balance = balance
    )
}