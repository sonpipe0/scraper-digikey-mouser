
import 'package:flutter/material.dart';
import 'package:flutter_application/appBar.dart';
import 'package:flutter_application/expansionTile.dart';
import 'package:flutter_application/sqlConnection.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: ExpandableList(),
    );
  }
}

class ExpandableList extends StatefulWidget {
  const ExpandableList({super.key});

  @override
  State<StatefulWidget> createState() {
    return _MainPageState();
  }
}

class _MainPageState extends State<ExpandableList> with WidgetsBindingObserver {
  SqlConnectionExample sqlconnection = SqlConnectionExample();
  List<Map<String, dynamic>> csvData = [];
  bool stopLoading = false;
  int deleted = 0;

  void resetList() {
    sqlconnection.deleteQtyes();
    setState(() {
      csvData = [];
    });
  }

  void setcsvData() async {
    csvData = await sqlconnection.connectToDatabase();
    stopLoading = false;
    setState(() {});
  }

  @override
  void initState() {
    setcsvData();
    super.initState();
  }



  void setStopLoading() {
    setState(() {
      stopLoading = true;
    });
  }

  @override
  Widget build(BuildContext context) {

    double screenWidth = MediaQuery.of(context).size.width;
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: AppBarWidget(data: csvData, upDataChanged: setcsvData),
        backgroundColor: const Color.fromARGB(255, 253, 253, 253),
        elevation: 1.5,
        shadowColor: Colors.black,
      ),
      body: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            child: !stopLoading
                ? ListView.builder(
              padding: const EdgeInsets.all(4),
              itemCount: csvData.length,
              itemBuilder: (context, index) {
                return CartaInformacion(
                  index,
                  csvData: csvData,
                  key: UniqueKey(),
                );
              },
            )
                : const SizedBox(height: 10),
          ),
        ],
      ),
      floatingActionButton: Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              SizedBox(width: 32/1536*screenWidth),
              FloatingActionButton(
                onPressed: setStopLoading,
                child: const Icon(Icons.close),
              ),
            ],
          ),
          FloatingActionButton(
            onPressed: resetList,
            child: const Icon(Icons.delete_rounded),
          ),
        ],
      ),
    );
  }
}
