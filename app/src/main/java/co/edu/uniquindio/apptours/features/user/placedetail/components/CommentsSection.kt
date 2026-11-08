package co.edu.uniquindio.apptours.features.user.placedetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ColabTextField
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.core.util.relativeTimeLabel
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.validation.PlaceForms

@Composable
internal fun CommentsHeaderAndInput(
    commentsCount: Int,
    commentText: String,
    errorMessage: String?,
    isSending: Boolean,
    requester: FieldRequester,
    onCommentTextChange: (String) -> Unit,
    onCommentFocusLost: () -> Unit,
    onSendComment: () -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.comments_title, commentsCount),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        ColabTextField(
            value = commentText,
            onValueChange = { onCommentTextChange(it.take(PlaceForms.MAX_COMMENT_LENGTH + 1)) },
            label = "",
            placeholder = stringResource(R.string.comments_placeholder),
            singleLine = false,
            errorMessage = errorMessage,
            fieldRequester = requester,
            onFocusLost = onCommentFocusLost,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSendComment,
            enabled = !isSending,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSending) ColabGrayBorder else ColabDarkGreen)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.comments_send),
                tint = Color.White
            )
        }
    }
}

@Composable
internal fun CommentCard(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColabSurface)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = ColabSageBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = comment.authorName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = ColabDarkGreen
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ColabTextPrimary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        SmallTag(text = stringResource(comment.authorLevel.labelRes()), color = ColabDarkGreen, background = ColabSageBg)
                    }
                    Text(
                        text = relativeTimeLabel(comment.createdAt),
                        fontSize = 11.sp,
                        color = ColabTextSecondary
                    )
                }

                if (comment.isVisitExperience) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(8.dp), color = ColabAmberBg) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Hiking, contentDescription = null, tint = ColabBrown, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = stringResource(R.string.comments_visit_tag),
                                    fontSize = 10.sp,
                                    color = ColabBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        comment.rating?.let {
                            Spacer(modifier = Modifier.width(8.dp))
                            RatingStars(rating = it, size = 14)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    fontSize = 13.sp,
                    color = ColabTextPrimary,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun SmallTag(text: String, color: Color, background: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = background) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/** Estrellas de solo lectura (1 a 5). */
@Composable
internal fun RatingStars(rating: Int, size: Int) {
    val description = stringResource(R.string.rating_description, rating, PlaceForms.MAX_RATING)
    Row {
        (1..PlaceForms.MAX_RATING).forEach { star ->
            Icon(
                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = if (star == 1) description else null,
                tint = ColabBrown,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}
