
abstract class Db{   //当做接口   接口：就是约定 、规范
    late String uri;      //数据库的连接地址
    add(String data);
    save();
    delete();
}