package components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.*

@Composable
fun OpenSourcePage() {
    Div {
        H1 {
            Text("Ai Là Ai is Open Source")
        }
        H3 {
            Text("This website")
        }
        A("https://github.com/Queatz/ailaai-web/") {
            Text("Go to GitHub")
        }
        H3 {
            Text("Ai Là Ai Server")
        }
        A("https://github.com/Queatz/ailaai-backend") {
            Text("Go to GitHub")
        }
        H3 {
            Text("Ai Là Ai for Android")
        }
        A("https://github.com/Queatz/AiLaAi") {
            Text("Go to GitHub")
        }
    }
}

