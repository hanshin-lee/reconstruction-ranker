package com.ranker.reconstruction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ranker.reconstruction.data.ComplexRepository
import com.ranker.reconstruction.data.model.Complex
import com.ranker.reconstruction.ui.disclaimer.DisclaimerScreen
import com.ranker.reconstruction.ui.margin.MarginScreen
import com.ranker.reconstruction.ui.ranking.RankingScreen
import com.ranker.reconstruction.ui.simulation.ContributionScreen
import com.ranker.reconstruction.ui.simulation.ProportionalRateScreen
import com.ranker.reconstruction.ui.simulation.SimulationState
import com.ranker.reconstruction.ui.theme.RankerTheme

private sealed interface Screen {
    data object Ranking : Screen
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

        LaunchedEffect(Unit) {
            complexes = ComplexRepository.loadAll()
        }

        val loadedComplexes = complexes
        if (loadedComplexes == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@RankerTheme
        }

        when (val s = screen) {
            is Screen.Ranking -> RankingScreen(
                complexes = loadedComplexes,
                onComplexClick = { screen = Screen.Disclaimer(it) }
            )

            is Screen.Disclaimer -> DisclaimerScreen(
                complex = s.complex,
                onAccept = { screen = Screen.ProportionalRate(SimulationState(s.complex)) },
                onBack = { screen = Screen.Ranking }
            )

            is Screen.ProportionalRate -> ProportionalRateScreen(
                state = s.state,
                onNavigateToContribution = { screen = Screen.Contribution(s.state) },
                onBack = { screen = Screen.Ranking }
            )

            is Screen.Contribution -> ContributionScreen(
                state = s.state,
                onNavigateToMargin = { screen = Screen.Margin(s.state) },
                onBack = { screen = Screen.ProportionalRate(s.state) }
            )

            is Screen.Margin -> MarginScreen(
                state = s.state,
                onBack = { screen = Screen.Contribution(s.state) }
            )
        }
    }
}
