import 'package:json_annotation/json_annotation.dart';

@JsonSerializable(nullable: false)
class UserEntity{
  String name;
  int age;
  DateTime birth;

  UserEntity({this.name, this.age, this.birth});


}