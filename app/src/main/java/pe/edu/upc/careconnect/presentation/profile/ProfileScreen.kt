package pe.edu.upc.careconnect.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.presentation.components.AppIcon
import pe.edu.upc.careconnect.presentation.components.CareScreenHeader
import pe.edu.upc.careconnect.presentation.theme.BackgroundSoft
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.CareConnectTheme
import pe.edu.upc.careconnect.presentation.theme.GreenDark
import pe.edu.upc.careconnect.presentation.theme.GreenLight
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.PrimaryDark
import pe.edu.upc.careconnect.presentation.theme.PrimaryLight
import pe.edu.upc.careconnect.presentation.theme.RedDark
import pe.edu.upc.careconnect.presentation.theme.RedLight
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun ProfileScreen(
    onManageAccessClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft)
    ) {
        CareScreenHeader(
            title = "CareConnect",
            onActionClick = onNotificationsClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            ProfileHero()
            ProfileOptions(onManageAccessClick = onManageAccessClick)
            ProfileStats()
        }
    }
}

@Composable
private fun ProfileHero() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .background(Surface, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EG",
                        style = MaterialTheme.typography.headlineLarge,
                        color = PrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    icon = R.drawable.ic_profile,
                    contentDescription = null,
                    tint = Surface,
                    size = 18.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Elena García",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "elena.garcia@email.com",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .background(GreenLight, RoundedCornerShape(999.dp))
                .padding(horizontal = 16.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                icon = R.drawable.ic_shield,
                contentDescription = null,
                tint = GreenDark,
                size = 18.dp
            )
            Text(
                text = "Paciente",
                style = MaterialTheme.typography.labelMedium,
                color = GreenDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProfileOptions(onManageAccessClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Configuración de cuenta",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )

        ProfileOptionButton(
            icon = R.drawable.ic_shield,
            iconBackground = PrimaryLight,
            iconTint = Primary,
            text = "Gestionar accesos",
            contentDescription = "Gestionar accesos al perfil",
            onClick = onManageAccessClick
        )

        ProfileOptionButton(
            icon = R.drawable.ic_lock,
            iconBackground = RedLight,
            iconTint = RedDark,
            text = "Cerrar sesión",
            contentDescription = "Cerrar sesión",
            onClick = { }
        )
    }
}

@Composable
private fun ProfileOptionButton(
    icon: Int,
    iconBackground: Color,
    iconTint: Color,
    text: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(iconBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        icon = icon,
                        contentDescription = null,
                        tint = iconTint,
                        size = 20.dp
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AppIcon(
                icon = R.drawable.ic_chevron_right,
                contentDescription = contentDescription,
                tint = TextSecondary,
                size = 18.dp
            )
        }
    }
}

@Composable
private fun ProfileStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileStatCard(
            icon = R.drawable.ic_shield,
            title = "Perfil\nVerificado",
            modifier = Modifier.weight(1f)
        )

        ProfileStatCard(
            icon = R.drawable.ic_clock,
            title = "2 Años de\nCuidado",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileStatCard(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(134.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppIcon(
                icon = icon,
                contentDescription = null,
                tint = Primary,
                size = 26.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    CareConnectTheme {
        ProfileScreen(
            onManageAccessClick = { },
            onNotificationsClick = { }
        )
    }
}
