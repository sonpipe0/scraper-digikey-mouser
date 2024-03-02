import 'package:flutter/material.dart';

class ResultRow {
  final List<Widget> cells;

  ResultRow({required this.cells});

  TableRow buildRow() {
    return TableRow(
      children: cells.map((cell) {
        return  Padding(
              padding: const EdgeInsets.symmetric(
                vertical: 4.0,
                horizontal: 8.0,
              ),
              child: cell,
            );
      }).toList(),
    );
  }
}