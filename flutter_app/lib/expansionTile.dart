import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_application/CustomTextField.dart';
import 'package:flutter_application/StyledText.dart';
import 'package:flutter_application/backend.dart';
import 'package:flutter_application/resultRow.dart';

class CartaInformacion extends StatefulWidget {
  final int index;
  final List<Map<String,dynamic>> csvData;

  const CartaInformacion(this.index, {super.key, required this.csvData});

  @override
  _CartaInformacionState createState() => _CartaInformacionState();
}

class _CartaInformacionState extends State<CartaInformacion> with AutomaticKeepAliveClientMixin {
  final Color backgroundColor1 = const Color.fromARGB(255, 240, 243, 245);
  final Color backgroundColor2 = const Color.fromARGB(255, 253, 253, 253);
  final Color textColor = const Color.fromARGB(255, 79, 79, 79);
  final FontWeight parameter = FontWeight.w500;
  final Color? _borderColor = Colors.grey[800];
  int _qty = -1;



  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    void reloadTile(int newQty){
      setState(() {
        _qty = newQty;
      });
    }
    var parameterRow = ResultRow(
      cells: [
        StyledText(color: textColor, text: 'Qty', fontWeight: parameter,),
        StyledText(color: textColor, text: 'Supplier', fontWeight: parameter),
        StyledText(color: textColor, text: 'Stock', fontWeight: parameter,),
        StyledText(color: textColor, text: 'Break', fontWeight: parameter,),
        StyledText(color: textColor, text: 'UntPriceCurnt', fontWeight: parameter),
        StyledText(color: textColor, text: 'FnlPriceCurnt', fontWeight: parameter,),
        StyledText(color: textColor, text: 'NxtBreak', fontWeight: parameter,),
        StyledText(color: textColor, text: 'UntPriceNxt', fontWeight: parameter,),
        StyledText(color: textColor, text: 'FnlPriceNxt', fontWeight: parameter,),
        Container(
          decoration: BoxDecoration(
              color: backgroundColor1
          ),
          child: const Icon(
            CupertinoIcons.cube_box_fill,
            size: 20.0,
            color: Color.fromARGB(255, 79, 79, 79),

          ),
        ),
        StyledText(color: textColor, text: 'QtyFullBox', fontWeight: parameter,),
        StyledText(color: textColor, text: 'PriceFullBox', fontWeight: parameter,),
      ],
    );


    FutureBuilder buildTile({int qty = -1}){
      if (qty == -1) {
        qty = int.parse(widget.csvData[widget.index]['Cantidad']);
      }
      return FutureBuilder<List<List<SearchResult?>?>>(
        future: Future.wait([
          RestService.searchData(widget.csvData[widget.index]['Codigo'],qty),
        ]),
        builder: (context, AsyncSnapshot<List<List<SearchResult?>?>> snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }
          if (snapshot.hasError) {
            return Text('Error: ${snapshot.error}');
          }
          if (!snapshot.hasData) {
            return const Text('No data available.');
          }

          List<TableRow> resultRows = [];
          resultRows.add(parameterRow.buildRow());

          for (int i = 0; i < snapshot.data!.length; i++) {
            List<SearchResult?>? searchData = snapshot.data![i];
            if (searchData != null && searchData.isNotEmpty) {
              for (int j = 0; j < searchData.length; j++) {
                SearchResult? data = searchData[j];
                if (data != null) {
                  String manufacturer = data.manufacturer;
                  resultRows.add(ResultRow(
                    cells: [
                      Text(qty.toString(), textAlign: TextAlign.center),
                      Text(manufacturer, textAlign: TextAlign.center),
                      Text(data.stock, textAlign: TextAlign.center, style: TextStyle(backgroundColor: checkQty(qty,int.parse(data.stock)))),
                      Text(data.currentBreakPoint, textAlign: TextAlign.center),
                      Text(data.unitPriceCurrent, textAlign: TextAlign.center),
                      Text(data.finalPriceCurrent, textAlign: TextAlign.center),
                      Text(data.nextBreakPoint, textAlign: TextAlign.center),
                      Text(data.unitPriceNext, textAlign: TextAlign.center),
                      Text(data.finalPriceNext, textAlign: TextAlign.center),
                      Text(data.fullBoxUnitPrice, textAlign: TextAlign.center),
                      Text(data.fullBoxUnitQty, textAlign: TextAlign.center),
                      Text(data.fullBoxFinalPrice, textAlign: TextAlign.center),
                    ],
                  ).buildRow());
                }
              }
            }
          }

          return resultRows.length > 1 ? Container(
            margin:  EdgeInsets.symmetric(
              vertical: 12,
              horizontal: 8/1536*screenWidth,
            ),
            child: ExpansionTile(
              backgroundColor: const Color.fromARGB(255, 245, 245, 245),
              title: Text(widget.csvData[widget.index]['Codigo']),
              trailing: CustomTextField(borderColor: _borderColor!,qty: _qty, reloadTile: reloadTile),
              childrenPadding: const EdgeInsets.all(12),
              collapsedShape: const RoundedRectangleBorder(
                borderRadius: BorderRadius.all(Radius.circular(8)),
                side: BorderSide(
                  color: Color.fromARGB(255, 234, 234, 234),
                  width: 1,
                ),
              ),
              collapsedBackgroundColor: const Color.fromARGB(255, 84, 127, 169),
              collapsedTextColor: Colors.white,
              shape: const RoundedRectangleBorder(
                borderRadius: BorderRadius.all(Radius.circular(8)),
                side: BorderSide(
                  color: Color.fromARGB(255, 234, 234, 234),
                  width: 1,
                ),
              ),
              children: <Widget>[
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: Colors.grey),
                    ),
                    child: Table(
                      defaultVerticalAlignment: TableCellVerticalAlignment.middle,
                      border: TableBorder.symmetric(
                        inside: const BorderSide(
                          color: Colors.grey,
                        ),
                      ),
                      columnWidths:  <int, TableColumnWidth>{
                        0: FixedColumnWidth(75.0/1536*screenWidth),
                        1: FixedColumnWidth(120.0/1536*screenWidth),
                        2: FixedColumnWidth(80.0/1536*screenWidth),
                        3: FixedColumnWidth(160.0/1536*screenWidth),
                        4: FixedColumnWidth(140.0/1536*screenWidth),
                        5: FixedColumnWidth(150.0/1536*screenWidth),
                        6: FixedColumnWidth(145.0/1536*screenWidth),
                        7: FixedColumnWidth(135.0/1536*screenWidth),
                        8: FixedColumnWidth(140.0/1536*screenWidth),
                        9: FixedColumnWidth(100.0/1536*screenWidth),
                        10: FixedColumnWidth(110.0/1536*screenWidth),
                        11: FixedColumnWidth(120.0/1536*screenWidth),
                      },
                      children: resultRows,
                    ),
                  ),
                ),
              ],
            ),
          ) : const SizedBox(height: 0,);
        },
      );
    }
    super.build(context);// Important for AutomaticKeepAliveClientMixin




    return buildTile(qty: _qty);
  }

  @override
  bool get wantKeepAlive => true;

  Color? checkQty(int qty,int resultStock) {
    if (resultStock <= qty) return const Color.fromARGB(255, 255, 67, 67);
    if(resultStock <= 2 * qty) {
      return const Color.fromARGB(255, 232, 225, 125);
    } else {
      return null;
    }

  }


}
