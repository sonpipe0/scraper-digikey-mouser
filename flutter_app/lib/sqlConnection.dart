import 'package:mysql1/mysql1.dart';

class SqlConnectionExample {
  late MySqlConnection _connection;

  String idDigikey = 'codeDigikey';
  String idMouser = 'codeMouser';

  Future<List<Map<String, dynamic>>> connectToDatabase() async {
    final settings = ConnectionSettings(
      host: 'YourIpHost',
      port: 0000,
      user: 'Your User',
      password: 'YourPassword',
      db: 'Your Database',
    );

    try {
      _connection = await MySqlConnection.connect(settings);
      print('Connected to database');
      var result = await _connection.query(
          'SELECT Componente, CantidadPedida FROM Componentes WHERE  CantidadPedida > 0 ');

      List<Map<String, dynamic>> resultList = [];
      for(var row in result){
        Map<String,String> resultMap = {};
        resultMap['Codigo'] = row[0];
        resultMap['Cantidad'] = row[1].toString();
        resultList.add(resultMap);
      }

      return resultList ;
    } catch (e) {
      print('Error connecting to database: $e');
      return [];
    }
  }

  void deleteQtyes() async{
    final settings = ConnectionSettings(
      host: 'YourIpHost',
      port: 0000,
      user: 'Your User',
      password: 'YourPassword',
      db: 'Your Database',
    );

    try {
      _connection = await MySqlConnection.connect(settings);
      print('Connected to database');
      var result = await _connection.query(
          'UPDATE Componentes SET CantidadPedida = 0 WHERE CantidadPedida > 0');

    } catch (e) {
      print('Error connecting to database: $e');
    }
  }
}


