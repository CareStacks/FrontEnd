package pe.edu.upc.careconnect.presentation.agenda

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.careconnect.R
import pe.edu.upc.careconnect.presentation.theme.Background
import pe.edu.upc.careconnect.presentation.theme.Border
import pe.edu.upc.careconnect.presentation.theme.Neutral
import pe.edu.upc.careconnect.presentation.theme.Primary
import pe.edu.upc.careconnect.presentation.theme.Secondary
import pe.edu.upc.careconnect.presentation.theme.Surface
import pe.edu.upc.careconnect.presentation.theme.Tertiary
import pe.edu.upc.careconnect.presentation.theme.TextPrimary
import pe.edu.upc.careconnect.presentation.theme.TextSecondary

@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onRescheduleClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
    ) {
        EventDetailHeader(
            onBackClick = onBackClick,
            onNotificationsClick = onNotificationsClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                EventHeroCard()

                Spacer(modifier = Modifier.height(24.dp))

                DetailInfoCard(
                    icon = R.drawable.ic_calendar,
                    iconBackground = Primary.copy(alpha = 0.18f),
                    iconTint = Primary,
                    label = "FECHA Y HORA",
                    title = "24 Oct, 2023",
                    description = "10:30 AM - 11:30 AM"
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatusInfoCard()

                Spacer(modifier = Modifier.height(16.dp))

                DetailInfoCard(
                    icon = R.drawable.ic_location,
                    iconBackground = Secondary.copy(alpha = 0.85f),
                    iconTint = Color(0xFF4E6F61),
                    label = "UBICACIÓN",
                    title = "Centro Médico Salud\nIntegral",
                    description = "Av. Principal 123, Piso 4 -\nConsultorio 402"
                )

                Spacer(modifier = Modifier.height(16.dp))

                DescriptionCard()
            }
        }

        Button(
            onClick = onConfirmClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Surface
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = "Confirmar evento",
                tint = Surface,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Confirmar evento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRescheduleClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.5.dp, Color(0xFF4E6F61)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF4E6F61)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_reschedule),
                contentDescription = "Reprogramar",
                tint = Color(0xFF4E6F61),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Reprogramar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EventDetailHeader(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Volver",
                tint = Neutral,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Detalle del evento",
            color = Primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notifications),
                contentDescription = "Notificaciones",
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun EventHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Neutral)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_event_detail),
            contentDescription = "Consulta médica",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.15f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFFD7B5))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "CITA MÉDICA",
                    color = Neutral,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Consulta con Dr.\nMartínez",
                color = Surface,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            )
        }
    }
}

@Composable
private fun DetailInfoCard(
    @DrawableRes icon: Int,
    iconBackground: Color,
    iconTint: Color,
    label: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 23.sp
                )
            }
        }
    }
}

@Composable
private fun StatusInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Tertiary.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Estado",
                    tint = Color(0xFFB36A18),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ESTADO",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pendiente",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB36A18))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Esperando confirmación",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DescriptionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "DESCRIPCIÓN",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Revisión trimestral de rutina para control de presión arterial y ajuste de medicación. El paciente debe asistir en ayunas y traer los resultados de los análisis de sangre realizados la semana pasada.",
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Primary.copy(alpha = 0.10f))
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(104.dp)
                            .background(Primary)
                    )

                    Text(
                        text = "Nota: Favor de llegar 15 minutos\nantes para el registro en\nrecepción.",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 25.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}