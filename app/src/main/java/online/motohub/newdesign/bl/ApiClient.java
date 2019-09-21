package online.motohub.newdesign.bl;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import online.motohub.R;
import online.motohub.application.MotoHub;
import online.motohub.interfaces.RetrofitApiInterface;
import online.motohub.interfaces.UserPreferences;
import online.motohub.model.ErrorMessage;
import online.motohub.util.PreferenceUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private final UserPreferences sharedPreference;
    public RetrofitApiInterface apiInterface;
    public Converter<ResponseBody, ErrorMessage> errorConverter;

    public ApiClient(String base_url, UserPreferences preferences, MHFileCacheImplementor fileCacheImplementor) {
        this.sharedPreference = preferences;
        final String apiKey = MotoHub.getApplicationInstance().getResources().getString(R.string.dream_factory_api_key);
        final String sessionToken = sharedPreference.getString(PreferenceUtils.SESSION_TOKEN);
        OkHttpClient okHttpClient = getUnsafeOkHttpClient()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                                Request.Builder requestBuilder =
                                        chain.request().newBuilder()
                                                .addHeader("X-DreamFactory-Api-Key", apiKey)
                                                .addHeader("Accept", "Application/JSON");
                                if (!sessionToken.isEmpty()) {
                                    requestBuilder.addHeader("X-DreamFactory-Session-Token", sessionToken);
                                }
                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }).connectTimeout(60, TimeUnit.SECONDS)
                .addNetworkInterceptor(new ResponseInterceptor(fileCacheImplementor))
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        errorConverter = retrofit.responseBodyConverter(ErrorMessage.class, new Annotation[0]);
        apiInterface = retrofit.create(RetrofitApiInterface.class);
    }


    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            if (BuildConfig.DEBUG) {
//                builder.addInterceptor(logging);
//            }

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });


            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
