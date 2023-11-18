import 'package:floor/floor.dart';

import 'Person.dart';

@dao
abstract class PersonDao {
  @insert
  Future<void> insertPerson(Person person);

  @delete
  Future<void> deletePerson(Person person);

  @update
  Future<void> updatePerson(Person person);

  @Query('SELECT * FROM Person')
  Future<List<Person>> findAllPerson();

  @Query('SELECT name FROM Person')
  Future<List<String>> findAllPersonName();

  @Query('SELECT * FROM Person WHERE id = :id')
  Future<Person?> findPersonById(int id);
}
