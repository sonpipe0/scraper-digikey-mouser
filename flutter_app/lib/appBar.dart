import 'package:flutter/material.dart';


class AppBarWidget extends StatelessWidget {
  const AppBarWidget({super.key, required this.data, required this.upDataChanged});

  final List<Map<String,dynamic>> data;
  final void Function() upDataChanged;


  @override
  Widget build(BuildContext context) {
    return  Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Image.asset(
          "images/jotatecIcon.png",
          height: 50,

        ),
        ElevatedButton.icon(
          onPressed: () {upDataChanged();
          },
          style: ElevatedButton.styleFrom(
            foregroundColor: Colors.white,
            backgroundColor: const Color.fromARGB(255, 180, 42, 27),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
          ),
          icon: const Icon(Icons.file_upload),
          label: const Text("Upload/Reload Data"),
        ),
        const Text("Stock Management"),
      ],
    );
  }

}


