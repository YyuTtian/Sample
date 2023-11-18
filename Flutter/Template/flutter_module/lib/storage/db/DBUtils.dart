import 'core/AppDatabase.dart';
import 'core/Person.dart';

class DBUtils {
  static void test() async {
    final database = await $FloorAppDatabase.databaseBuilder('app_database.db').build();

    final personDao = database.personDao;

    final person = Person(1, '张三');
    await personDao.insertPerson(person);

    final result = await personDao.findPersonById(1);
    print("qglog  db result id=${result?.id}  name=${result?.name}");
  }
}
