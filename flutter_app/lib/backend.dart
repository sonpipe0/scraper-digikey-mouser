import 'dart:convert';
import 'package:http/http.dart' as http;

class SearchResult {
  final String stock;
  final String unitPriceCurrent;
  final String finalPriceCurrent;
  final String currentBreakPoint;
  final String unitPriceNext;
  final String finalPriceNext;
  final String nextBreakPoint;
  final String fullBoxUnitPrice;
  final String fullBoxFinalPrice;
  final String fullBoxUnitQty;
  final String manufacturer;

  SearchResult({
    required this.stock,
    required this.unitPriceCurrent,
    required this.finalPriceCurrent,
    required this.currentBreakPoint,
    required this.unitPriceNext,
    required this.finalPriceNext,
    required this.nextBreakPoint,
    required this.fullBoxUnitPrice,
    required this.fullBoxFinalPrice,
    required this.fullBoxUnitQty,
    required this.manufacturer
  });

  factory SearchResult.fromJson(Map<String, dynamic> json) {
    return SearchResult(
      stock: json['Stock'] ?? "Null",
      unitPriceCurrent: json['UnitPriceCurrent'] ?? "Null",
      finalPriceCurrent: json['FinalPriceCurrent'] ?? "Null",
      currentBreakPoint: json['CurrentBreakPoint'] ?? "Null",
      unitPriceNext: json['UnitPriceNext'] ?? "Null",
      finalPriceNext: json['FinalPriceNext'] ?? "Null",
      nextBreakPoint: json['NextBreakPoint'] ?? "Null",
      fullBoxUnitPrice: json['FullBoxUnitPrice'] ?? "Null",
      fullBoxFinalPrice: json['FullBoxFinalPrice'] ?? "Null",
      fullBoxUnitQty: json['FullBoxUnitQty'] ?? "Null",
      manufacturer: json['manufacturer'] ?? "Null",
    );
  }
}

class RestService {
  static const String baseUrl = 'http://localhost:8080';

  static Future<List<SearchResult?>?> searchData(String codeId,int qty) async {
    String url = '$baseUrl/search';

    Map<String, String> queryParams = {
      'codigoJotatec': codeId,
      'qty': qty.toString(),
    };

    String queryString = Uri(queryParameters: queryParams).query;
    url += '?$queryString';

    try {
      http.Response response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        List<dynamic> jsonResponse = jsonDecode(response.body);
        return jsonResponse.map((json) => SearchResult.fromJson(json)).toList();
      } else {
        return null;
      }
    } catch (e) {
      return [SearchResult(
        stock: "null",
        unitPriceCurrent: "null",
        finalPriceCurrent: "null",
        currentBreakPoint: "null",
        unitPriceNext: "null",
        finalPriceNext: "null",
        nextBreakPoint: "null",
        fullBoxUnitPrice: "null",
        fullBoxFinalPrice: "null",
        fullBoxUnitQty: "null",
        manufacturer: "null"
      )];
    }
  }
}


