package com.app.kickboxing.Activitys.Data.reposistory


import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.app.kickboxing.Activitys.Data.api.ApiService
import com.app.kickboxing.Activitys.domain.model.CancelWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.CreateProfileRequest
import com.app.kickboxing.Activitys.domain.model.HomeRequest
import com.app.kickboxing.Activitys.domain.model.JoinWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.LeaderboardDetailRequest
import com.app.kickboxing.Activitys.domain.model.LoginRequest
import com.app.kickboxing.Activitys.domain.model.NotificationRequest
import com.app.kickboxing.Activitys.domain.model.RegisterRequest
import com.app.kickboxing.Activitys.domain.model.ResetPasswordRequest
import com.app.kickboxing.Activitys.domain.model.ScheduledWorkoutRequest
import com.app.kickboxing.Activitys.domain.model.SendOTPRequest
import com.app.kickboxing.Activitys.domain.model.SessionDetailRequest
import com.app.kickboxing.Activitys.domain.model.SessionRequest
import com.app.kickboxing.Activitys.domain.model.VerifyOtpRequest
import com.app.kickboxing.Activitys.domain.response.BaseResponse
import com.app.kickboxing.Activitys.domain.response.CancelWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.CreateProfileResponse
import com.app.kickboxing.Activitys.domain.response.GetProfileResponse
import com.app.kickboxing.Activitys.domain.response.HomeResponse
import com.app.kickboxing.Activitys.domain.response.JoinWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.LeaderBoardResponse
import com.app.kickboxing.Activitys.domain.response.LoginResponse
import com.app.kickboxing.Activitys.domain.response.LogoutResponse
import com.app.kickboxing.Activitys.domain.response.NotificationResponse
import com.app.kickboxing.Activitys.domain.response.ProfileData
import com.app.kickboxing.Activitys.domain.response.RegisterResponse
import com.app.kickboxing.Activitys.domain.response.ResetPasswordResponse
import com.app.kickboxing.Activitys.domain.response.ScheduledWorkoutResponse
import com.app.kickboxing.Activitys.domain.response.SendOTPResponse
import com.app.kickboxing.Activitys.domain.response.SessionDetailResponse
import com.app.kickboxing.Activitys.domain.response.SessionResponse
import com.app.kickboxing.Activitys.domain.response.VerifyOTPResponse
import com.app.kickboxing.Activitys.presentation.state.Results
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.http.PUT
import retrofit2.http.Part
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(private val apiService: ApiService) : AppRepository {

    override suspend fun userLoginReq(payload: LoginRequest): Results<LoginResponse> {
        return try {
            val response = apiService.login(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                 //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun home(payload: HomeRequest): Results<HomeResponse> {

        return try {
            val response = apiService.home(payload)
            Results.Success(response)


        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }


    }

    override suspend fun userRegisterAccount(payload: RegisterRequest): Results<RegisterResponse> {
        return try {
            val response = apiService.registerAccount(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun userSendOTPRequest(payload: SendOTPRequest): Results<SendOTPResponse> {
        return try {
            val response = apiService.sendOtp(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun userVerifyOTPRequest(payload: VerifyOtpRequest): Results<VerifyOTPResponse> {
        return try {
            val response = apiService.verifyOtp(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun userResetPassword(payload: ResetPasswordRequest): Results<ResetPasswordResponse> {
        return try {
            val response = apiService.resetPassword(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun session(payload: SessionRequest): Results<SessionResponse> {
        return try {
            val response = apiService.session(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun sessionDetails(payload: SessionDetailRequest): Results<SessionDetailResponse> {
        return try {
            val response = apiService.sessionDetails(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun scheduledWorkout(payload: ScheduledWorkoutRequest): Results<ScheduledWorkoutResponse> {
        return try {
            val response = apiService.scheduledWorkout(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun joinWorkout(payload: JoinWorkoutRequest): Results<JoinWorkoutResponse> {

        return try {
            val response = apiService.joinWorkout(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun cancelWorkout(payload: CancelWorkoutRequest): Results<CancelWorkoutResponse> {
        return try {
            val response = apiService.cancelWorkout(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }


    override suspend fun leaderboardDetails(payload: LeaderboardDetailRequest): Results<LeaderBoardResponse> {
        return try {
            val response = apiService.leaderboardDetails(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun logout(): Results<LogoutResponse> {
        return try {

            val response = apiService.logout()
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun notification(payload: NotificationRequest): Results<NotificationResponse> {
        return try {

            val response = apiService.notification(payload)
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun userCreateProfile(payload: CreateProfileRequest, context: Context): Results<CreateProfileResponse> {
        return try {

            val response = apiService.createProfile(
                userName = createTextPart(payload.userName),
                fullName = createTextPart(payload.fullName),
                DOB = createTextPart(payload.DOB),
                gender = createTextPart(payload.gender),
                height = createDoublePart(payload.height?:0.0),
                weight = createIntPart(payload.weight?:0),
                profile = createImagePart(context, payload.userImageUri),
                weightGoal = createIntPart(payload.weightGoals?:0),
                punchAverageGoal  = createIntPart(payload.punchAverageGoal?:0),
                        completeProfile=createBoolPart(payload.completeProfile?:true)

            )
            Results.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }


    override suspend fun userGetProfile(): Results<GetProfileResponse> {
        return try {

            val response = apiService.getProfile()
            Results.Success(response)

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    override suspend fun userUpdateProfile(payload: CreateProfileRequest,context: Context): Results<GetProfileResponse> {

        return try {
            val response = apiService.updateProfile(
                userName = createTextPart(payload.userName),
                fullName = createTextPart(payload.fullName),
                DOB = createTextPart(payload.DOB),
                gender = createTextPart(payload.gender),
                height = createDoublePart(payload.height?:0.0),
                weight = createIntPart(payload.weight?:0),
                profile = createImagePart(context, payload.userImageUri),
                weightGoal = createIntPart(payload.weightGoals?:0),
                punchAverageGoal  = createIntPart(payload.punchAverageGoal?:0)
            )
            Results.Success(response)

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                when (val message = errorResponse.message) {
                    is String -> message
                    //   is List<*> -> message.joinToString(", ") { it.toString() }
                    else -> "Unknown error"
                }
            } catch (jsonException: Exception) {
                e.message() ?: "Unknown error"
            }
            Results.Failure(errorMessage, e.code())
        } catch (e: IOException) {
            Results.Failure("Network error: ${e.message}", -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Failure("Unexpected error: ${e.message}", -1)
        }
    }

    private fun createImagePart(context: Context, uri: Uri?): MultipartBody.Part? {
        return uri?.let {
            val file = File(getRealPathFromURI(context, it))
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val requestFile = contentUri.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), file)
            }
            MultipartBody.Part.createFormData("profile", file.name, requestFile)
        }
    }

    private fun createTextPart(value: String?): RequestBody? {
        return value?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it)
        }
    }

    fun createStringPart(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createIntPart(value: Int): RequestBody {
        return value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
    }
    fun createBoolPart(value: Boolean): RequestBody {
        return value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createDoublePart(value: Double): RequestBody {
        return value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun createBooleanPart(value: Boolean): RequestBody {
        return value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val filePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return filePath ?: ""
    }

}


    fun parseErrorMessage(json: String?): String {
        return try {
            val obj = JSONObject(json ?: "")
            obj.optString("message", "Unknown error")
        } catch (e: Exception) {
            "Unknown error"
        }
    }


