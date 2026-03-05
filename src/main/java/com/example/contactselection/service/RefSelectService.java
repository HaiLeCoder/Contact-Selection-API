package com.example.contactselection.service;

import com.example.contactselection.dto.in.RefLoadFormDto;
import com.example.contactselection.dto.in.RefSearchFormDto;
import com.example.contactselection.dto.out.LoadOutDto;
import com.example.contactselection.dto.out.RefSearchOutDto;
import com.example.contactselection.dto.out.RefSelectItemDto;
import com.example.contactselection.dto.out.RgonInfoDto;
import com.example.contactselection.exception.NoResultException;
import com.example.contactselection.repository.RefSelectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RefSelectService – Business Logic Layer.
 *
 * Physical name: (per server process definition)
 *   load()   → 画面初期表示処理
 *   search() → 問合せ先検索処理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefSelectService {

    private final RefSelectRepository refSelectRepository;

    /**
     * Business rule limit from design document (defined externally / configurable).
     * Document: "Nếu kết quả > 80 → hiển thị dialog xác nhận"
     */
    private static final int MAX_RESULTS_WITHOUT_CONFIRMATION = 80;

    // ─── PUBLIC METHODS ───────────────────────────────────────────────────────

    /**
     * 画面初期表示処理 (Initial Display)
     *
     * Business logic:
     *   1. Lấy danh sách 地域M (SQL: getRgonInfList)
     *   2. Set vào response DTO
     *
     * @param form Request từ client (kindRef để biết màn hình gọi)
     * @return LoadOutDto chứa danh sách khu vực
     */
    public LoadOutDto load(RefLoadFormDto form) {
        log.debug("[RefSelectService.load] kindRef={}", form.getKindRef());

        List<RgonInfoDto> rgonList = refSelectRepository.getRgonInfList();

        LoadOutDto dto = new LoadOutDto();
        dto.setRgonInfoList(rgonList);

        log.debug("[RefSelectService.load] Loaded {} regions", rgonList.size());
        return dto;
    }

    /**
     * 問合せ先検索処理 (Contact Search)
     *
     * Business logic (per Client Process Definition):
     *   Step 1: Đếm số kết quả
     *   Step 2: Nếu = 0  → throw NoResultException (MSG_NO_RESULT)
     *   Step 3: Nếu > 80 AND chưa xác nhận → trả needsConfirmation=true
     *   Step 4: Nếu > 80 AND đã xác nhận  → lấy data bình thường
     *   Step 5: Bình thường → lấy và trả danh sách
     *
     * @param form Điều kiện tìm kiếm (đã validate ở Controller)
     * @return RefSearchOutDto (danh sách hoặc flag cảnh báo)
     */
    public RefSearchOutDto search(RefSearchFormDto form) {
        log.debug("[RefSelectService.search] conditions: name={}, kana={}, tel={}, url={}, regions={}",
                form.getTxtRefNm(), form.getTxtRefKn(), form.getTelno(),
                form.getUrlSearch(), form.getRgonCdList());

        // Step 1: Đếm số kết quả
        int count = refSelectRepository.countContacts(form);
        log.debug("[RefSelectService.search] totalCount={}", count);

        RefSearchOutDto result = new RefSearchOutDto();
        result.setTotalCount(count);

        // Step 2: Không có kết quả → lỗi
        if (count == 0) {
            throw new NoResultException("Không có giá trị phù hợp với điều kiện tìm kiếm.");
        }

        // Step 3: Vượt quá giới hạn và chưa xác nhận → báo client hỏi xác nhận
        if (count > MAX_RESULTS_WITHOUT_CONFIRMATION && !form.isConfirmed()) {
            log.info("[RefSelectService.search] Over limit ({} > {}), needs confirmation",
                    count, MAX_RESULTS_WITHOUT_CONFIRMATION);
            result.setNeedsConfirmation(true);
            return result;
        }

        // Step 4 & 5: Lấy danh sách đầy đủ
        List<RefSelectItemDto> list = refSelectRepository.searchContacts(form);
        result.setRefList(list);

        log.debug("[RefSelectService.search] Returning {} contacts", list.size());
        return result;
    }
}
