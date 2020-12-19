class Log {

  static int _limitLength = 800;

  //一般信息
  static void i(String tag, String content){
    printLog("[普通]", tag, content);
  }

  //调试
  static void d(String tag, String content){
    printLog("[调试]", tag, content);
  }

  //警告
  static void w(String tag, String content){
    printLog("[警告]", tag, content);
  }

  //错误
  static void e(String tag, String content){
    printLog("[错误]", tag, content);
  }


  static void printLog(String type, String tag, String content){
    type = (type != null && type.isNotEmpty) ? type : "";
    tag = (tag != null && tag.isNotEmpty) ? "/" + tag + ":" : "";

    String flag = type + tag;

    //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
    //  把4*1024的MAX字节打印长度改为2001字符数
    int maxLength = 800 - flag.length;
    // //大于4000时
    while (content.length > maxLength) {
      print(flag + content.substring(0, maxLength));
      content = content.substring(maxLength);
    }
    //剩余部分
    print(flag + content);
  }
}