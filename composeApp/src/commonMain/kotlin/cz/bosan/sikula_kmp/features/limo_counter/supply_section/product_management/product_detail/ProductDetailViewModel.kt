package cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.Product
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val leaderRepository: LeaderRepository,
    private val productRepository: ProductRepository,
    private val productId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(currentLeader = leaderRepository.getCurrentLeaderLocal())
            }
            if (productId != newItemID) {
                loadProduct(productId)
            }
            loadBuyer()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: ProductDetailAction) {
        when (action) {
            is ProductDetailAction.UpdateProduct -> {
                _state.update { it.copy(product = it.product.let(action.update)) }
            }

            is ProductDetailAction.UpdateValidationState -> updateValidationState(action.changedValue)
            ProductDetailAction.OnSaveProductClick -> validateValues()
            is ProductDetailAction.UpdateNumber -> updateNumberString(
                number = action.number,
                numberTyp = action.numberTyp
            )
        }
    }

    private suspend fun loadProduct(productId: Int) {
        productRepository.getProduct(
            campId = _state.value.currentLeader.camp.id,
            productId = productId
        ).onSuccess { product ->
            _state.update {
                it.copy(
                    product = product,
                    boughtAmountString = product.boughtAmount.toString(),
                    purchasedPriceString = product.purchasePrice.toString(),
                    salePriceString = product.salePrice.toString()
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    product = Product.EMPTY,
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private suspend fun loadBuyer(){
        val buyerId = _state.value.product.buyerId
        var buyer: Leader = Leader.EMPTY
        if(buyerId == Product.EMPTY.buyerId || buyerId == _state.value.currentLeader.leader.id){
            buyer = _state.value.currentLeader.leader
        } else{
            leaderRepository.getLeader(
                leaderId = buyerId,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess { leader ->
                buyer = leader
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
        _state.update{
            it.copy(buyer = buyer)
        }
    }

    private fun updateNumberString(number: String, numberTyp: String) {
        if (numberTyp == "boughtAmount") {
            _state.update {
                it.copy(boughtAmountString = number)
            }
        } else if (numberTyp == "purchasePrice") {
            _state.update {
                it.copy(purchasedPriceString = number)
            }
        } else if (numberTyp == "salePrice") {
            _state.update {
                it.copy(salePriceString = number)
            }
        }
    }


    private fun validateValues() {
        viewModelScope.launch {
            if (_state.value.product.name.isBlank()) {
                _state.update {
                    it.copy(isNameValid = false)
                }
            } else {
                _state.update {
                    it.copy(isNameValid = true)
                }
            }

            if (_state.value.boughtAmountString.isNullOrBlank()) {
                _state.update {
                    it.copy(isBoughtAmountValid = false)
                }
            } else {
                try {
                    val converted = _state.value.boughtAmountString!!.toInt()
                    _state.update {
                        it.copy(
                            isBoughtAmountValid = true,
                            product = _state.value.product.copy(boughtAmount = converted)
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(isBoughtAmountValid = false)
                    }
                }
            }

            if (_state.value.purchasedPriceString.isNullOrBlank()) {
                _state.update {
                    it.copy(isPurchasePriceValid = false)
                }
            } else {
                try {
                    val converted = _state.value.purchasedPriceString!!.toDouble()
                    _state.update {
                        it.copy(
                            isPurchasePriceValid = true,
                            product = _state.value.product.copy(purchasePrice = converted)
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(isPurchasePriceValid = false)
                    }
                }
            }

            if (_state.value.salePriceString.isNullOrBlank()) {
                _state.update {
                    it.copy(isSalePriceValid = false)
                }
            } else {
                try {
                    val converted = _state.value.salePriceString!!.toDouble()
                    _state.update {
                        it.copy(
                            isSalePriceValid = true,
                            product = _state.value.product.copy(salePrice = converted)
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(isSalePriceValid = false)
                    }
                }
            }

            if(_state.value.product.id != newItemID && _state.value.product.comment.isEmpty()){
                _state.update {
                    it.copy(isCommentValid = false)
                }
            }

            if (_state.value.isNameValid && _state.value.isBoughtAmountValid && _state.value.isPurchasePriceValid && _state.value.isSalePriceValid && _state.value.isCommentValid) {
                if (_state.value.product.id == newItemID) {
                    createProduct()
                } else {
                    updateProduct()
                }
            }
        }
    }

    private fun createProduct() {

    }

    private fun updateProduct() {

    }

    private fun updateValidationState(changedValue: String) {
        when (changedValue) {
            "name" -> {
                _state.update {
                    it.copy(isNameValid = true)
                }
            }

            "boughtAmount" -> {
                _state.update {
                    it.copy(isBoughtAmountValid = true)
                }
            }

            "purchasePrice" -> {
                _state.update {
                    it.copy(isPurchasePriceValid = true)
                }
            }

            "salePrice" -> {
                _state.update {
                    it.copy(isSalePriceValid = true)
                }
            }

            "comment" -> {
                _state.update {
                    it.copy(isCommentValid = true)
                }
            }
        }
    }


}

sealed interface ProductDetailAction {
    data class UpdateProduct(val update: Product.() -> Product) : ProductDetailAction
    data class UpdateNumber(val number: String, val numberTyp: String) : ProductDetailAction
    data class UpdateValidationState(val changedValue: String) : ProductDetailAction
    data object OnSaveProductClick : ProductDetailAction
}

data class ProductDetailState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val product: Product = Product.EMPTY,
    val boughtAmountString: String? = null,
    val purchasedPriceString: String? = null,
    val salePriceString: String? = null,
    val buyer: Leader = Leader.EMPTY,
    //val updatedNumber: String? = null,
    val isNameValid: Boolean = true,
    val isBoughtAmountValid: Boolean = true,
    val isPurchasePriceValid: Boolean = true,
    val isSalePriceValid: Boolean = true,
    val isCommentValid: Boolean = true,
    val productCreateSuccessfully: Boolean = false
)