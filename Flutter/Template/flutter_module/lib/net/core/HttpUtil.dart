import 'package:common_utils/common_utils.dart';
import 'package:dio/dio.dart';

class HeadInterceptor extends Interceptor {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    var h = <String, dynamic>{};
    h["customHeadKey"] = "customHeadValue";
    options.headers.addAll(h);
    super.onRequest(options, handler);
  }
}

class HttpUtil {
  static final dio = Dio()
    ..interceptors.add(HeadInterceptor())
    ..interceptors.add(LogInterceptor(
        responseBody: true,
        logPrint: (log) {
          LogUtil.v(tag: "HttpLog", log);
        }));

  static Future<Object> get(String url) async {
    try {
      return await dio.get(url);
    } catch (e) {}
    return Future.value("");
  }

  static Future<Object> post(String url, String json) async {
    try {
      return await dio.post(url, data: json);
    } catch (e) {}
    return Future.value("");
  }
}
