import 'dart:collection';

import 'package:flutter/cupertino.dart';
import 'package:wechat/entity/CartEntity.dart';

class CartModel extends ChangeNotifier {
  List<CartEntity> _list = [];

  CartModel();

  CartModel.load(this._list);

  UnmodifiableListView<CartEntity> get list => UnmodifiableListView(_list);

  double get totalMoney =>
      _list.fold(0, (value, item) {
        return value + item.num * item.price;
      });

  int get cartNum => _list.length;

  int getItemCartNum(int index) {
    if(_list!= null && _list.length > index) {
      return _list[index].num;
    }return 1;

  }

  void add(CartEntity item) {
    if(!_list.contains(item)) {
      _list.add(item);
    }else {
      item.num++;
    }
    notifyListeners();
  }

  void clear() {
    _list.clear();
    notifyListeners();
  }
}
