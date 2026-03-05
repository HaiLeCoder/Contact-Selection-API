package com.example.contactselection.dto.in;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

/**
 * Request DTO: Contact Search (問合せ先検索リクエスト)
 *
 * Business rules from Basic Design (画面項目定義):
 *   - txtRefNm  : max 100, full/half-width, prefix search
 *   - txtRefKn  : max 200, full/half-width, prefix search
 *   - telno     : max 13,  half-width only, prefix search
 *   - urlSearch : max 256, half-width, partial search
 *   - rgonCdList: list of region codes (from 地域M master)
 */
@Data
public class RefSearchFormDto {

    /** 問合せ先名称 – Tên nơi liên hệ (prefix search) */
    @Size(max = 100, message = "Tên nơi liên hệ tối đa 100 ký tự")
    private String txtRefNm;

    /** 問合せ先名称カナ – Tên kana (prefix search) */
    @Size(max = 200, message = "Tên kana tối đa 200 ký tự")
    private String txtRefKn;

    /** 電話番号 – Số điện thoại (prefix search, half-width only) */
    @Size(max = 13, message = "Số điện thoại tối đa 13 ký tự")
    @Pattern(regexp = "^[0-9\\-]*$",
             message = "Số điện thoại chỉ được nhập chữ số và dấu -")
    private String telno;

    /** URLアドレス – URL (partial search) */
    @Size(max = 256, message = "URL tối đa 256 ký tự")
    private String urlSearch;

    /** 地域コードリスト – Danh sách mã khu vực được chọn */
    private List<Integer> rgonCdList;

    /**
     * 問合せ先選択の前画面
     * 0 = Màn hình khác  →  chỉ được chọn 1 record
     * 1 = 一括追加       →  có thể chọn nhiều record
     */
    @NotNull(message = "kindRef là bắt buộc")
    @Min(0) @Max(1)
    private Integer kindRef;

    /**
     * Client đã xác nhận > 80 kết quả sẽ hiển thị
     * false = chưa xác nhận (server trả cảnh báo nếu > 80)
     * true  = đã xác nhận (server trả toàn bộ danh sách)
     */
    private boolean confirmed = false;
}
