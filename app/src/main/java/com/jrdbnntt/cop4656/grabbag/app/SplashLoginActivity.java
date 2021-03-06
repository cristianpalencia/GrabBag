package com.jrdbnntt.cop4656.grabbag.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jrdbnntt.cop4656.grabbag.R;
import com.jrdbnntt.cop4656.grabbag.api.GrabBagApi;
import com.jrdbnntt.cop4656.grabbag.api.modules.user.data.LoginRequest;
import com.jrdbnntt.cop4656.grabbag.api.modules.user.data.get.SummaryResponse;
import com.jrdbnntt.cop4656.grabbag.api.util.data.EmptyResponse;

/**
 * Splash screen + login screen.
 * There is persistent login, so they will be redirected if already logged in
 */

public class SplashLoginActivity extends AppCompatActivity {
    GrabBagApi api;
    Button bRegister, bLogIn;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new GrabBagApi(this);
        super.onPostResume();
        api.getUserModule().getSummary(new Response.Listener<SummaryResponse>() {
            @Override
            public void onResponse(SummaryResponse response) {
                if (response.username != null) {
                    Log.v("Log in", "Already logged in with username " + response.username);
                    continueToMain();
                } else {
                    Log.v("Log in", "Not logged in, waiting for user");
                    init();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                init();
            }
        });
    }

    public void init() {
        setContentView(R.layout.activity_splash_login);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        bLogIn = (Button) findViewById(R.id.bLogIn);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogIn();
            }
        });
    }

    /**
     * When the user is logged in, move to the main activity
     */
    private void continueToMain() {
        Intent intent = new Intent(getApplicationContext(), MainNavActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    private void attemptLogIn() {
        LoginRequest req = new LoginRequest();
        req.username = etUsername.getText().toString();
        req.password = etPassword.getText().toString();
        api.getUserModule().logIn(req, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                continueToMain();
            }
        }, api.dialogErrorListener(this));
    }
}
