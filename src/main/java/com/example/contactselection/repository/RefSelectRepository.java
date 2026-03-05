package com.example.contactselection.repository;

import com.example.contactselection.dto.in.RefSearchFormDto;
import com.example.contactselection.dto.out.RefSelectItemDto;
import com.example.contactselection.dto.out.RgonInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RefSelectRepository – Data access layer.
 *
 * SQL definitions in: resources/mappers/RefSelectMapper.xml
 */
@Mapper
public interface RefSelectRepository {

    /**
     * SQL: getRgonInfList
     * Lấy danh sách khu vực từ 地域M để render checkboxes.
     *
     * WHERE: DELETE_FLG = '0' AND RGON_USE_TYP = '1'
     * ORDER BY: DISP_ORDER ASC
     */
    List<RgonInfoDto> getRgonInfList();

    /**
     * Đếm tổng số kết quả tìm kiếm trước khi lấy data.
     * Business rule: count = 0 → NoResultException
     *                count > 80 → return warning flag
     *
     * @param form Điều kiện tìm kiếm
     * @return Tổng số kết quả
     */
    int countContacts(@Param("form") RefSearchFormDto form);

    /**
     * Lấy danh sách nơi liên hệ theo điều kiện tìm kiếm.
     *
     * JOINs:
     *   問合せ先_M → 都道府県_M (prefecture)
     *   問合せ先_M → 地域_M    (region)
     *   問合せ先_M → URL_M     (URL, GROUP_CONCAT with " / ")
     *
     * @param form Điều kiện tìm kiếm
     * @return Danh sách nơi liên hệ
     */
    List<RefSelectItemDto> searchContacts(@Param("form") RefSearchFormDto form);
}
