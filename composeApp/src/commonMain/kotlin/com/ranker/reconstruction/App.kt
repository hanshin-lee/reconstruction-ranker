package com.ranker.reconstruction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ranker.reconstruction.data.ComplexRepository
import com.ranker.reconstruction.data.model.Complex
import com.ranker.reconstruction.ui.addcomplex.AddComplexScreen
import com.ranker.reconstruction.ui.disclaimer.DisclaimerScreen
import com.ranker.reconstruction.ui.margin.MarginScreen
import com.ranker.reconstruction.ui.quicksim.QuickSimulationScreen
import com.ranker.reconstruction.ui.ranking.RankingScreen
import com.ranker.reconstruction.ui.simulation.ContributionScreen
import com.ranker.reconstruction.ui.simulation.ProportionalRateScreen
import com.ranker.reconstruction.ui.simulation.SimulationState
import com.ranker.reconstruction.ui.theme.RankerTheme
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Ranking : Screen
    data object AddComplex : Screen
    data object QuickSimulation : Screen
    data class Disclaimer(val complex: Complex) : Screen
    data class ProportionalRate(val state: SimulationState) : Screen
    data class Contribution(val state: SimulationState) : Screen
    data class Margin(val state: SimulationState) : Screen
}

@Composable
fun App() {
    RankerTheme {
        var complexes by remember { mutableStateOf<List<Complex>?>(null) }
        var screen by remember { mutableStateOf<Screen>(Screen.Ranking) }
        var refreshKey by remember { mutableStateOf(0) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(refreshKey) {
            complexes = ComplexRepository.loadAll()
        }

        val loadedComplexes = complexes
        if (loadedComplexes == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@RankerTheme
        }

        val goHome = { screen = Screen.Ranking }

        when (val s = screen) {
            is Screen.Ranking -> RankingScreen(
                complexes = loadedComplexes,
                onComplexClick = { screen = Screen.Disclaimer(it) },
                onAddComplexClick = { screen = Screen.AddComplex },
                onQuickSimulationClick = { screen = Screen.QuickSimulation },
                onDeleteComplex = { complex ->
                    scope.launch {
                        ComplexRepository.deleteComplex(complex.id)
                        refreshKey++
                    }
                }
            )

            is Screen.AddComplex -> AddComplexScreen(
                onSaved = { complex ->
                    scope.launch {
                        ComplexRepository.addComplex(complex)
                        refreshKey++
                        screen = Screen.Ranking
                    }
                },
                onCancel = goHome
            )

            is Screen.QuickSimulation -> QuickSimulationScreen(
                onStartSimulation = { state -> screen = Screen.ProportionalRate(state) },
                onBack = goHome
            )

            is Screen.Disclaimer -> DisclaimerScreen(
                complex = s.complex,
                onAccept = { screen = Screen.ProportionalRate(SimulationState(s.complex)) },
                onBack = goHome,
                onNavigateHome = goHome
            )

            is Screen.ProportionalRate -> ProportionalRateScreen(
                state = s.state,
                onNavigateToContribution = { screen = Screen.Contribution(s.state) },
                onBack = { screen = Screen.Disclaimer(s.state.complex) },
                onNavigateHome = goHome
            )

            is Screen.Contribution -> ContributionScreen(
                state = s.state,
                onNavigateToMargin = { screen = Screen.Margin(s.state) },
                onBack = { screen = Screen.ProportionalRate(s.state) },
                onNavigateHome = goHome
            )

            is Screen.Margin -> MarginScreen(
                state = s.state,
                onBack = { screen = Screen.Contribution(s.state) },
                onNavigateHome = goHome
            )
        }
    }
}
