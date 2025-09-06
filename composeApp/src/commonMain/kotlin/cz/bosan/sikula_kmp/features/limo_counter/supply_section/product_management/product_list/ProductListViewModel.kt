package cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.Product
import cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager.ProductRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val leaderRepository: LeaderRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(currentLeader = leaderRepository.getCurrentLeaderLocal())
            }
            loadProducts()
            _state.update{
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: ProductListAction) {
        when (action) {
            ProductListAction.OnLogoutClicked -> onLogoutClicked()
            ProductListAction.ResetLogout -> resetLogoutSuccessful()
            is ProductListAction.OnProductSelected -> {}
        }
    }

    private suspend fun loadProducts() {
        productRepository.getProducts(
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { products ->
            _state.update {
                it.copy(
                    products = products.sortedByDescending { it.timeStamp },
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    products = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private fun onLogoutClicked() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            leaderRepository.deleteCurrentLeader()
            _state.update {
                it.copy(logoutSuccessful = true)
            }
        }
    }

    private fun resetLogoutSuccessful() {
        _state.update {
            it.copy(logoutSuccessful = false)
        }
    }

}

sealed interface ProductListAction {
    data object OnLogoutClicked : ProductListAction
    data object ResetLogout : ProductListAction
    data class OnProductSelected(val product: Product) : ProductListAction
}

data class ProductListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val products: List<Product> = emptyList(),
)