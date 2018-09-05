package anindya.sample.smackchat.utils;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * https://github.com/square/retrofit/issues/1554#issuecomment-178633697
 * https://github.com/square/retrofit/issues/1554#issuecomment-212941985
 */
public final class NullOnEmptyConverterFactory extends Converter.Factory {
    public NullOnEmptyConverterFactory() {
    }

    public static Converter.Factory create() {
        return new NullOnEmptyConverterFactory();
    }

    @Override
    public @Nullable
    Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                if (body.contentLength() == 0) {
                    return null;
                }
                return delegate.convert(body);
            }
        };
    }
}