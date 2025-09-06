package cz.bosan.sikula_kmp.core.domain

sealed interface Warning: Error {
    enum class Common: Warning {
        EMPTY_LIST,
        NO_CAMP,
        NONEXISTENT_LEADER,
        NONEXISTENT_CHILD,
        NONEXISTENT_EMAIL,
        RESULTS_NOT_SAVING,
        UNKNOWN
    }

    enum class QrState: Warning {
        PAYMENT_IN_CASH,
        NOT_FILL_SUPPLIER_BANK_ACCOUNT,
        NOT_FILL_CONSUMER_BANK_ACCOUNT,
        NOT_VALID_AMOUNT,
        PAYMENT_BY_SUPPLIER,
        PAYMENT_BY_CONSUMER,
        QR_GENERATING_ERROR,
    }
}