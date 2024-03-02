import 'package:flutter/material.dart';

class CustomTextField extends StatefulWidget {
  final Color borderColor;

  const CustomTextField({super.key, required this.borderColor, required this.qty, required this.reloadTile});

  final int qty;
  final void Function(int) reloadTile;

  @override
  State<CustomTextField> createState(){
    return _CustomTextFieldState();
  }
}

class _CustomTextFieldState extends State<CustomTextField> {
  @override
  Widget build(BuildContext context) {
    return Container(
      width: 128,
      padding: const EdgeInsets.all(4),
      child: TextField(
        decoration: InputDecoration(
          enabledBorder: OutlineInputBorder(
            borderRadius: const BorderRadius.all(Radius.circular(8)),
            borderSide: BorderSide(color: widget.borderColor),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: const BorderRadius.all(Radius.circular(8)),
            borderSide: BorderSide(color: widget.borderColor),
          ),
          labelText: 'Qty',
          labelStyle: TextStyle(
            color: widget.borderColor,
          ),
          contentPadding: const EdgeInsets.only(left: 12, top: 20, bottom: 20),
        ),
        style: TextStyle(color: widget.borderColor),
        onSubmitted: (value) {
          int newValue = int.tryParse(value) ?? widget.qty;
          widget.reloadTile(newValue);
          },
      ),
    );
  }
}
