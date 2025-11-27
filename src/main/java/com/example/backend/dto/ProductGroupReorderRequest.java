package com.example.backend.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** 並び替え用のシンプルなリクエストDTO（IDの配列のみ） */
@Getter @Setter
public class ProductGroupReorderRequest {
  // 例: [3,1,2] → 先頭から順に orderIndex=0,1,2 を付与
  private List<Long> orderedIds;
}
