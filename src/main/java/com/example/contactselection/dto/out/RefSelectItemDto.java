package com.example.contactselection.dto.out;

import lombok.Data;

/**
 * 問合せ先選択 item DTO – One row in the contact list table.
 *
 * Maps to columns from Basic Design (問合せ先一覧領域):
 *   refCd       → 問合せ先コード
 *   refNm       → 問合せ先名称
 *   refKn       → 問合せ先名称カナ
 *   telno       → 電話番号
 *   prefecture  → 都道府県名
 *   rgonNm      → 地域名称
 *   urlDisplay  → URLアドレス (joined by " / " when multiple)
 */
@Data
public class RefSelectItemDto {

    /** Mã nơi liên hệ */
    private String refCd;

    /** Tên nơi liên hệ */
    private String refNm;

    /** Tên kana */
    private String refKn;

    /** Số điện thoại */
    private String telno;

    /** Tỉnh/thành (都道府県名) */
    private String prefecture;

    /** Khu vực (地域名称) */
    private String rgonNm;

    /**
     * URL hiển thị – nếu nhiều URL thì nối bằng " / "
     * Ví dụ: "https://a.com / https://b.com"
     */
    private String urlDisplay;
}
