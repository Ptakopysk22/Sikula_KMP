package cz.bosan.sikula_kmp.core.presentation

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Info
import cz.bosan.sikula_kmp.core.domain.Warning
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_nonexistent_child
import sikula_kmp.composeapp.generated.resources.description_nonexistent_leader
import sikula_kmp.composeapp.generated.resources.description_nonexistent_leader_email
import sikula_kmp.composeapp.generated.resources.error_bad_request
import sikula_kmp.composeapp.generated.resources.error_disk_full
import sikula_kmp.composeapp.generated.resources.error_forbidden
import sikula_kmp.composeapp.generated.resources.error_no_internet
import sikula_kmp.composeapp.generated.resources.error_not_found
import sikula_kmp.composeapp.generated.resources.error_request_timeout
import sikula_kmp.composeapp.generated.resources.error_serialization
import sikula_kmp.composeapp.generated.resources.error_too_many_requests
import sikula_kmp.composeapp.generated.resources.error_unauthorized
import sikula_kmp.composeapp.generated.resources.error_unknown
import sikula_kmp.composeapp.generated.resources.info_count_recording
import sikula_kmp.composeapp.generated.resources.info_count_recording_team
import sikula_kmp.composeapp.generated.resources.info_initialize_boat_race_recording
import sikula_kmp.composeapp.generated.resources.info_initialize_trail_recording
import sikula_kmp.composeapp.generated.resources.warning_empty_list
import sikula_kmp.composeapp.generated.resources.warning_no_camp
import sikula_kmp.composeapp.generated.resources.warning_not_fill_consumer_bank_account
import sikula_kmp.composeapp.generated.resources.warning_not_fill_supplier_bank_account
import sikula_kmp.composeapp.generated.resources.warning_not_valid_amount
import sikula_kmp.composeapp.generated.resources.warning_payment_by_consumer
import sikula_kmp.composeapp.generated.resources.warning_payment_by_supplier
import sikula_kmp.composeapp.generated.resources.warning_payment_in_cash
import sikula_kmp.composeapp.generated.resources.warning_qr_generating_error
import sikula_kmp.composeapp.generated.resources.warning_results_not_saving

fun DataError.toUiText(): UiText {
    val stringRes = when(this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.NOT_FOUND -> Res.string.error_not_found
        DataError.Remote.BAD_REQUEST -> Res.string.error_bad_request
        DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
        DataError.Remote.FORBIDDEN -> Res.string.error_forbidden
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }
    return UiText.StringResourceId(stringRes)
}

fun Warning.toUiText(): UiText {
    val stringRes = when(this) {
        Warning.Common.EMPTY_LIST -> Res.string.warning_empty_list
        Warning.Common.NO_CAMP -> Res.string.warning_no_camp
        Warning.Common.NONEXISTENT_LEADER -> Res.string.description_nonexistent_leader
        Warning.Common.NONEXISTENT_CHILD -> Res.string.description_nonexistent_child
        Warning.Common.NONEXISTENT_EMAIL -> Res.string.description_nonexistent_leader_email
        Warning.Common.RESULTS_NOT_SAVING -> Res.string.warning_results_not_saving
        Warning.Common.UNKNOWN -> Res.string.error_unknown
        Warning.QrState.PAYMENT_IN_CASH -> Res.string.warning_payment_in_cash
        Warning.QrState.NOT_FILL_SUPPLIER_BANK_ACCOUNT -> Res.string.warning_not_fill_supplier_bank_account
        Warning.QrState.NOT_FILL_CONSUMER_BANK_ACCOUNT -> Res.string.warning_not_fill_consumer_bank_account
        Warning.QrState.NOT_VALID_AMOUNT -> Res.string.warning_not_valid_amount
        Warning.QrState.PAYMENT_BY_SUPPLIER -> Res.string.warning_payment_by_supplier
        Warning.QrState.PAYMENT_BY_CONSUMER -> Res.string.warning_payment_by_consumer
        Warning.QrState.QR_GENERATING_ERROR -> Res.string.warning_qr_generating_error
    }
    return UiText.StringResourceId(stringRes)
}

fun Info.toUiText(): UiText {
    val stringRes = when(this) {
        Info.Common.INITIALIZE_TRAIL_RECORDING -> Res.string.info_initialize_trail_recording
        Info.Common.INITIALIZE_BOAT_RACE_RECORDING -> Res.string.info_initialize_boat_race_recording
        Info.Common.COUNT_RECODING -> Res.string.info_count_recording
        Info.Common.COUNT_RECODING_TEAM -> Res.string.info_count_recording_team
        Info.Common.UNKNOWN -> Res.string.error_unknown

    }
    return UiText.StringResourceId(stringRes)
}