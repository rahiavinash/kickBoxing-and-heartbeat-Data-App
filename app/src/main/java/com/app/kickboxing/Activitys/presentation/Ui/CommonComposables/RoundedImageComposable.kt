package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.app.kickboxing.R
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.PlaceHolderColor

@Composable
fun RoundedImage(url: String?, size: Int) {
    val actualUrl = if (url.isNullOrBlank()) {
        "https://ctmirror.org/wp-content/uploads/2021/01/dummy-man-570x570-1.png"
    } else url

    val painter = rememberAsyncImagePainter(
        model = actualUrl,
        placeholder = painterResource(id = R.drawable.ic_dummy),
        error = painterResource(id = R.drawable.ic_dummy),
        contentScale = ContentScale.Crop
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
    )
}


@Preview
@Composable
fun RoundedImagePreview() {
    RoundedImage(
        url = "https://ctmirror.org/wp-content/uploads/2021/01/dummy-man-570x570-1.png",
        size = 38
    )
}