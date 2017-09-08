package com.huahan.hhbaseutils;

import android.text.TextUtils;
import android.util.Log;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.huahan.hhbaseutils.model.HHAbsNameValueModel;
import com.huahan.hhbaseutils.model.HHBasicNameValuePair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 获取网络数据的工具类<br/>
 * 该工具类整合了以前的get、post、webservice方法，已实现统一的调用。<br/>
 * 方法名称的说明:以"_B"结尾的方法是公司专用的方法；以"_B_D"结尾的方法在以后可能会删除。其他的方法都是通用的方法
 *
 * @author yuan
 */
@SuppressWarnings("deprecation")
public class HHWebDataUtils {
    private static final String tag = HHWebDataUtils.class.getSimpleName();

    /**
     * 发送一个SOAP的webservice请求
     *
     * @param rpc        SoapObject请求
     * @param url        WSDL地址，也就是接口地址
     * @param nameSplace 命名空间
     * @param method     请求的方法的名称
     * @return 请求失败的情况下返回null
     */
    private static String sendSoapRequest(SoapObject rpc, String url, String nameSplace, String method) {
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(url, 5000);
            ht.debug = true;
            ht.call(nameSplace + method, envelope);
            return envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(tag, method + "获取数据异常===" + e.getMessage() + "==" + e.getClass());
            return null;
        }
    }

    /**
     * 发送一个Get请求
     *
     * @param requestUrl 请求的地址
     * @return
     */
    public static String sendGetRequest(String requestUrl) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            InputStream inputStream = conn.getInputStream();
            return HHStreamUtils.convertStreamToString(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            HHLog.i(tag, "sendGetRequest", e);
        }
        return null;
    }

    // 创建HttpClient
    private static HttpClient createHttpClient() {
        HttpParams mDefaultHttpParams = new BasicHttpParams();
        // 设置连接超时
        HttpConnectionParams.setConnectionTimeout(mDefaultHttpParams, 10000);
        // 设置请求超时
        HttpConnectionParams.setSoTimeout(mDefaultHttpParams, 10000);
        HttpConnectionParams.setTcpNoDelay(mDefaultHttpParams, true);
        HttpProtocolParams.setVersion(mDefaultHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(mDefaultHttpParams, HTTP.UTF_8);
        // 持续握手
        HttpProtocolParams.setUseExpectContinue(mDefaultHttpParams, true);
        HttpClient mHttpClient = new DefaultHttpClient(mDefaultHttpParams);
        return mHttpClient;
    }

    /**
     * 发送post请求
     *
     * @param requestUrl 请求的地址
     * @param param      发送的参数
     */
    public static String sendPostRequest(String requestUrl, Map<String, String> param) {
        return sendPostRequest(requestUrl, param, null);
    }

    /**
     * 发送Post请求
     *
     * @param requestUrl 请求的地址
     * @param param      请求的参数
     * @param headers    请求头
     * @return
     */
    private static String sendPostRequest(String requestUrl, Map<String, String> param, Map<String, String> headers) {
        String paramInfo = null;
        if (param != null && !param.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            Set<String> keySet = param.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = param.get(key);
                HHLog.i(tag, "key=" + key + ",value=" + value);
                builder.append(key);
                builder.append("=");
                builder.append(value);
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);
            paramInfo = builder.toString();
        }
        return sendBasePostRequest(requestUrl, paramInfo, headers);
    }

    /**
     * 发送Post请求
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param headerMap  请求的头信息
     * @return
     */
    private static String sendPostRequest_Client(String requestUrl, Map<String, String> paramMap, Map<String, String> headerMap) {
        return sendPostRequest_Client(requestUrl, paramMap, null, headerMap);
    }

    /**
     * 发送Post请求
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param fileMap    文件
     * @param headerMap  头信息
     * @return
     */
    private static String sendPostRequest_Client(String requestUrl, Map<String, String> paramMap, Map<String, String> fileMap, Map<String, String> headerMap) {
        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(requestUrl);
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> keySet = headerMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                httpPost.addHeader(key, headerMap.get(key));
            }
        }
        try {
            List<Part> list = new ArrayList<Part>();
            if (paramMap != null && !paramMap.isEmpty()) {
                Set<String> keySet = paramMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    StringPart part = new StringPart(key, paramMap.get(key));
                    list.add(part);
                }
            }
            if (fileMap != null && !fileMap.isEmpty()) {
                Set<String> keySet = fileMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    FilePart part = new FilePart(key, new File(fileMap.get(key)));
                    list.add(part);
                }
            }
            if (list.size() != 0) {
                Part[] array = new Part[list.size()];
                list.toArray(array);
                MultipartEntity entity = new MultipartEntity(array);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());
            HHLog.i(tag, "sendPostRequest_B_D:" + serverResponse);
        } catch (Exception e) {
            e.printStackTrace();
            HHLog.i(tag, "sendPostRequest_B_D", e);
        }
        return serverResponse;
    }

    /**
     * 发送post请求获取数据，使用的是公司定义好的格式
     *
     * @param requestUrl 请求的地址
     * @param param      请求的参数
     * @return 请求失败的情况下返回null
     */
    private static String sendPostRequest_B(String requestUrl, Map<String, String> param) {
        return sendPostRequestWithArray_B(requestUrl, param, null);
    }

    /**
     * 发送Post请求获取数据，使用的是公司定义好的格式
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param arrayMap   请求中带有数组的参数
     * @return 请求失败的返回null
     */
    private static String sendPostRequestWithArray_B(String requestUrl, Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap) {

        return sendPostRequestWithArray_B(requestUrl, paramMap, arrayMap, null);
    }

    /**
     * 发送Post请求获取数据，使用的是公司定义好的格式
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param arrayMap   请求中带有数组的参数
     * @param headers    请求头信息
     * @return 请求失败的返回null
     */
    private static String sendPostRequestWithArray_B(String requestUrl, Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap, Map<String, String> headers) {
        String result = sendBasePostRequest(requestUrl, getPostRequestParamString(paramMap, arrayMap), headers);
        HHLog.i(tag, "sendPostRequestWithArray_B:" + result);
        return result;
    }

    /**
     * 创建HttpGet 请求
     *
     * @param url 请求地址
     */
    public static String useHttpClientGet(String url) {
        String result = "";
        HttpGet mHttpGet = new HttpGet(url);
        mHttpGet.addHeader("Connection", "Keep-Alive");
        try {
            HttpClient mHttpClient = createHttpClient();
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            int code = mHttpResponse.getStatusLine().getStatusCode();
            HHLog.i(tag, "useHttpClientGet_code== " + code);
            if (null != mHttpEntity) {
                InputStream mInputStream = mHttpEntity.getContent();
                result = HHStreamUtils.convertStreamToString(mInputStream);
                mInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(tag, "useHttpClientGet_result== " + result);
        return result;
    }

    /**
     * 创建HttpPost 请求
     *
     * @param url 请求地址
     * @param map 请求参数
     */
    public static String useHttpClientPost(String url, Map<String, String> map) {
        String result = "";
        HttpPost mHttpPost = new HttpPost(url);
        mHttpPost.addHeader("Connection", "Keep-Alive");
        try {
            HttpClient mHttpClient = createHttpClient();
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            // 要传递的参数
            for (Map.Entry<String, String> entry : map.entrySet()) {
                postParams.add(new BasicNameValuePair(entry.getKey(), entry
                        .getValue()));
                HHLog.i(tag, "key:" + entry.getKey() + ",value:" + entry.getValue());
            }
            mHttpPost.setEntity(new UrlEncodedFormEntity(postParams));
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            int code = mHttpResponse.getStatusLine().getStatusCode();
            HHLog.i(tag, "useHttpClientPost_code== " + code);
            if (null != mHttpEntity) {
                InputStream mInputStream = mHttpEntity.getContent();
                result = HHStreamUtils.convertStreamToString(mInputStream);
                mInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(tag, "useHttpClientPost_result== " + result);
        return result;
    }

    /**
     * 发送Post请求
     *
     * @param requestUrl 请求地址
     * @param paramInfo  请求的参数
     * @param headers    添加的http头部信息
     * @return
     */
    private static String sendBasePostRequest(String requestUrl, String paramInfo, Map<String, String> headers) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            if (headers != null && !headers.isEmpty()) {
                Set<String> keySet = headers.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    conn.setRequestProperty(key, headers.get(key));
                    HHLog.i(tag, "header:" + key + ",value:" + headers.get(key));
                }
            } else {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            }
            conn.setDoInput(true);
            conn.setUseCaches(false);
            if (!TextUtils.isEmpty(paramInfo)) {
                conn.setDoOutput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(paramInfo.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();
            }
            HHLog.i(tag, "sendPostRequest==code==" + conn.getResponseCode());
            InputStream is = conn.getInputStream();
            return HHStreamUtils.convertStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
            HHLog.i(tag, "sendPostRequest", e);
        }
        return null;
    }

    /**
     * 发送Post请求
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param arrayMap   请求的参数中的数组
     * @param fileName   文件的名称，可以为null
     * @param filePath   文件的地址
     * @return
     */
    private static String sendPostRequest_B_D(String requestUrl, Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap, String fileName, String filePath) {
        Map<String, String> fileMap = new HashMap<String, String>();
        if (TextUtils.isEmpty(fileName)) {
            fileName = "file0";
        }
        fileMap.put(fileName, filePath);
        return sendPostRequest_B_D(requestUrl, paramMap, arrayMap, fileMap);
    }

    /**
     * 发送Post请求
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param arrayMap   请求的参数中的数组
     * @param files      请求的携带的文件
     * @return
     */
    private static String sendPostRequest_B_D(String requestUrl, Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap, List<String> files) {
        Map<String, String> fileMap = new HashMap<String, String>();
        if (files != null && files.size() != 0) {
            for (int i = 0; i < files.size(); i++) {
                fileMap.put("file" + i, files.get(i));
            }
        }
        return sendPostRequest_B_D(requestUrl, paramMap, arrayMap, fileMap);
    }

    /**
     * 发送Post请求获取数据
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param arrayMap   请求的参数中的数据
     * @param fileMap    参数中的文件，map中的key为文件对应的键值
     * @return
     */
    private static String sendPostRequest_B_D(String requestUrl, Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap, Map<String, String> fileMap) {
        return sendPostRequest_B_D(requestUrl, paramMap, arrayMap, fileMap, null);
    }

    /**
     * 发送Post请求获取数据
     *
     * @param requestUrl 请求的地址
     * @param paramMap   请求的参数
     * @param arrayMap   请求的参数中的数据
     * @param fileMap    参数中的文件，map中的key为文件对应的键值
     * @param headers    参数的头信息
     * @return
     */
    private static String sendPostRequest_B_D(String requestUrl, Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap, Map<String, String> fileMap, Map<String, String> headers) {
        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(requestUrl);
        if (headers != null && !headers.isEmpty()) {
            Set<String> keySet = headers.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                httpPost.addHeader(key, headers.get(key));
            }
        }
        String paramInfo = getPostRequestParamString(paramMap, arrayMap, false);
        try {
            List<Part> list = new ArrayList<Part>();
            if (!TextUtils.isEmpty(paramInfo)) {
                list.add(new StringPart("para", paramInfo));
            }
            if (fileMap != null && !fileMap.isEmpty()) {
                Set<String> keySet = fileMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = fileMap.get(key);
                    list.add(new FilePart(key, new File(value)));
                }
            }
            if (list.size() != 0) {
                Part[] parts = new Part[list.size()];
                list.toArray(parts);
                MultipartEntity multipartContent = new MultipartEntity(parts);
                httpPost.setEntity(multipartContent);
            }
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());
            HHLog.i(tag, "sendPostRequest_B_D:" + serverResponse);
            serverResponse = HHEncryptUtils.decodeAES_B(serverResponse);
        } catch (Exception e) {
            e.printStackTrace();
            HHLog.i(tag, "sendPostRequest_B_D", e);
        }
        return serverResponse;
    }

    /**
     * 获取发送post请求的时候发送的数据，使用的是公司默认的实现
     *
     * @param paramMap 参数的map
     * @param arrayMap 参数中含有数据的map
     * @param addPara  是否添加para参数
     * @return 如果两个map都是null，则返回null
     */
    private static String getPostRequestParamString(Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap, boolean addPara) {

        if (paramMap != null || arrayMap != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            if (paramMap != null && !paramMap.isEmpty()) {
                Set<String> keySet = paramMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    HHLog.i(tag, "key:" + key + ",value:" + paramMap.get(key));
                    builder.append("\"" + key + "\":\"" + paramMap.get(key) + "\",");
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            if (arrayMap != null && !arrayMap.isEmpty()) {
                builder.append(",");
                Set<String> keySet = arrayMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    builder.append("\"" + next + "\":[");
                    List<? extends HHAbsNameValueModel> list = arrayMap.get(next);
                    if (list != null && list.size() != 0) {

                        for (HHAbsNameValueModel absNameValueModel : list) {

                            List<HHBasicNameValuePair> nameValueList = absNameValueModel.getNameValueList();
                            if (nameValueList != null && nameValueList.size() != 0) {
                                builder.append("{");
                                for (HHBasicNameValuePair basicNameValuePair : nameValueList) {
                                    builder.append("\"" + basicNameValuePair.getName() + "\":\"" + basicNameValuePair.getValue() + "\",");
                                }
                                builder.deleteCharAt(builder.length() - 1);
                                builder.append("}");
                                builder.append(",");
                            }

                        }
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    builder.append("],");
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("}");
            String paramInfo = builder.toString();
            HHLog.i(tag, "getPostRequestParamString:" + paramInfo);
            return paramInfo;
        }
        return null;
    }

    /**
     * 获取发送的post请求的参数
     *
     * @param paramMap 发送的请求的参数（不含有数据参数）
     * @param arrayMap 发送的请求的参数中含有数据的参数
     * @return
     */
    private static String getPostRequestParamString(Map<String, String> paramMap, Map<String, List<? extends HHAbsNameValueModel>> arrayMap) {
        return getPostRequestParamString(paramMap, arrayMap, false);
    }

    /**
     * 发送post请求获取数据
     *
     * @param requestUrl 请求地址
     * @param paramMap   请求参数
     * @param fileMap    参数中的文件，map中的key为文件对应的键值
     * @return
     */
    private static String sendPostRequest_B_D(String requestUrl, Map<String, String> paramMap, Map<String, String> fileMap) {
        return sendPostRequest_B_D(requestUrl, paramMap, null, fileMap, null);
    }
}
