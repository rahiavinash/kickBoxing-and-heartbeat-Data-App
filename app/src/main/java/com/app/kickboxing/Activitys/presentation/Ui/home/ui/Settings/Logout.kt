package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.GrayBorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun LogoutDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Logout",  style = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
            ),) },
            text = { Text("Are you sure you want to logout?" , style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.dmsans_medium)),
            ),) },
            confirmButton = {
                Text(
                    text = "Logout",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 18.sp,
                        color = WhiteColor
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onConfirmLogout()
                            onDismiss()
                        },
                    color = Color.Red
                )
            },
            dismissButton = {
                Text(
                    text = "Cancel",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 18.sp,
                        color = WhiteColor
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onDismiss() },
                    color = Color.Gray
                )
            }
        )
    }
}


@Composable
fun CustomPopup(openDialogCustom: MutableState<Boolean> ,  onLogoutConfirm: () -> Unit) {
    Dialog(onDismissRequest = { openDialogCustom.value }) {
        CustomDialogUI(openDialogCustom = openDialogCustom , onLogoutConfirm)
    }
}


@Composable
fun CustomDialogUI(
    openDialogCustom: MutableState<Boolean>,
    onLogoutConfirm: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(35.dp),
        modifier = Modifier
           // .padding(10.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(BlackCardBackground)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(35.dp)
                )
                .padding(20.dp)
        ) {

            Text(
                text = "Logout",
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                color =Color.White,
                modifier = Modifier
                    .padding(top = 15.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )


            Text(
                text = "Are you sure you want to Logout?",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Cancel Button
                Text(
                    text = "Cancel",
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.LightGray)
                        .clickable { openDialogCustom.value = false }
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    color = Color.Black,
                    fontSize = 16.sp
                )
Spacer(modifier = Modifier.width(10.dp))
                // Logout Button
                Text(
                    text = "Logout",
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .background(ButtonRed)
                        .clickable {
                            openDialogCustom.value = false
                            onLogoutConfirm()
                        }
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

        }
    }
}

@Preview
@Composable
fun ShowLogout(){
//LogoutDialog(showDialog = true, onDismiss = { false }) {
//}
}
