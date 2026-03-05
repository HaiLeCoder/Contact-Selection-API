package com.example.contactselection.dto.out;

import lombok.Data;
import java.util.List;

/**
 * Response DTO: Initial Display (問合せ先選択初期表示レスポンス)
 * Physical class name: LoadOutDto (per server process definition)
 *
 * Returns region list for rendering checkboxes.
 */
@Data
public class LoadOutDto {

    /** 地域チェックリスト – Danh sách khu vực để render checkbox */
    private List<RgonInfoDto> rgonInfoList;
}
