package pe.edu.upc.careconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pe.edu.upc.careconnect.presentation.navigation.RootNavHost
import pe.edu.upc.careconnect.presentation.onboarding.OnBoarding
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CareConnectTheme {
                RootNavHost()
            }
        }
    }
}