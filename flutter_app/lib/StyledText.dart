import 'package:flutter/cupertino.dart';

class StyledText extends StatelessWidget{
  const StyledText({super.key,required this.color,required this.text,required this.fontWeight});

  final Color color;
  final String text;
  final FontWeight fontWeight;

  @override
  Widget build (BuildContext context){
    return Text(text,
      textAlign: TextAlign.center,
      style: TextStyle(
        color: color,
        fontWeight: fontWeight,
      ),
    );
  }
}