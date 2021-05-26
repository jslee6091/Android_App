package com.example.auth0_first

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val TAG = "TAG_MainActivity" //로그를 분류할 태그

        lateinit var mRetrofit : Retrofit // 사용할 Retrofit Object
        lateinit var mRetrofitAPI: RetrofitAPI // Retrofit api Object
        lateinit var mCallTodoList : Call<JsonObject> // Json 데이터를 요청하는 Object
        lateinit var mRetrofitPOST: RetrofitPOST // Retrofit POST object
        lateinit var mDataTransfer: Call<DataModel.CashInfo> // POST Json Data

        var button1 = findViewById<Button>(R.id.button1) as Button
        var progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
        var textView = findViewById<TextView>(R.id.textView) as TextView

        fun setRetrofit(){
            // Retrofit 에서 가져올 url 설정 후 세팅
            mRetrofit = Retrofit
                .Builder()
                .baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            //interface 로 만든 Retrofit api request 받는 것 변수로 등록
            mRetrofitAPI = mRetrofit.create(RetrofitAPI::class.java)

            //POST Retrofit
            mRetrofitPOST = mRetrofit.create(RetrofitPOST::class.java)
        }

        // Retrofit setting
        setRetrofit()

        //GET 방식 : http response 받는 callback method
        val mRetrofitCallback  = (object : retrofit2.Callback<JsonObject>{ //Json 객체를 응답받는 콜백 객체

            //response 받기 실패
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
                Log.d(TAG, "에러입니다. => ${t.message.toString()}")
                textView.text = "에러\n" + t.message.toString()

                progressBar.visibility = View.GONE
                button1.visibility = View.VISIBLE
            }
            //response 가져오기 성공 -> 성공한 반응 처리
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val result = response.body()
                Log.d(TAG, "결과는 => $result")

                var mGson = Gson()
                val dataParsed1 = mGson.fromJson(result, DataModel.TodoInfo1::class.java)
                val dataParsed2 = mGson.fromJson(result, DataModel.TodoInfo2::class.java)
                val dataParsed3 = mGson.fromJson(result, DataModel.TodoInfo3::class.java)

                textView.text = "해야할 일\n" + dataParsed1.todo1.task+"\n"+dataParsed2.todo2.task +"\n"+dataParsed3.todo3.task

                progressBar.visibility = View.GONE
                button1.visibility = View.VISIBLE
            }
        })

        //POST 방식 Callback 함수
        var mRetrofitPOSTCallback = (object : retrofit2.Callback<DataModel.CashInfo>{
            override fun onFailure(call: Call<DataModel.CashInfo>?, t: Throwable?) {
                Log.e("retrofit Failure", t.toString())
            }

            override fun onResponse(call: Call<DataModel.CashInfo>?, response: Response<DataModel.CashInfo>?) {
                Log.d("retrofit Success", response?.body().toString())
                val result = response.body()

                var mGson = Gson()
                val dataParsed1 = mGson.fromJson(result, DataModel.TodoInfo1::class.java)
                val dataParsed2 = mGson.fromJson(result, DataModel.TodoInfo2::class.java)
                val dataParsed3 = mGson.fromJson(result, DataModel.TodoInfo3::class.java)

                textView.text = "해야할 일\n" + dataParsed1.todo1.task+"\n"+dataParsed2.todo2.task +"\n"+dataParsed3.todo3.task

                progressBar.visibility = View.GONE
                button1.visibility = View.VISIBLE
            }
        })

        // Connect RetrofitAPI and Callback
        fun callTodoList(){
            // RetrofitAPI 에서 Json Object 요청 반환 메서드
            mCallTodoList = mRetrofitAPI.getTodoList()
            // 응답들을 큐에 넣어 대기시키는 callback. 응답이 생기면 데이터 빠져나감
            mCallTodoList.enqueue(mRetrofitCallback)

        }

        // Connect RetrofitPOST and Callback
        fun callRetrofitPOST(){
            // POST 방식
            mDataTransfer = mRetrofitPOST.dataTransfer("chicken", "123")
            mDataTransfer.enqueue(mRetrofitPOSTCallback)
        }

        // Flask data transfer button
        button1.setOnClickListener {
            button1.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            callRetrofitPOST()
        }

        // Logout 기능
        var kakao_logout_button = findViewById<Button>(R.id.kakao_logout_button) as Button

        kakao_logout_button.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                // Logout 이후 뒤로가기 누를 시 로그인 된 상태인 이전 화면으로 돌아가는 것을 방지
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        // User 정보 띄우기
        var id = findViewById<TextView>(R.id.id) as TextView
        var nickname = findViewById<TextView>(R.id.nickname) as TextView
        var profileimage_url = findViewById<TextView>(R.id.profileimage_url) as TextView
        var thumbnailimage_url = findViewById<TextView>(R.id.thumbnailimage_url) as TextView

        UserApiClient.instance.me { user, error ->
            id.text = "회원번호: ${user?.id}"
            nickname.text = "닉네임: ${user?.kakaoAccount?.profile?.nickname}"
            profileimage_url.text = "프로필 링크: ${user?.kakaoAccount?.profile?.profileImageUrl}"
            thumbnailimage_url.text = "썸네일 링크: ${user?.kakaoAccount?.profile?.thumbnailImageUrl}"
        }
    }
}